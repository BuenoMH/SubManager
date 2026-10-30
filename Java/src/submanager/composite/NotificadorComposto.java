package submanager.composite;

import submanager.interfaces.Notificador;
import submanager.model.Cliente;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Agrupa vários canais de notificação e os trata como um único {@link Notificador}.
 * Os Services chamam enviar() uma vez e a mensagem sai por todos os canais.
 * Como também é um Notificador, pode conter outros compostos.
 *
 * @note Papel no Padrão: Composite. Component = Notificador;
 *       Leaf = EmailNotificador / WhatsappNotificador.
 */
public class NotificadorComposto implements Notificador {

    private final List<Notificador> canais = new ArrayList<>();

    public NotificadorComposto adicionar(Notificador canal) {
        canais.add(Objects.requireNonNull(canal, "canal é obrigatório"));
        return this;
    }

    public NotificadorComposto remover(Notificador canal) {
        canais.remove(canal);
        return this;
    }

    @Override
    public void enviar(String mensagem, Cliente destinatario) {
        System.out.println("[COMPOSITE] NotificadorComposto repassando para " + canais.size() + " canal(is)");
        for (Notificador canal : canais) {
            canal.enviar(mensagem, destinatario);
        }
    }
}
