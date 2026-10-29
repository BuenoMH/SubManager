package submanager.factory.pagamento;

import submanager.interfaces.MetodoPagamento;

/**
 * Creator abstrato dos meios de pagamento.
 * Define o Factory Method {@link #criarMetodo()}, implementado pelas subclasses,
 * e o método {@link #obterMetodo()}, que usa o produto e registra a criação no console.
 *
 * @note Papel no Padrão: Creator (Factory Method)
 */
public abstract class MetodoPagamentoCreator {

    /** Factory Method: cada Creator concreto decide qual MetodoPagamento instanciar. */
    protected abstract MetodoPagamento criarMetodo();

    public final MetodoPagamento obterMetodo() {
        MetodoPagamento metodo = criarMetodo();
        System.out.println("[FACTORY METHOD] " + getClass().getSimpleName()
                + " criou " + metodo.getClass().getSimpleName() + " (" + metodo.getDescricao() + ")");
        return metodo;
    }
}