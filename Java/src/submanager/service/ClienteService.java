package submanager.service;

import submanager.interfaces.ClienteRepository;
import submanager.model.Cliente;

import java.util.Objects;
import java.util.Optional;

// SRP: só cuida do cadastro de clientes. A validação dos dados continua sendo do modelo Cliente.
// DIP: depende apenas de ClienteRepository, recebido pelo construtor.
public class ClienteService {

    private final ClienteRepository repositorio;

    public ClienteService(ClienteRepository repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "repositorio é obrigatório");
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        return repositorio.buscarPorCpf(cpf);
    }

    // Cadastra um novo cliente. O CPF não pode se repetir; os demais dados são validados pelo modelo Cliente.
    public Cliente cadastrar(String nome, String email, String cpf, String telefone) {
        if (repositorio.buscarPorCpf(cpf).isPresent()) {
            throw new IllegalArgumentException("Já existe um cliente cadastrado com este CPF.");
        }
        Cliente cliente = new Cliente(repositorio.proximoId(), nome, email, cpf, telefone); // valida os dados
        repositorio.salvar(cliente);
        return cliente;
    }
}