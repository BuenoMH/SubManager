package submanager.factory.pagamento;

import submanager.implementations.pagamento.CartaoPagamento;
import submanager.interfaces.MetodoPagamento;

/**
 * @note Papel no Padrão: Concrete Creator (Factory Method) - cria CartaoPagamento.
 */
public class CartaoCreator extends MetodoPagamentoCreator {

    @Override
    protected MetodoPagamento criarMetodo() {
        return new CartaoPagamento();
    }
}