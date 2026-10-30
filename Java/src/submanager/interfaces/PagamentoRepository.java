package submanager.interfaces;

import submanager.model.Pagamento;

import java.util.List;

/*
 * ISP: contrato mínimo de armazenamento e consulta de pagamentos.
 */
public interface PagamentoRepository {

    int proximoId();

    /* Grava o pagamento (insere ou atualiza). */
    void salvar(Pagamento pagamento);

    List<Pagamento> listarPorStatus(Pagamento.Status status);

    List<Pagamento> listarTodos();

    List<Pagamento> listarPorAssinatura(int idAssinatura);

    /* Pagamentos de um cliente, identificado pelo CPF. */
    List<Pagamento> listarPorCliente(String cpf);
}