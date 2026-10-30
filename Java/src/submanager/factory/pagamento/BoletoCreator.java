package submanager.factory.pagamento;

import submanager.implementations.pagamento.BoletoPagamento;
import submanager.interfaces.MetodoPagamento;


public class BoletoCreator extends MetodoPagamentoCreator {

    @Override
    protected MetodoPagamento criarMetodo() {
        return new BoletoPagamento();
    }
}