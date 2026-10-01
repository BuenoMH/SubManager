package submanager.interfaces;

import submanager.model.Cliente;

// ISP: contrato mínimo de notificação.
// Recebe o Cliente (e não uma String) porque cada canal usa um contato diferente: e-mail usa getEmail(), WhatsApp usa getTelefone(). Assim os services nunca
// precisam saber qual canal está por trás da interface (LSP/DIP).
public interface Notificador {

    void enviar(String mensagem, Cliente destinatario);
}
