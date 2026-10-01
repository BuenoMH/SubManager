package submanager.implementations.pagamento;

import submanager.interfaces.MetodoPagamento;

public class CartaoPagamento implements MetodoPagamento {

    @Override
    public boolean pagar(double valor) {
        System.out.println("Comunicando com a operadora do cartão para debitar R$ " + String.format("%.2f", valor));
        return true;
    }

    @Override
    public String getDescricao() {
        return "Cartão de Crédito";
    }
}
