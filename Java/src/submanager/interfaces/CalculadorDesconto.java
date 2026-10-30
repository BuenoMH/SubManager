package submanager.interfaces;

import submanager.model.Cupom;

// ISP: contrato mínimo para cálculo de desconto.
/**
 * @note Papel no Padrão: Product (Factory Method); Component (Composite) -
 *       implementado pelas folhas (ClienteNovo/Cupom/SemDesconto) e por DescontoComposto.
 */
public interface CalculadorDesconto {

    // Retorna o VALOR DO DESCONTO em reais (0 se não houver), nunca o preço final.
    // O cupom pode ser null; cada implementação decide se o utiliza.
    double calcularDesconto(double valor, Cupom cupom);
}
