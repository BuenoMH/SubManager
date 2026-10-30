package submanager.service;

import submanager.interfaces.AssinaturaRepository;
import submanager.interfaces.CalculadorDesconto;
import submanager.interfaces.Notificador;
import submanager.model.Assinatura;
import submanager.model.Cliente;
import submanager.model.Cupom;
import submanager.model.Pagamento;
import submanager.model.Plano;

import java.util.Objects;

// SRP: coordena o ciclo de vida das assinaturas. Não calcula desconto, não cobra, não envia mensagem.
// DIP: depende de CalculadoraDesconto, Notificador, PagamentoService e AssinaturaRepository, injetados pelo construtor.
// OCP: novo desconto/canal/meio de pagamento = nova implementação; esta classe não muda.
public class AssinaturaService {

    private final PagamentoService pagamentoService;
    private final Notificador notificador;
    private final CalculadorDesconto calculadoraDesconto;
    private final AssinaturaRepository repositorio;

    public AssinaturaService(PagamentoService pagamentoService,
                             Notificador notificador,
                             CalculadorDesconto calculadoraDesconto,
                             AssinaturaRepository repositorio) {
        this.pagamentoService = Objects.requireNonNull(pagamentoService, "pagamentoService é obrigatório");
        this.notificador = Objects.requireNonNull(notificador, "notificador é obrigatório");
        this.calculadoraDesconto = Objects.requireNonNull(calculadoraDesconto, "calculadoraDesconto é obrigatória");
        this.repositorio = Objects.requireNonNull(repositorio, "repositorio é obrigatório");
    }

    // desconto -> valor final -> cobrança. A assinatura nasce PENDENTE e só é ativada quando o
    // pagamento for confirmado (confirmarPagamento).
    public Assinatura criarAssinatura(Cliente cliente, Plano plano, Cupom cupom) {
        Assinatura assinatura = new Assinatura(repositorio.proximoId(), cliente, plano); // valida cliente/plano; nasce PENDENTE
        repositorio.salvar(assinatura);

        double valorFinal = calcularValorFinal(plano, cupom);
        Pagamento pagamento = pagamentoService.processarPagamento(assinatura, valorFinal);

        if (pagamento.isPendente()) {
            notificador.enviar(
                    String.format("Olá %s! Recebemos seu pedido de assinatura do plano %s (R$ %.2f). "
                                    + "Ela será ativada quando o pagamento #%d for confirmado.",
                            cliente.getNome(), plano.getNome(), valorFinal, pagamento.getId()),
                    cliente);
        }

        return assinatura;
    }

    public Assinatura criarAssinatura(Cliente cliente, Plano plano) {
        return criarAssinatura(cliente, plano, null);
    }

    // Pede a renovação por mais um período, aplicando o desconto da estratégia ATUAL.
    // Gera um pagamento PENDENTE: a renovação só vale quando ele for confirmado.
    // Valida ANTES de cobrar: uma assinatura cancelada/pendente nunca gera cobrança.
    public Pagamento renovarAssinatura(Assinatura assinatura, Cupom cupom) {
        assinatura.validarRenovacao();

        double valorFinal = calcularValorFinal(assinatura.getPlano(), cupom);
        Pagamento pagamento = pagamentoService.processarPagamento(assinatura, valorFinal);

        if (pagamento.isPendente()) {
            notificador.enviar(
                    String.format("Olá %s! Recebemos seu pedido de renovação do plano %s (R$ %.2f). "
                                    + "Ela será efetivada quando o pagamento #%d for confirmado.",
                            assinatura.getCliente().getNome(), assinatura.getPlano().getNome(),
                            valorFinal, pagamento.getId()),
                    assinatura.getCliente());
        }

        return pagamento;
    }

    // Confirma um pagamento pendente e efetiva o que ele pagava (o estado da assinatura decide:
    // ativar a PENDENTE ou renovar a ATIVA/EXPIRADA). Se a assinatura não aceitar (ex.: CANCELADA),
    // lança exceção e nada é alterado: o pagamento continua PENDENTE (pode ser recusado).
    public void confirmarPagamento(Pagamento pagamento) {
        pagamento.validarPendente();
        Assinatura assinatura = pagamento.getAssinatura();

        assinatura.efetivarPagamento();
        repositorio.salvar(assinatura);
        pagamentoService.confirmar(pagamento);

        notificador.enviar(
                String.format("Olá %s! Sua assinatura do plano %s está ATIVA até %s.",
                        assinatura.getCliente().getNome(), assinatura.getPlano().getNome(), assinatura.getDataFinal()),
                assinatura.getCliente());
    }

    // Recusa um pagamento pendente. A assinatura não é alterada.
    public void recusarPagamento(Pagamento pagamento) {
        pagamentoService.recusar(pagamento);
    }

    public void cancelarAssinatura(Assinatura assinatura) {
        assinatura.cancelar();
        repositorio.salvar(assinatura);
        notificador.enviar(
                String.format("Olá %s. Sua assinatura do plano %s foi cancelada. Esperamos vê-lo de volta em breve!",
                        assinatura.getCliente().getNome(), assinatura.getPlano().getNome()),
                assinatura.getCliente());
    }

    // ATIVA -> EXPIRADA (fim do período) e avisa o cliente.
    public void expirarAssinatura(Assinatura assinatura) {
        assinatura.expirar();
        repositorio.salvar(assinatura);
        notificador.enviar(
                String.format("Olá %s. Sua assinatura do plano %s expirou. Renove para continuar com acesso.",
                        assinatura.getCliente().getNome(), assinatura.getPlano().getNome()),
                assinatura.getCliente());
    }

    private double calcularValorFinal(Plano plano, Cupom cupom) {
        double valorOriginal = plano.getValor();
        return valorOriginal - calculadoraDesconto.calcularDesconto(valorOriginal, cupom);
    }
}