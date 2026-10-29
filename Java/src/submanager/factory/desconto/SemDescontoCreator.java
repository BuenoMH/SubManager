package submanager.factory.desconto;

import submanager.implementations.desconto.SemDesconto;
import submanager.interfaces.CalculadorDesconto;

/**
 * @note Papel no Padrão: Concrete Creator (Factory Method) - cria SemDesconto.
 */
public class SemDescontoCreator extends DescontoCreator {

    @Override
    protected CalculadorDesconto criarCalculador() {
        return new SemDesconto();
    }
}