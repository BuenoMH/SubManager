package submanager.factory.desconto;

import submanager.composite.DescontoComposto;
import submanager.interfaces.CalculadorDesconto;

/**
 * @note Papel no Padrão: Concrete Creator (Factory Method) - cria um DescontoComposto
 *       (Composite) reunindo os descontos de DescontoClienteNovoCreator e DescontoCupomCreator.
 */
public class ClienteNovoECupomCreator extends DescontoCreator {

    @Override
    protected CalculadorDesconto criarCalculador() {
        return new DescontoComposto()
                .adicionar(new DescontoClienteNovoCreator().obterCalculador())
                .adicionar(new DescontoCupomCreator().obterCalculador());
    }
}
