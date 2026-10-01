package submanager.service;

import submanager.interfaces.MetodoPagamento;
import submanager.interfaces.Notificador;
import submanager.model.Assinatura;

// TODO: Integrante 2 — implementar a lógica completa de processamento de pagamento.
// O método processarPagamento foi definido para integração com o AssinaturaService.

public class PagamentoService {

    private MetodoPagamento metodoPagamento;
    private Notificador notificador;

    public PagamentoService(MetodoPagamento metodoPagamento, Notificador notificador) {
        this.metodoPagamento = metodoPagamento;
        this.notificador = notificador;
    }

    // Processa o pagamento de uma assinatura e retorna true se aprovado.
    // TODO: Integrante 2 — substituir pela lógica real de cobrança.
    public boolean processarPagamento(Assinatura assinatura, double valor) {
        System.out.println("  [PagamentoService] Processando pagamento de R$ " + String.format("%.2f", valor) + "...");

        // Delega para o método de pagamento (Pix, Cartão, Boleto)
        boolean aprovado = metodoPagamento.pagar(valor);

        if (aprovado) {
            System.out.println("  [PagamentoService] Pagamento aprovado.");
        } else {
            System.out.println("  [PagamentoService] Pagamento recusado.");
        }

        return aprovado;
    }
}
