package submanager.factory.desconto;

import submanager.implementations.desconto.DescontoCupom;
import submanager.interfaces.CalculadorDesconto;

/**
 * @note Papel no Padrão: Concrete Creator (Factory Method) - cria DescontoCupom.
 */
public class DescontoCupomCreator extends DescontoCreator {

    @Override
    protected CalculadorDesconto criarCalculador() {
        return new DescontoCupom();
    }
}