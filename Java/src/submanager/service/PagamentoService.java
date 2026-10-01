package submanager.service;

import submanager.interfaces.MetodoPagamento;
import submanager.interfaces.Notificador;
import submanager.model.Assinatura;
import submanager.model.Pagamento;

import java.util.Objects;

// SRP: coordena UM processo — cobrar, registrar o resultado e avisar o cliente.
// DIP: conhece apenas MetodoPagamento e Notificador (abstrações), recebidos pelo construtor.
// OCP: novo meio de pagamento = nova implementação de MetodoPagamento; esta classe não muda.
public class PagamentoService {

    private final MetodoPagamento metodoPagamento;
    private final Notificador notificador;

    // Contador simples para IDs (em memória)
    private int proximoId = 1;

    public PagamentoService(MetodoPagamento metodoPagamento, Notificador notificador) {
        this.metodoPagamento = Objects.requireNonNull(metodoPagamento, "metodoPagamento é obrigatório");
        this.notificador = Objects.requireNonNull(notificador, "notificador é obrigatório");
    }

    // Cobra o valor e devolve o Pagamento já finalizado (APROVADO ou RECUSADO).
    public Pagamento processarPagamento(Assinatura assinatura, double valor) {
        Pagamento pagamento = new Pagamento(proximoId++, assinatura, metodoPagamento, valor);

        if (metodoPagamento.pagar(valor)) {
            pagamento.aprovar();
            notificador.enviar(
                    String.format("Pagamento de R$ %.2f aprovado via %s.", valor, metodoPagamento.getDescricao()),
                    assinatura.getCliente());
        } else {
            pagamento.recusar();
            notificador.enviar(
                    String.format("Falha ao processar seu pagamento de R$ %.2f via %s.", valor, metodoPagamento.getDescricao()),
                    assinatura.getCliente());
        }

        return pagamento;
    }
}
