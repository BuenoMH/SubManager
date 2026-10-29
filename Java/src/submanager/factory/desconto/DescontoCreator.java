package submanager.factory.desconto;

import submanager.interfaces.CalculadorDesconto;

/**
 * Creator abstrato dos calculadores de desconto.
 *
 * @note Papel no Padrão: Creator (Factory Method)
 */
public abstract class DescontoCreator {

    /** Factory Method: cada Creator concreto decide qual CalculadorDesconto instanciar. */
    protected abstract CalculadorDesconto criarCalculador();

    public final CalculadorDesconto obterCalculador() {
        CalculadorDesconto calculador = criarCalculador();
        System.out.println("[FACTORY METHOD] " + getClass().getSimpleName()
                + " criou " + calculador.getClass().getSimpleName());
        return calculador;
    }
}