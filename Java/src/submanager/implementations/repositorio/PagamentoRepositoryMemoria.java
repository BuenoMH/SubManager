package submanager.implementations.repositorio;

import submanager.interfaces.PagamentoRepository;
import submanager.model.Pagamento;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
 * Guarda os pagamentos em memória, durante a execução do programa.
 */
public class PagamentoRepositoryMemoria implements PagamentoRepository {


    private final Map<Integer, Pagamento> pagamentos = new LinkedHashMap<>();
    private int proximoId = 1;

    @Override
    public int proximoId() {
        return proximoId++;
    }

    @Override
    public void salvar(Pagamento pagamento) {
        Objects.requireNonNull(pagamento, "pagamento é obrigatório");
        pagamentos.put(pagamento.getId(), pagamento);
    }

    @Override
    public List<Pagamento> listarTodos() {
        return new ArrayList<>(pagamentos.values());
    }

    @Override
    public List<Pagamento> listarPorAssinatura(int idAssinatura) {
        return pagamentos.values().stream()
                .filter(p -> p.getAssinatura().getId() == idAssinatura)
                .toList();
    }

    @Override
    public List<Pagamento> listarPorCliente(String cpf) {
        String cpfBuscado = cpf == null ? "" : cpf.trim();
        return pagamentos.values().stream()
                .filter(p -> p.getAssinatura().getCliente().getCpf().equals(cpfBuscado))
                .toList();
    }

    @Override
    public List<Pagamento> listarPorStatus(Pagamento.Status status) {
        return pagamentos.values().stream()
                .filter(p -> p.getStatus() == status)
                .toList();
    }

}