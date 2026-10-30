package submanager.model;

import submanager.interfaces.MetodoPagamento;

import java.time.LocalDateTime;

//Representa um pagamento relacionado a uma assinatura.
// É apenas o registro do pagamento: quem processa a cobrança de fato é o MetodoPagamento (Pix, Cartão, Boleto), coordenado pelo PagamentoService.

public class Pagamento {

    public enum Status {
        PENDENTE,
        APROVADO,
        RECUSADO
    }

    private final int id;
    private final Assinatura assinatura;
    private final MetodoPagamento metodo;
    private final String descricaoMetodo;
    private final double valor;
    private Status status;
    private LocalDateTime dataPagamento; // só é preenchida quando o pagamento é aprovado


    //pagamento ja nasce pendente

    public Pagamento(int id, Assinatura assinatura, MetodoPagamento metodo, double valor) {
        if (assinatura == null) {
            throw new IllegalArgumentException("O pagamento precisa de uma assinatura.");
        }
        if (metodo == null) {
            throw new IllegalArgumentException("O pagamento precisa de um método de pagamento.");
        }
        if (valor < 0) {
            throw new IllegalArgumentException("O valor do pagamento não pode ser negativo.");
        }

        this.id = id;
        this.assinatura = assinatura;
        this.metodo = metodo;
        this.descricaoMetodo = metodo.getDescricao();
        this.valor = valor;
        this.status = Status.PENDENTE;
        this.dataPagamento = null;
    }

    // PENDENTE -> APROVADO. Registra data e hora do pagamento.
    public void aprovar() {
        validarPendente();
        status = Status.APROVADO;
        dataPagamento = LocalDateTime.now();
    }

    // PENDENTE -> RECUSADO.
    public void recusar() {
        validarPendente();
        status = Status.RECUSADO;
    }

    // Lança exceção se o pagamento já foi finalizado (APROVADO ou RECUSADO).
    public void validarPendente() {
        if (status != Status.PENDENTE) {
            throw new IllegalStateException(
                    "O pagamento já foi finalizado (status atual: " + status + ").");
        }
    }

    public int getId() {
        return id;
    }

    public Assinatura getAssinatura() {
        return assinatura;
    }

    public String getDescricaoMetodo() {
        return descricaoMetodo;
    }

    public double getValor() {
        return valor;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isAprovado() {
        return status == Status.APROVADO;
    }

    public boolean isPendente() {
        return status == Status.PENDENTE;
    }

    // Retorna null enquanto o pagamento não for aprovado.
    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    @Override
    public String toString() {
        return String.format("Pagamento #%d | Assinatura #%d | R$ %.2f | %s | %s",
                id, assinatura.getId(), valor, descricaoMetodo, status);
    }
}