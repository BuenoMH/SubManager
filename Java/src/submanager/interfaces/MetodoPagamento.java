package submanager.interfaces;

// ISP: contrato mínimo de uma forma de pagamento.
/*
  PayFlowGatewayAdapter adapta o PayFlowGateway externo a este contrato.
 */
public interface MetodoPagamento {

    // Inicia a cobrança do valor. Retorna true se a cobrança foi aceita (o pagamento fica PENDENTE
    // até ser confirmado) e false se foi recusada de imediato.
    boolean pagar(double valor);

    String getDescricao();
}
