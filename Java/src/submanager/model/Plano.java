package submanager.model;

// Representa um plano disponível para contratação (Básico, Premium, Empresarial...)
public class Plano {

    private final int id;
    private final String nome;
    private final String descricao;
    private final double valor;
    private final int periodo; // duração do plano, em meses

    public Plano(int id, String nome, String descricao, double valor, int periodo) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do plano é obrigatório.");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do plano deve ser maior que zero.");
        }
        if (periodo <= 0) {
            throw new IllegalArgumentException("O período do plano deve ser de pelo menos 1 mês.");
        }

        this.id = id;
        this.nome = nome.trim();
        this.descricao = descricao == null ? "" : descricao.trim();
        this.valor = valor;
        this.periodo = periodo;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getValor() {
        return valor;
    }

    // Duração do plano em meses.
    public int getPeriodo() {
        return periodo;
    }

    @Override
    public String toString() {
        return String.format("Plano #%d - %s | R$ %.2f | %d mês(es) | %s",
                id, nome, valor, periodo, descricao);
    }
}