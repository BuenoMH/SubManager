package interfaces;

// Contrato para envio de mensagens, independente de ser E-mail ou WhatsApp
public interface Notificador {
    void enviar(String mensagem, String destinatario);
    String getCanal();
}
