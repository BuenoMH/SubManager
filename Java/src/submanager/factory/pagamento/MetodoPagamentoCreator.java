package submanager.factory.pagamento;

import submanager.interfaces.MetodoPagamento;


public abstract class MetodoPagamentoCreator {

    /* Factory Method: cada Creator concreto decide qual MetodoPagamento instanciar. */
    protected abstract MetodoPagamento criarMetodo();

    public final MetodoPagamento obterMetodo() {
        MetodoPagamento metodo = criarMetodo();
        System.out.println("[FACTORY METHOD] " + getClass().getSimpleName()
                + " criou " + metodo.getClass().getSimpleName() + " (" + metodo.getDescricao() + ")");
        return metodo;
    }
}