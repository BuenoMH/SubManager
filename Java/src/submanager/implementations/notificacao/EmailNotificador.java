package submanager.implementations.notificacao;

import submanager.interfaces.Notificador;
import submanager.model.Cliente;

public class EmailNotificador implements Notificador {

    @Override
    public void enviar(String mensagem, Cliente destinatario) {
        System.out.println("Enviando E-mail para " + destinatario.getEmail() + " | Mensagem: " + mensagem);
    }
}
