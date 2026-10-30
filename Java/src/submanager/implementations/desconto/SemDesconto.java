package submanager.implementations.desconto;

import submanager.interfaces.CalculadorDesconto;
import submanager.model.Cupom;

/**
 * Opção "Nenhum desconto": devolve sempre zero.
 * Substitui o lambda que existia no switch da Main (Null Object simples).
 *
 * @note Papel no Padrão: Concrete Product (Factory Method); Leaf (Composite - DescontoComposto)
 */
public class SemDesconto implements CalculadorDesconto {

    @Override
    public double calcularDesconto(double valor, Cupom cupom) {
        return 0.0;
    }
}