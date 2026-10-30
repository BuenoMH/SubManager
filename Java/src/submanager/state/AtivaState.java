
package submanager.state;

import submanager.model.Assinatura;

import java.time.LocalDate;

/*
 * Assinatura paga e em vigor. Pode ser renovada (o período é prolongado),
 * cancelada ou expirada (manualmente ou quando a data final passa).
 */
public class AtivaState implements EstadoAssinatura {

    @Override
    public Assinatura.Status getStatus() {
        return Assinatura.Status.ATIVA;
    }

    @Override
    public void renovar(Assinatura assinatura) {
        assinatura.prolongarPeriodo(); // o novo período começa ao fim do atual
    }

    @Override
    public void efetivarPagamento(Assinatura assinatura) {
        renovar(assinatura); // pagamento de renovação confirmado
    }

    @Override
    public void cancelar(Assinatura assinatura) {
        assinatura.mudarEstado(new CanceladaState());
    }

    @Override
    public void expirar(Assinatura assinatura) {
        assinatura.mudarEstado(new ExpiradaState());
    }

    @Override
    public void validarRenovacao() {
        // permitido
    }

    @Override
    public void verificarExpiracao(Assinatura assinatura) {
        if (LocalDate.now().isAfter(assinatura.getDataFinal())) {
            assinatura.mudarEstado(new ExpiradaState());
        }
    }
}
