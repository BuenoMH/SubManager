package submanager.factory.notificacao;

import submanager.interfaces.Notificador;


public abstract class NotificadorCreator {

    /* Factory Method: cada Creator concreto decide qual Notificador instanciar. */
    protected abstract Notificador criarNotificador();

    public final Notificador obterNotificador() {
        Notificador notificador = criarNotificador();
        System.out.println("[FACTORY METHOD] " + getClass().getSimpleName()
                + " criou " + notificador.getClass().getSimpleName());
        return notificador;
    }
}