package submanager.factory.notificacao;

import submanager.implementations.notificacao.WhatsappNotificador;
import submanager.interfaces.Notificador;

/**
 * @note Papel no Padrão: Concrete Creator (Factory Method) - cria WhatsappNotificador.
 */
public class WhatsappCreator extends NotificadorCreator {

    @Override
    protected Notificador criarNotificador() {
        return new WhatsappNotificador();
    }
}