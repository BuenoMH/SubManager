package submanager.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//  Representa a contratação de um plano por um cliente.
// Controla o próprio ciclo de vida (ativar, renovar, cancelar), mas NÃO processa pagamento nem envia notificação: isso é papel dos Services.

public class Assinatura {

    public enum Status {
        ATIVA,
        PENDENTE,
        CANCELADA,
        EXPIRADA
    }

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final int id;
    private final Cliente cliente;
    private final Plano plano;
    private LocalDate dataInicio;
    private LocalDate dataFinal;
    private Status status;

    // Toda assinatura nasce PENDENTE, até o pagamento ser aprovado e ela ser ativada.
    public Assinatura(int id, Cliente cliente, Plano plano) {
        if (cliente == null) {
            throw new IllegalArgumentException("A assinatura precisa de um cliente.");
        }
        if (plano == null) {
            throw new IllegalArgumentException("A assinatura precisa de um plano.");
        }

        this.id = id;
        this.cliente = cliente;
        this.plano = plano;
        this.dataInicio = LocalDate.now();
        this.dataFinal = dataInicio.plusMonths(plano.getPeriodo());
        this.status = Status.PENDENTE;
    }

    // Ciclo de vida

    // PENDENTE -> ATIVA. Reinicia o período a partir de hoje
    public void ativar() {
        if (status != Status.PENDENTE) {
            throw new IllegalStateException(
                    "Só é possível ativar uma assinatura PENDENTE (status atual: " + status + ").");
        }
        dataInicio = LocalDate.now();
        dataFinal = dataInicio.plusMonths(plano.getPeriodo());
        status = Status.ATIVA;
    }

    // Cancela a assinatura (qualquer status, exceto se já estiver cancelada).
    public void cancelar() {
        if (status == Status.CANCELADA) {
            throw new IllegalStateException("A assinatura já está cancelada.");
        }
        status = Status.CANCELADA;
    }

    // Renova a assinatura por mais um período do plano.
    // ATIVA: o novo período começa ao fim do atual.
    // EXPIRADA: o novo período começa hoje.
    // PENDENTE ou CANCELADA: não pode ser renovada.

    public void renovar() {
        verificarExpiracao();

        if (status == Status.PENDENTE || status == Status.CANCELADA) {
            throw new IllegalStateException(
                    "Não é possível renovar uma assinatura " + status + ".");
        }

        if (status == Status.EXPIRADA) {
            dataInicio = LocalDate.now();
            dataFinal = dataInicio.plusMonths(plano.getPeriodo());
        } else {
            dataFinal = dataFinal.plusMonths(plano.getPeriodo());
        }
        status = Status.ATIVA;
    }

    // Se estiver ATIVA e a data final já passou, marca como EXPIRADA.
    public void verificarExpiracao() {
        if (status == Status.ATIVA && LocalDate.now().isAfter(dataFinal)) {
            status = Status.EXPIRADA;
        }
    }

    // Getters

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Plano getPlano() {
        return plano;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFinal() {
        return dataFinal;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("Assinatura #%d | %s | Plano %s | %s a %s | %s",
                id, cliente.getNome(), plano.getNome(),
                dataInicio.format(FORMATO), dataFinal.format(FORMATO), status);
    }
}