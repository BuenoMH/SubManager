package submanager.service;

import submanager.interfaces.MetodoPagamento;
import submanager.interfaces.Notificador;
import submanager.interfaces.PagamentoRepository;
import submanager.model.Assinatura;
import submanager.model.Pagamento;

import java.util.Objects;

// SRP: coordena o ciclo do pagamento — registrar a cobrança, confirmá-la ou recusá-la, e avisar o cliente.
// DIP: conhece apenas MetodoPagamento, Notificador e PagamentoRepository (abstrações), recebidos pelo construtor.
// OCP: novo meio de pagamento = nova implementação de MetodoPagamento; esta classe não muda.
public class PagamentoService {

    private final MetodoPagamento metodoPagamento;
    private final Notificador notificador;
    private final PagamentoRepository repositorio;

    public PagamentoService(MetodoPagamento metodoPagamento, Notificador notificador, PagamentoRepository repositorio) {
        this.metodoPagamento = Objects.requireNonNull(metodoPagamento, "metodoPagamento é obrigatório");
        this.notificador = Objects.requireNonNull(notificador, "notificador é obrigatório");
        this.repositorio = Objects.requireNonNull(repositorio, "repositorio é obrigatório");
    }

    // Inicia a cobrança. O pagamento nasce PENDENTE e já fica gravado; só passa a APROVADO
    // quando for confirmado (confirmar). Se o meio de pagamento recusar de imediato, fica RECUSADO.
    public Pagamento processarPagamento(Assinatura assinatura, double valor) {
        Pagamento pagamento = new Pagamento(repositorio.proximoId(), assinatura, metodoPagamento, valor);
        repositorio.salvar(pagamento);

        if (metodoPagamento.pagar(valor)) {
            notificador.enviar(
                    String.format("Pagamento #%d de R$ %.2f via %s registrado. Aguardando confirmação.",
                            pagamento.getId(), valor, pagamento.getDescricaoMetodo()),
                    assinatura.getCliente());
        } else {
            pagamento.recusar();
            repositorio.salvar(pagamento);
            notificador.enviar(
                    String.format("Falha ao processar seu pagamento #%d de R$ %.2f via %s.",
                            pagamento.getId(), valor, pagamento.getDescricaoMetodo()),
                    assinatura.getCliente());
        }

        return pagamento;
    }

    // PENDENTE -> APROVADO.
    public void confirmar(Pagamento pagamento) {
        pagamento.aprovar();
        repositorio.salvar(pagamento);
        notificador.enviar(
                String.format("Pagamento #%d de R$ %.2f via %s confirmado.",
                        pagamento.getId(), pagamento.getValor(), pagamento.getDescricaoMetodo()),
                pagamento.getAssinatura().getCliente());
    }

    // PENDENTE -> RECUSADO.
    public void recusar(Pagamento pagamento) {
        pagamento.recusar();
        repositorio.salvar(pagamento);
        notificador.enviar(
                String.format("Pagamento #%d de R$ %.2f via %s foi recusado.",
                        pagamento.getId(), pagamento.getValor(), pagamento.getDescricaoMetodo()),
                pagamento.getAssinatura().getCliente());
    }
}