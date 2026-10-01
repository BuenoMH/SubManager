package submanager.interfaces;

import submanager.model.Cupom;

// ISP: interface pequena e focada — só define o contrato para cálculo de desconto.
// DIP: os Services dependem desta abstração, não das implementações concretas.

public interface CalculadorDesconto {

    // Recebe o valor original e um cupom (pode ser null) e retorna o valor com desconto aplicado.
    double calcularDesconto(double valor, Cupom cupom);

    // Retorna uma descrição do tipo de desconto aplicado.
    String getDescricao();
}
