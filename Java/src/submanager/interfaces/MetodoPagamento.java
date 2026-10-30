package submanager.interfaces;

// ISP: contrato mínimo de uma forma de pagamento.
/**
 * @note Papel no Padrão: Product (Factory Method); Target (Adapter) -
 *       PayFlowGatewayAdapter adapta o PayFlowGateway externo a este contrato.
 */
public interface MetodoPagamento {

    // Tenta cobrar o valor. Retorna true se aprovado.
    boolean pagar(double valor);

    String getDescricao();
}
