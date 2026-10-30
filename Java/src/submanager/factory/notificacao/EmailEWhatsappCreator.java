package submanager.factory.notificacao;

import submanager.composite.NotificadorComposto;
import submanager.interfaces.Notificador;

/**
 * @note Papel no Padrão: Concrete Creator (Factory Method) - cria um NotificadorComposto
 *       (Composite) reunindo os canais criados por EmailCreator e WhatsappCreator.
 */
public class EmailEWhatsappCreator extends NotificadorCreator {

    @Override
    protected Notificador criarNotificador() {
        return new NotificadorComposto()
                .adicionar(new EmailCreator().obterNotificador())
                .adicionar(new WhatsappCreator().obterNotificador());
    }
}
