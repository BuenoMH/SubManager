package submanager.composite;

import submanager.interfaces.CalculadorDesconto;
import submanager.model.Cupom;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Agrupa vários descontos e os trata como um único {@link CalculadorDesconto}.
 * Os descontos são aplicados em cascata: cada filho calcula sobre o que restou
 * depois dos anteriores, então a soma nunca passa do valor original.
 *
 * @note Papel no Padrão: Composite. Component = CalculadorDesconto;
 *       Leaf = DescontoClienteNovo / DescontoCupom / SemDesconto.
 */
public class DescontoComposto implements CalculadorDesconto {

    private final List<CalculadorDesconto> descontos = new ArrayList<>();

    public DescontoComposto adicionar(CalculadorDesconto desconto) {
        descontos.add(Objects.requireNonNull(desconto, "desconto é obrigatório"));
        return this;
    }

    public DescontoComposto remover(CalculadorDesconto desconto) {
        descontos.remove(desconto);
        return this;
    }

    @Override
    public double calcularDesconto(double valor, Cupom cupom) {
        System.out.println("[COMPOSITE] DescontoComposto combinando " + descontos.size() + " desconto(s) em cascata");
        double restante = valor;
        double total = 0;
        for (CalculadorDesconto desconto : descontos) {
            double parcial = desconto.calcularDesconto(restante, cupom);
            total += parcial;
            restante -= parcial;
        }
        return total;
    }
}
