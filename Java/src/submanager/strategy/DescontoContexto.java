
package submanager.strategy;

import submanager.interfaces.CalculadorDesconto;
import submanager.model.Cupom;

import java.util.Objects;

/**
 * Guarda a regra de desconto em uso e permite trocá-la em tempo de execução.
 * A próxima cobrança (por exemplo, uma renovação) já usa a nova regra.
 *
 * @note Papel no Padrão: Context (Strategy). Strategy = CalculadorDesconto;
 *       Concrete Strategy = ClienteNovo, Cupom, SemDesconto e DescontoComposto.
 */
public class DescontoContexto implements CalculadorDesconto {

    private CalculadorDesconto estrategia;

    public DescontoContexto(CalculadorDesconto estrategiaInicial) {
        this.estrategia = Objects.requireNonNull(estrategiaInicial, "estratégia inicial é obrigatória");
    }

    public void trocarEstrategia(CalculadorDesconto nova) {
        Objects.requireNonNull(nova, "nova estratégia é obrigatória");
        System.out.println("[STRATEGY] Desconto trocado: "
                + estrategia.getClass().getSimpleName() + " -> " + nova.getClass().getSimpleName());
        this.estrategia = nova;
    }

    public CalculadorDesconto getEstrategiaAtual() {
        return estrategia;
    }

    @Override
    public double calcularDesconto(double valor, Cupom cupom) {
        System.out.println("[STRATEGY] Calculando desconto com: " + estrategia.getClass().getSimpleName());
        return estrategia.calcularDesconto(valor, cupom);
    }
}
