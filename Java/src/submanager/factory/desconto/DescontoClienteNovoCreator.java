package submanager.factory.desconto;

import submanager.implementations.desconto.DescontoClienteNovo;
import submanager.interfaces.CalculadorDesconto;

/**
 * @note Papel no Padrão: Concrete Creator (Factory Method) - cria DescontoClienteNovo.
 */
public class DescontoClienteNovoCreator extends DescontoCreator {

    @Override
    protected CalculadorDesconto criarCalculador() {
        return new DescontoClienteNovo();
    }
}