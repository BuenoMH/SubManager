package submanager.implementations.desconto;

import submanager.interfaces.CalculadorDesconto;
import submanager.model.Cupom;

// Desconto fixo de 15% para cliente novo. Não depende de cupom (parâmetro ignorado).
public class DescontoClienteNovo implements CalculadorDesconto {

    private static final double PERCENTUAL_CLIENTE_NOVO = 15.0;

    @Override
    public double calcularDesconto(double valor, Cupom cupom) {
        return valor * (PERCENTUAL_CLIENTE_NOVO / 100.0);
    }
}
