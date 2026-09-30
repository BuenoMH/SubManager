package submanager.interfaces;

// ISP: interface pequena e específica — só define o contrato de pagamento.
// TODO: Integrante 2 — esta interface foi definida para integração.

public interface MetodoPagamento {

    // Tenta processar o pagamento do valor informado. Retorna true se aprovado.
    boolean pagar(double valor);

    // Retorna o nome/descrição do método de pagamento (ex.: "Pix", "Cartão").
    String getDescricao();
}
