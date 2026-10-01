package interfaces;

// Contrato base para qualquer forma de pagamento (Aberto/Fechado)
public interface MetodoPagamento {
    boolean pagar(Double valor);
    String getDescricao();
}
