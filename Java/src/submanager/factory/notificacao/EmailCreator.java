package submanager.factory.notificacao;

import submanager.implementations.notificacao.EmailNotificador;
import submanager.interfaces.Notificador;


public class EmailCreator extends NotificadorCreator {

    @Override
    protected Notificador criarNotificador() {
        return new EmailNotificador();
    }
}
