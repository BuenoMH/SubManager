package implementations.pagamento;
import interfaces.MetodoPagamento;

// Implementação específica para pagamento via Cartão
public class CartaoPagamento implements MetodoPagamento {
    @Override
    public boolean pagar(Double valor) {
        System.out.println("Comunicando com a operadora do cartão para debitar R$ " + valor);
        return true;
    }

    @Override
    public String getDescricao() {
        return "Cartão de Crédito";
    }
}
