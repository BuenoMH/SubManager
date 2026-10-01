package implementations.pagamento;
import interfaces.MetodoPagamento;

// Implementação específica para pagamento via Pix (Responsabilidade Única)
public class PixPagamento implements MetodoPagamento {
    @Override
    public boolean pagar(Double valor) {
        System.out.println("Gerando QR Code e processando Pix no valor de R$ " + valor);
        return true;
    }

    @Override
    public String getDescricao() {
        return "Pix";
    }
}
