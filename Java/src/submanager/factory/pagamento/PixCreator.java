package submanager.factory.pagamento;

import submanager.implementations.pagamento.PixPagamento;
import submanager.interfaces.MetodoPagamento;

/**
 * @note Papel no Padrão: Concrete Creator (Factory Method) - cria PixPagamento.
 */
public class PixCreator extends MetodoPagamentoCreator {

    @Override
    protected MetodoPagamento criarMetodo() {
        return new PixPagamento();
    }
}