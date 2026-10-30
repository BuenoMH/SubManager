package submanager.factory.pagamento;

import submanager.adapter.PayFlowGatewayAdapter;
import submanager.external.PayFlowGateway;
import submanager.interfaces.MetodoPagamento;


public class GatewayExternoCreator extends MetodoPagamentoCreator {

    private static final String CONTA_ORIGEM = "SUBMANAGER-LOJA";

    @Override
    protected MetodoPagamento criarMetodo() {
        return new PayFlowGatewayAdapter(new PayFlowGateway(), CONTA_ORIGEM);
    }
}
