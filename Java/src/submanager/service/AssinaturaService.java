package submanager.service;

import submanager.interfaces.CalculadorDesconto;
import submanager.interfaces.Notificador;
import submanager.model.Assinatura;
import submanager.model.Cliente;
import submanager.model.Cupom;
import submanager.model.Pagamento;
import submanager.model.Plano;

import java.util.Objects;

// SRP: coordena o ciclo de vida das assinaturas. Não calcula desconto, não cobra, não envia mensagem.
// DIP: depende de CalculadoraDesconto, Notificador e PagamentoService, injetados pelo construtor.
// OCP: novo desconto/canal/meio de pagamento = nova implementação; esta classe não muda.
public class AssinaturaService {

    private final PagamentoService pagamentoService;
    private final Notificador notificador;
    private final CalculadorDesconto calculadoraDesconto;

    // Contador simples para IDs (em memória)
    private int proximoId = 1;

    public AssinaturaService(PagamentoService pagamentoService,
                             Notificador notificador,
                             CalculadorDesconto calculadoraDesconto) {
        this.pagamentoService = Objects.requireNonNull(pagamentoService, "pagamentoService é obrigatório");
        this.notificador = Objects.requireNonNull(notificador, "notificador é obrigatório");
        this.calculadoraDesconto = Objects.requireNonNull(calculadoraDesconto, "calculadoraDesconto é obrigatória");
    }

    // desconto -> valor final -> pagamento -> ativação -> notificação.
    // Se o pagamento for recusado, devolve a assinatura ainda PENDENTE.
    public Assinatura criarAssinatura(Cliente cliente, Plano plano, Cupom cupom) {
        Assinatura assinatura = new Assinatura(proximoId++, cliente, plano); // valida cliente/plano; nasce PENDENTE

        double valorOriginal = plano.getValor();
        double valorFinal = valorOriginal - calculadoraDesconto.calcularDesconto(valorOriginal, cupom);

        Pagamento pagamento = pagamentoService.processarPagamento(assinatura, valorFinal);
        if (!pagamento.isAprovado()) {
            return assinatura;
        }

        assinatura.ativar();
        notificador.enviar(
                String.format("Olá %s! Sua assinatura do plano %s foi criada com sucesso. Valor: R$ %.2f",
                        cliente.getNome(), plano.getNome(), valorFinal),
                cliente);

        return assinatura;
    }

    public Assinatura criarAssinatura(Cliente cliente, Plano plano) {
        return criarAssinatura(cliente, plano, null);
    }

    // Renova por mais um período. Retorna false se o pagamento foi recusado.
    // Valida ANTES de cobrar: uma assinatura cancelada/pendente nunca gera cobrança.
    public boolean renovarAssinatura(Assinatura assinatura) {
        assinatura.validarRenovacao();

        Pagamento pagamento = pagamentoService.processarPagamento(assinatura, assinatura.getPlano().getValor());
        if (!pagamento.isAprovado()) {
            return false;
        }

        assinatura.renovar();
        notificador.enviar(
                String.format("Olá %s! Sua assinatura do plano %s foi renovada. Novo vencimento: %s",
                        assinatura.getCliente().getNome(), assinatura.getPlano().getNome(), assinatura.getDataFinal()),
                assinatura.getCliente());

        return true;
    }

    public void cancelarAssinatura(Assinatura assinatura) {
        assinatura.cancelar();
        notificador.enviar(
                String.format("Olá %s. Sua assinatura do plano %s foi cancelada. Esperamos vê-lo de volta em breve!",
                        assinatura.getCliente().getNome(), assinatura.getPlano().getNome()),
                assinatura.getCliente());
    }
}
