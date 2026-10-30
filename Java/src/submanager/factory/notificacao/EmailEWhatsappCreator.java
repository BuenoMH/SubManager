package submanager.factory.notificacao;

import submanager.composite.NotificadorComposto;
import submanager.interfaces.Notificador;


public class EmailEWhatsappCreator extends NotificadorCreator {

    @Override
    protected Notificador criarNotificador() {
        return new NotificadorComposto()
                .adicionar(new EmailCreator().obterNotificador())
                .adicionar(new WhatsappCreator().obterNotificador());
    }
}
