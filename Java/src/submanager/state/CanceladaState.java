
package submanager.state;

import submanager.model.Assinatura;

/*
 * Estado final: nenhuma operação é permitida, e cancelar de novo é erro.
 */
public class CanceladaState implements EstadoAssinatura {

    @Override
    public Assinatura.Status getStatus() {
        return Assinatura.Status.CANCELADA;
    }

    @Override
    public void cancelar(Assinatura assinatura) {
        throw new IllegalStateException("A assinatura já está cancelada.");
    }
}
