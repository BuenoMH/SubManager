package submanager.implementations.desconto;

import submanager.interfaces.CalculadorDesconto;
import submanager.model.Cupom;

// Desconto baseado em cupom. Sem cupom (null) ou cupom vencido = desconto zero.
public class DescontoCupom implements CalculadorDesconto {

    @Override
    public double calcularDesconto(double valor, Cupom cupom) {
        if (cupom == null || !cupom.estaValido()) {
            return 0;
        }
        // Cupom garante percentual entre 0 e 100, então o desconto nunca excede o valor.
        return valor * (cupom.getPercentual() / 100.0);
    }
}
