package submanager.factory.desconto;

import submanager.implementations.desconto.DescontoCupom;
import submanager.interfaces.CalculadorDesconto;

public class DescontoCupomCreator extends DescontoCreator {

    @Override
    protected CalculadorDesconto criarCalculador() {
        return new DescontoCupom();
    }
}