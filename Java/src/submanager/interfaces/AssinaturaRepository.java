package submanager.interfaces;

import submanager.model.Assinatura;

import java.util.List;

/*
 * ISP: contrato mínimo de armazenamento e consulta de assinaturas.
 * Os Services dependem apenas desta abstração; onde os dados ficam (memória,
 * arquivo, banco) é decidido por quem monta o sistema.
 */
public interface AssinaturaRepository {

    /* Gera um identificador único (substitui os contadores soltos dentro dos Services). */
    int proximoId();

    /* Grava a assinatura (insere ou atualiza). Deve ser chamada a cada mudança de estado. */
    void salvar(Assinatura assinatura);

    List<Assinatura> listarTodas();

    /* Assinaturas de um cliente, identificado pelo CPF. */
    List<Assinatura> listarPorCliente(String cpf);

    List<Assinatura> listarPorStatus(Assinatura.Status status);
}