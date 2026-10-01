package implementations.notificacao;
import interfaces.Notificador;

// Cuida apenas do envio de E-mails
public class EmailNotificador implements Notificador {
    @Override
    public void enviar(String mensagem, String destinatario) {
        System.out.println("Enviando E-mail para " + destinatario + " | Mensagem: " + mensagem);
    }

    @Override
    public String getCanal() {
        return "E-mail";
    }
}
