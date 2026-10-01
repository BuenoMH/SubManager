package implementations.notificacao;
import interfaces.Notificador;

// Cuida apenas do envio de mensagens no WhatsApp
public class WhatsAppNotificador implements Notificador {
    @Override
    public void enviar(String mensagem, String destinatario) {
        System.out.println("Enviando WhatsApp para " + destinatario + " | Mensagem: " + mensagem);
    }

    @Override
    public String getCanal() {
        return "WhatsApp";
    }
}
