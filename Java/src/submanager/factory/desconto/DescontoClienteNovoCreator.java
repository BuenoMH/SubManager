package submanager.factory.desconto;

import submanager.implementations.desconto.DescontoClienteNovo;
import submanager.interfaces.CalculadorDesconto;

public class DescontoClienteNovoCreator extends DescontoCreator {

    @Override
    protected CalculadorDesconto criarCalculador() {
        return new DescontoClienteNovo();
    }
}