package submanager.interfaces;

// ISP: interface pequena e específica — só define o contrato de notificação.
// TODO: Integrante 2 — esta interface foi definida para integração.

public interface Notificador {

    // Envia uma notificação para o destinatário.
    void enviar(String mensagem, String destinatario, String assunto);

    // Retorna o canal de notificação (ex.: "Email", "WhatsApp").
    String getCanal();
}
