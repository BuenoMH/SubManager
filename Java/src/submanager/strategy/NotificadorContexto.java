
package submanager.strategy;

import submanager.interfaces.Notificador;
import submanager.model.Cliente;

import java.util.Objects;

/**
 * Guarda o canal de notificação em uso e permite trocá-lo em tempo de execução.
 * Como o canal pode ser simples ou um NotificadorComposto, a troca funciona
 * para qualquer Notificador.
 *
 * @note Papel no Padrão: Context (Strategy). Strategy = Notificador;
 *       Concrete Strategy = Email, WhatsApp e NotificadorComposto.
 */
public class NotificadorContexto implements Notificador {

    private Notificador estrategia;

    public NotificadorContexto(Notificador estrategiaInicial) {
        this.estrategia = Objects.requireNonNull(estrategiaInicial, "estratégia inicial é obrigatória");
    }

    public void trocarEstrategia(Notificador nova) {
        Objects.requireNonNull(nova, "nova estratégia é obrigatória");
        System.out.println("[STRATEGY] Canal de notificação trocado: "
                + estrategia.getClass().getSimpleName() + " -> " + nova.getClass().getSimpleName());
        this.estrategia = nova;
    }

    public Notificador getEstrategiaAtual() {
        return estrategia;
    }

    @Override
    public void enviar(String mensagem, Cliente destinatario) {
        System.out.println("[STRATEGY] Notificando com: " + estrategia.getClass().getSimpleName());
        estrategia.enviar(mensagem, destinatario);
    }
}
