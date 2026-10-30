
package submanager.model;

import submanager.state.EstadoAssinatura;
import submanager.state.PendenteState;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Representa a contratação de um plano por um cliente.
 * O ciclo de vida (PENDENTE, ATIVA, CANCELADA, EXPIRADA) é delegado ao
 * {@link EstadoAssinatura} atual: esta classe não tem nenhum "if" de status.
 * NÃO processa pagamento nem envia notificação: isso é papel dos Services.
 *
 * @note Papel no Padrão: Context (State)
 */
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
    private EstadoAssinatura estado;

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
        this.estado = new PendenteState();
    }

    // Ciclo de vida: cada operação é delegada ao estado atual

    // PENDENTE -> ATIVA. Reinicia o período a partir de hoje.
    public void ativar() {
        estado.ativar(this);
    }

    // Cancela a assinatura (qualquer estado, exceto se já estiver cancelada).
    public void cancelar() {
        estado.cancelar(this);
    }

    // ATIVA -> EXPIRADA. Usada quando o período termina (ou para simular o fim do período).
    public void expirar() {
        estado.expirar(this);
    }

    // Renova por mais um período do plano.
    // ATIVA: o novo período começa ao fim do atual. EXPIRADA: começa hoje.
    // PENDENTE ou CANCELADA: não pode ser renovada.
    public void renovar() {
        verificarExpiracao();
        estado.renovar(this);
    }

    // Lança exceção se a assinatura não puder ser renovada (PENDENTE ou CANCELADA).
    // Público para que o AssinaturaService valide ANTES de cobrar o cliente.
    public void validarRenovacao() {
        estado.validarRenovacao();
    }

    // Se estiver ATIVA e a data final já passou, passa para EXPIRADA.
    public void verificarExpiracao() {
        estado.verificarExpiracao(this);
    }

    // Operações usadas pelos estados concretos (submanager.state) para executar as transições.
    // Não fazem parte da API do restante do sistema: quem muda o status é sempre uma operação do ciclo de vida.

    public void mudarEstado(EstadoAssinatura novoEstado) {
        if (novoEstado == null) {
            throw new IllegalArgumentException("O novo estado é obrigatório.");
        }
        System.out.println("[STATE] Assinatura #" + id + ": "
                + estado.getStatus() + " -> " + novoEstado.getStatus());
        this.estado = novoEstado;
    }

    public void reiniciarPeriodo() {
        dataInicio = LocalDate.now();
        dataFinal = dataInicio.plusMonths(plano.getPeriodo());
    }

    public void prolongarPeriodo() {
        dataFinal = dataFinal.plusMonths(plano.getPeriodo());
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
        return estado.getStatus();
    }

    @Override
    public String toString() {
        return String.format("Assinatura #%d | %s | Plano %s | %s a %s | %s",
                id, cliente.getNome(), plano.getNome(),
                dataInicio.format(FORMATO), dataFinal.format(FORMATO), getStatus());
    }
}
