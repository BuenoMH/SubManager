package submanager.external;

/*
 * Gateway de pagamento de terceiros (simulado). Simula uma biblioteca externa
 * que o SubManager não controla: valores em CENTAVOS (long), moeda informada
 * como texto, resposta por código numérico e nenhuma relação com a interface
 * {@code MetodoPagamento} do sistema.
 */
public class PayFlowGateway {

    public static final int CODIGO_APROVADO = 200;
    public static final int CODIGO_RECUSADO = 402;

    private static final long LIMITE_POR_COBRANCA_CENTAVOS = 1_000_000L; // R$ 10.000,00

    private int sequencia = 1;

    public PayFlowResposta enviarCobranca(String contaOrigem, long valorEmCentavos, String moeda) {
        String protocolo = String.format("PF-%06d", sequencia++);
        System.out.println("[PayFlow] Cobrança " + protocolo + " | conta " + contaOrigem
                + " | " + valorEmCentavos + " centavos (" + moeda + ")");
        int codigo = valorEmCentavos > LIMITE_POR_COBRANCA_CENTAVOS ? CODIGO_RECUSADO : CODIGO_APROVADO;
        return new PayFlowResposta(codigo, protocolo);
    }

    public String nomeDoProvedor() {
        return "PayFlow";
    }
}
