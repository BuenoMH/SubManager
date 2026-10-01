package submanager.interfaces;

// ISP: contrato mínimo de uma forma de pagamento.
public interface MetodoPagamento {

    // Tenta cobrar o valor. Retorna true se aprovado.
    boolean pagar(double valor);

    String getDescricao();
}
