package submanager.factory.desconto;

import submanager.implementations.desconto.SemDesconto;
import submanager.interfaces.CalculadorDesconto;

public class SemDescontoCreator extends DescontoCreator {

    @Override
    protected CalculadorDesconto criarCalculador() {
        return new SemDesconto();
    }
}