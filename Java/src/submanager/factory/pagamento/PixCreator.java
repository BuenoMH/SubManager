package submanager.factory.pagamento;

import submanager.implementations.pagamento.PixPagamento;
import submanager.interfaces.MetodoPagamento;

public class PixCreator extends MetodoPagamentoCreator {

    @Override
    protected MetodoPagamento criarMetodo() {
        return new PixPagamento();
    }
}