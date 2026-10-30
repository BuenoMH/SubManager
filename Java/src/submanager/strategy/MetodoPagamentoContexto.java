package submanager.strategy;

import submanager.interfaces.MetodoPagamento;

import java.util.Objects;

/**
 * Guarda a forma de pagamento em uso e permite trocá-la em tempo de execução.
 * Os Services recebem este contexto no lugar de um meio de pagamento fixo e
 * continuam sem conhecer nenhuma classe concreta: depois da troca, a próxima
 * cobrança já usa a nova estratégia.
 *
 * @note Papel no Padrão: Context (Strategy). Strategy = MetodoPagamento;
 *       Concrete Strategy = Pix, Cartão, Boleto e PayFlowGatewayAdapter.
 */
public class MetodoPagamentoContexto implements MetodoPagamento {

    private MetodoPagamento estrategia;

    public MetodoPagamentoContexto(MetodoPagamento estrategiaInicial) {
        this.estrategia = Objects.requireNonNull(estrategiaInicial, "estratégia inicial é obrigatória");
    }

    public void trocarEstrategia(MetodoPagamento nova) {
        Objects.requireNonNull(nova, "nova estratégia é obrigatória");
        System.out.println("[STRATEGY] Forma de pagamento trocada: "
                + estrategia.getDescricao() + " -> " + nova.getDescricao());
        this.estrategia = nova;
    }

    public MetodoPagamento getEstrategiaAtual() {
        return estrategia;
    }

    @Override
    public boolean pagar(double valor) {
        System.out.println("[STRATEGY] Executando pagamento com: " + estrategia.getDescricao());
        return estrategia.pagar(valor);
    }

    @Override
    public String getDescricao() {
        return estrategia.getDescricao();
    }
}
