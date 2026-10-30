
package submanager.state;

import submanager.model.Assinatura;

/*
 * Assinatura criada e aguardando o pagamento. Pode ser ativada ou cancelada.
 */
public class PendenteState implements EstadoAssinatura {

    @Override
    public Assinatura.Status getStatus() {
        return Assinatura.Status.PENDENTE;
    }

    @Override
    public void ativar(Assinatura assinatura) {
        assinatura.reiniciarPeriodo();
        assinatura.mudarEstado(new AtivaState());
    }

    @Override
    public void efetivarPagamento(Assinatura assinatura) {
        ativar(assinatura); // primeiro pagamento confirmado: PENDENTE -> ATIVA
    }

    @Override
    public void cancelar(Assinatura assinatura) {
        assinatura.mudarEstado(new CanceladaState());
    }
}
