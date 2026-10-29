package submanager.factory.notificacao;

import submanager.implementations.notificacao.EmailNotificador;
import submanager.interfaces.Notificador;

/**
 * @note Papel no Padrão: Concrete Creator (Factory Method) - cria EmailNotificador.
 */
public class EmailCreator extends NotificadorCreator {

    @Override
    protected Notificador criarNotificador() {
        return new EmailNotificador();
    }
}
