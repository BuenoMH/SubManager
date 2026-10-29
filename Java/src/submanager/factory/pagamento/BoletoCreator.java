package submanager.factory.pagamento;

import submanager.implementations.pagamento.BoletoPagamento;
import submanager.interfaces.MetodoPagamento;

/**
 * @note Papel no Padrão: Concrete Creator (Factory Method) - cria BoletoPagamento.
 */
public class BoletoCreator extends MetodoPagamentoCreator {

    @Override
    protected MetodoPagamento criarMetodo() {
        return new BoletoPagamento();
    }
}