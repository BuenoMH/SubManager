package submanager.factory.pagamento;

import submanager.implementations.pagamento.CartaoPagamento;
import submanager.interfaces.MetodoPagamento;


public class CartaoCreator extends MetodoPagamentoCreator {

    @Override
    protected MetodoPagamento criarMetodo() {
        return new CartaoPagamento();
    }
}