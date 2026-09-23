package submanager.model;

import java.time.LocalDate;


// Representa um cupom de desconto (ex.: PRIMEIRA10 = 10%).
// O cupom só guarda os dados; o cálculo do desconto fica nas implementações de CalculadoraDesconto.

public class Cupom {

    public enum Tipo {
        PROMOCIONAL,
        CLIENTE_NOVO
    }

    private final String codigo;
    private final Tipo tipo;
    private final double percentual; // 10.0 significa 10%
    private final LocalDate validade;

    public Cupom(String codigo, Tipo tipo, double percentual, LocalDate validade) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("O código do cupom é obrigatório.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo do cupom é obrigatório.");
        }
        if (percentual <= 0 || percentual > 100) {
            throw new IllegalArgumentException("O percentual do cupom deve estar entre 0 e 100.");
        }
        if (validade == null) {
            throw new IllegalArgumentException("A validade do cupom é obrigatória.");
        }

        this.codigo = codigo.trim().toUpperCase();
        this.tipo = tipo;
        this.percentual = percentual;
        this.validade = validade;
    }

    // o cupom vale até o fim do dia da validade (inclusive)
    public boolean estaValido() {
        return estaValido(LocalDate.now());
    }

    public boolean estaValido(LocalDate data) {
        return !data.isAfter(validade);
    }

    public String getCodigo() {
        return codigo;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public double getPercentual() {
        return percentual;
    }

    public LocalDate getValidade() {
        return validade;
    }

    @Override
    public String toString() {
        return String.format("Cupom %s | %.0f%% | %s | válido até %s",
                codigo, percentual, tipo, validade);
    }
}