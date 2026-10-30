
package submanager.state;

import submanager.model.Assinatura;

/**
 * Contrato de um estado do ciclo de vida da assinatura. Cada operação recebe a
 * assinatura (o contexto) para poder executar a transição. Por padrão toda
 * operação é INVÁLIDA e lança exceção; cada estado concreto sobrescreve apenas
 * o que é permitido nele. Assim a Assinatura não precisa de nenhum "if" de status.
 *
 * @note Papel no Padrão: State (interface)
 */
public interface EstadoAssinatura {

    /** Status exposto para o restante do sistema (mantém o enum Assinatura.Status). */
    Assinatura.Status getStatus();

    default void ativar(Assinatura assinatura) {
        throw new IllegalStateException(
                "Só é possível ativar uma assinatura PENDENTE (status atual: " + getStatus() + ").");
    }

    default void renovar(Assinatura assinatura) {
        throw renovacaoNegada();
    }

    default void cancelar(Assinatura assinatura) {
        throw new IllegalStateException(
                "Não é possível cancelar uma assinatura " + getStatus() + ".");
    }

    default void expirar(Assinatura assinatura) {
        throw new IllegalStateException(
                "Só é possível expirar uma assinatura ATIVA (status atual: " + getStatus() + ").");
    }

    /** Lança exceção se o estado não permitir renovação. Usado antes de cobrar o cliente. */
    default void validarRenovacao() {
        throw renovacaoNegada();
    }

    /** Estados que podem expirar por data sobrescrevem este método. */
    default void verificarExpiracao(Assinatura assinatura) {
        // por padrão não faz nada
    }

    private IllegalStateException renovacaoNegada() {
        return new IllegalStateException("Não é possível renovar uma assinatura " + getStatus() + ".");
    }
}
