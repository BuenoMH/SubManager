package submanager.implementations.notificacao;

import submanager.interfaces.Notificador;
import submanager.model.Cliente;

/**
 * Canal de notificação por e-mail (usa {@code Cliente.getEmail()}).
 *
 * @note Papel no Padrão: Concrete Product (Factory Method); Leaf (Composite - NotificadorComposto)
 */
public class EmailNotificador implements Notificador {

    @Override
    public void enviar(String mensagem, Cliente destinatario) {
        System.out.println("Enviando E-mail para " + destinatario.getEmail() + " | Mensagem: " + mensagem);
    }
}
