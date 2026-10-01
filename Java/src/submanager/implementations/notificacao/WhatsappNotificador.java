package submanager.implementations.notificacao;

import submanager.interfaces.Notificador;
import submanager.model.Cliente;

public class WhatsappNotificador implements Notificador {

    @Override
    public void enviar(String mensagem, Cliente destinatario) {
        System.out.println("Enviando WhatsApp para " + destinatario.getTelefone() + " | Mensagem: " + mensagem);
    }
}