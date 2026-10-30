package submanager.implementations.notificacao;

import submanager.interfaces.Notificador;
import submanager.model.Cliente;

/**
 * Canal de notificação por WhatsApp (usa {@code Cliente.getTelefone()}).
 *
 * @note Papel no Padrão: Concrete Product (Factory Method); Leaf (Composite - NotificadorComposto)
 */
public class WhatsappNotificador implements Notificador {

    @Override
    public void enviar(String mensagem, Cliente destinatario) {
        System.out.println("Enviando WhatsApp para " + destinatario.getTelefone() + " | Mensagem: " + mensagem);
    }
}