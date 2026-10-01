package submanager.implementations.pagamento;

import submanager.interfaces.MetodoPagamento;

public class PixPagamento implements MetodoPagamento {

    @Override
    public boolean pagar(double valor) {
        System.out.println("Gerando QR Code e processando Pix no valor de R$ " + String.format("%.2f", valor));
        return true;
    }

    @Override
    public String getDescricao() {
        return "Pix";
    }
}
