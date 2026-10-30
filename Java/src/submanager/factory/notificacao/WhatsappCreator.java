package submanager.factory.notificacao;

import submanager.implementations.notificacao.WhatsappNotificador;
import submanager.interfaces.Notificador;

public class WhatsappCreator extends NotificadorCreator {

    @Override
    protected Notificador criarNotificador() {
        return new WhatsappNotificador();
    }
}