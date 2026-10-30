package submanager.interfaces;

import submanager.model.Cliente;

import java.util.List;
import java.util.Optional;

/*
 * ISP: contrato mínimo de armazenamento e consulta de clientes.
 * O CPF é a identidade de negócio do cliente: não existem dois clientes com o mesmo CPF.
 */

public interface ClienteRepository {

    /* Gera um identificador único para um novo cliente. */
    int proximoId();

    /* Grava o cliente (insere ou atualiza, usando o CPF como chave). */
    void salvar(Cliente cliente);

    Optional<Cliente> buscarPorCpf(String cpf);

    List<Cliente> listarTodos();
}