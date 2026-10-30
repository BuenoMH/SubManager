
package submanager.state;

import submanager.model.Assinatura;

/**
 * Assinatura cujo período terminou. Pode ser renovada (o novo período começa
 * hoje e ela volta a ficar ATIVA) ou cancelada.
 *
 * @note Papel no Padrão: Concrete State
 */
public class ExpiradaState implements EstadoAssinatura {

    @Override
    public Assinatura.Status getStatus() {
        return Assinatura.Status.EXPIRADA;
    }

    @Override
    public void renovar(Assinatura assinatura) {
        assinatura.reiniciarPeriodo();
        assinatura.mudarEstado(new AtivaState());
    }

    @Override
    public void cancelar(Assinatura assinatura) {
        assinatura.mudarEstado(new CanceladaState());
    }

    @Override
    public void validarRenovacao() {
        // permitido
    }
}
