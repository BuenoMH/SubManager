package submanager.implementations.repositorio;

import submanager.interfaces.ClienteRepository;
import submanager.model.Cliente;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/*
 * Guarda os clientes em memória, indexados pelo CPF.
 */
public class ClienteRepositoryMemoria implements ClienteRepository {

    private final Map<String, Cliente> clientesPorCpf = new LinkedHashMap<>();
    private int proximoId = 1;

    @Override
    public int proximoId() {
        return proximoId++;
    }

    @Override
    public void salvar(Cliente cliente) {
        Objects.requireNonNull(cliente, "cliente é obrigatório");
        clientesPorCpf.put(cliente.getCpf(), cliente);
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        if (cpf == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(clientesPorCpf.get(cpf.trim()));
    }

    @Override
    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientesPorCpf.values());
    }
}