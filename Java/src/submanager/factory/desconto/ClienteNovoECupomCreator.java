package submanager.factory.desconto;

import submanager.composite.DescontoComposto;
import submanager.interfaces.CalculadorDesconto;


public class ClienteNovoECupomCreator extends DescontoCreator {

    @Override
    protected CalculadorDesconto criarCalculador() {
        return new DescontoComposto()
                .adicionar(new DescontoClienteNovoCreator().obterCalculador())
                .adicionar(new DescontoCupomCreator().obterCalculador());
    }
}
