package implementations.pagamento;
import interfaces.MetodoPagamento;

// Implementação específica para pagamento via Boleto
public class BoletoPagamento implements MetodoPagamento {
    @Override
    public boolean pagar(Double valor) {
        System.out.println("Gerando código de barras para boleto no valor de R$ " + valor);
        return true;
    }

    @Override
    public String getDescricao() {
        return "Boleto Bancário";
    }
}
