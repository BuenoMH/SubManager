package submanager.adapter;

import submanager.external.PayFlowGateway;
import submanager.external.PayFlowResposta;
import submanager.interfaces.MetodoPagamento;

import java.util.Objects;

/*
   Adapta a API do gateway externo PayFlow ao contrato {@link MetodoPagamento}.
   Traduz o que o sistema entende (valor em reais, retorno boolean) para o que o
   gateway exige (centavos, moeda em texto, código de resposta). Os Services
   continuam enxergando apenas um MetodoPagamento comum.
   Target = MetodoPagamento; Adaptee = PayFlowGateway.
 */
public class PayFlowGatewayAdapter implements MetodoPagamento {

    private static final String MOEDA = "BRL";

    private final PayFlowGateway gateway;
    private final String contaOrigem;

    public PayFlowGatewayAdapter(PayFlowGateway gateway, String contaOrigem) {
        this.gateway = Objects.requireNonNull(gateway, "gateway é obrigatório");
        if (contaOrigem == null || contaOrigem.isBlank()) {
            throw new IllegalArgumentException("A conta de origem é obrigatória.");
        }
        this.contaOrigem = contaOrigem.trim();
    }

    @Override
    public boolean pagar(double valor) {
        long centavos = Math.round(valor * 100);
        System.out.println("[ADAPTER] pagar(R$ " + String.format("%.2f", valor)
                + ") traduzido para enviarCobranca(" + centavos + " centavos, " + MOEDA + ")");
        PayFlowResposta resposta = gateway.enviarCobranca(contaOrigem, centavos, MOEDA);
        System.out.println("[ADAPTER] resposta " + resposta.getCodigo()
                + " (protocolo " + resposta.getProtocolo() + ") traduzida para "
                + (resposta.getCodigo() == PayFlowGateway.CODIGO_APROVADO ? "cobrança aceita" : "cobrança recusada"));
        return resposta.getCodigo() == PayFlowGateway.CODIGO_APROVADO;
    }

    @Override
    public String getDescricao() {
        return "Gateway Externo (" + gateway.nomeDoProvedor() + ")";
    }
}
