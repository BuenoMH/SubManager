package submanager.external;

/**
 * Resposta devolvida pelo gateway externo PayFlow.
 * Faz parte da "biblioteca de terceiros": não deve ser alterada pelo SubManager.
 *
 * @note Papel no Padrão: Adaptee (parte da API incompatível) - Adapter
 */
public class PayFlowResposta {

    private final int codigo;
    private final String protocolo;

    public PayFlowResposta(int codigo, String protocolo) {
        this.codigo = codigo;
        this.protocolo = protocolo;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getProtocolo() {
        return protocolo;
    }
}
