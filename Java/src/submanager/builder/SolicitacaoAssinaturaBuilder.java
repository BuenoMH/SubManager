package submanager.builder;

import submanager.interfaces.CalculadorDesconto;
import submanager.interfaces.MetodoPagamento;
import submanager.interfaces.Notificador;
import submanager.model.Cliente;
import submanager.model.Cupom;
import submanager.model.Plano;

import java.util.ArrayList;
import java.util.List;

/**
 * Monta a {@link SolicitacaoAssinatura} passo a passo, na mesma ordem do menu
 * (cliente, plano, notificação, desconto, pagamento). Os setters apenas guardam
 * os valores; toda a validação acontece em {@link #build()}, que reporta de uma
 * vez só tudo o que estiver faltando ou inválido.
 *
 * @note Papel no Padrão: Builder (Concrete Builder). A Main atua como Director,
 *       conduzindo a ordem dos passos conforme as escolhas do usuário.
 */
public class SolicitacaoAssinaturaBuilder {

    private Cliente cliente;
    private Plano plano;
    private Notificador notificador;
    private CalculadorDesconto calculadorDesconto;
    private Cupom cupom;
    private MetodoPagamento metodoPagamento;

    public SolicitacaoAssinaturaBuilder comCliente(Cliente cliente) {
        this.cliente = cliente;
        return this;
    }

    public SolicitacaoAssinaturaBuilder comPlano(Plano plano) {
        this.plano = plano;
        return this;
    }

    public SolicitacaoAssinaturaBuilder comNotificador(Notificador notificador) {
        this.notificador = notificador;
        return this;
    }

    public SolicitacaoAssinaturaBuilder comDesconto(CalculadorDesconto calculadorDesconto) {
        this.calculadorDesconto = calculadorDesconto;
        return this;
    }

    /** Opcional: só é necessário para descontos baseados em cupom. */
    public SolicitacaoAssinaturaBuilder comCupom(Cupom cupom) {
        this.cupom = cupom;
        return this;
    }

    public SolicitacaoAssinaturaBuilder comMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
        return this;
    }

    /**
     * Valida todos os campos e cria a solicitação.
     *
     * @throws IllegalStateException listando todas as pendências encontradas
     */
    public SolicitacaoAssinatura build() {
        List<String> problemas = new ArrayList<>();

        if (cliente == null) problemas.add("cliente não informado");
        if (plano == null) problemas.add("plano não escolhido");
        if (notificador == null) problemas.add("canal de notificação não escolhido");
        if (calculadorDesconto == null) problemas.add("tipo de desconto não escolhido");
        if (metodoPagamento == null) problemas.add("forma de pagamento não escolhida");
        if (cupom != null && !cupom.estaValido()) problemas.add("cupom " + cupom.getCodigo() + " está vencido");

        if (!problemas.isEmpty()) {
            throw new IllegalStateException("Solicitação inválida: " + String.join("; ", problemas) + ".");
        }

        SolicitacaoAssinatura solicitacao =
                new SolicitacaoAssinatura(cliente, plano, notificador, calculadorDesconto, cupom, metodoPagamento);
        System.out.println("[BUILDER] build() validou os 5 passos e construiu a SolicitacaoAssinatura.");
        return solicitacao;
    }
}