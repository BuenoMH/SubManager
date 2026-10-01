package submanager.implementations.pagamento;

import submanager.interfaces.MetodoPagamento;

public class BoletoPagamento implements MetodoPagamento {

    @Override
    public boolean pagar(double valor) {
        System.out.println("Gerando código de barras para boleto no valor de R$ " + String.format("%.2f", valor));
        return true;
    }

    @Override
    public String getDescricao() {
        return "Boleto Bancário";
    }
}
