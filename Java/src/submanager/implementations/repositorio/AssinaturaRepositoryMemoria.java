package submanager.implementations.repositorio;

import submanager.interfaces.AssinaturaRepository;
import submanager.model.Assinatura;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
 * Guarda as assinaturas em memória, durante a execução do programa.
*/
public class AssinaturaRepositoryMemoria implements AssinaturaRepository {

    private final Map<Integer, Assinatura> assinaturas = new LinkedHashMap<>();
    private int proximoId = 1;

    @Override
    public int proximoId() {
        return proximoId++;
    }

    @Override
    public void salvar(Assinatura assinatura) {
        Objects.requireNonNull(assinatura, "assinatura é obrigatória");
        assinaturas.put(assinatura.getId(), assinatura);
    }

    @Override
    public List<Assinatura> listarTodas() {
        return new ArrayList<>(assinaturas.values());
    }

    @Override
    public List<Assinatura> listarPorCliente(String cpf) {
        String cpfBuscado = cpf == null ? "" : cpf.trim();
        return assinaturas.values().stream()
                .filter(a -> a.getCliente().getCpf().equals(cpfBuscado))
                .toList();
    }

    @Override
    public List<Assinatura> listarPorStatus(Assinatura.Status status) {
        return assinaturas.values().stream()
                .filter(a -> a.getStatus() == status)
                .toList();
    }
}