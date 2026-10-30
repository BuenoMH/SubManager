package submanager.service;

import submanager.interfaces.AssinaturaRepository;
import submanager.interfaces.ClienteRepository;
import submanager.interfaces.PagamentoRepository;
import submanager.model.Assinatura;
import submanager.model.Cliente;
import submanager.model.Pagamento;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

// SRP: só consulta (leitura). Não cria, cobra nem altera nada.
// DIP: depende apenas das interfaces de repositório, recebidas pelo construtor.
public class ConsultaService {

    // Visão consolidada de um cliente
    public record ResumoCliente(Cliente cliente,
                                long assinaturasTotal,
                                long assinaturasAtivas,
                                long assinaturasCanceladas,
                                long pagamentosPendentes) {
        @Override
        public String toString() {
            return String.format("%s%n  Assinaturas: %d no total | %d ativa(s) | %d cancelada(s)%n  Pagamentos pendentes: %d",
                    cliente, assinaturasTotal, assinaturasAtivas, assinaturasCanceladas, pagamentosPendentes);
        }
    }

    private final ClienteRepository clientes;
    private final AssinaturaRepository assinaturas;
    private final PagamentoRepository pagamentos;

    public ConsultaService(ClienteRepository clientes,
                           AssinaturaRepository assinaturas,
                           PagamentoRepository pagamentos) {
        this.clientes = Objects.requireNonNull(clientes, "clientes é obrigatório");
        this.assinaturas = Objects.requireNonNull(assinaturas, "assinaturas é obrigatório");
        this.pagamentos = Objects.requireNonNull(pagamentos, "pagamentos é obrigatório");
    }

    public List<Cliente> clientesCadastrados() {
        return clientes.listarTodos();
    }

    public List<Assinatura> assinaturasDoCliente(String cpf) {
        return assinaturas.listarPorCliente(cpf);
    }

    public List<Assinatura> assinaturasPorStatus(Assinatura.Status status) {
        return assinaturas.listarPorStatus(status);
    }

    public List<Pagamento> pagamentosDoCliente(String cpf) {
        return pagamentos.listarPorCliente(cpf);
    }

    public List<Pagamento> pagamentosDaAssinatura(int idAssinatura) {
        return pagamentos.listarPorAssinatura(idAssinatura);
    }

    public List<Pagamento> pagamentosPorStatus(Pagamento.Status status) {
        return pagamentos.listarPorStatus(status);
    }

    public List<Pagamento> todosOsPagamentos() {
        return pagamentos.listarTodos();
    }

    // Vazio se o CPF não estiver cadastrado.
    public Optional<ResumoCliente> resumoDoCliente(String cpf) {
        return clientes.buscarPorCpf(cpf).map(cliente -> {
            List<Assinatura> doCliente = assinaturas.listarPorCliente(cliente.getCpf());
            long ativas = doCliente.stream().filter(a -> a.getStatus() == Assinatura.Status.ATIVA).count();
            long canceladas = doCliente.stream().filter(a -> a.getStatus() == Assinatura.Status.CANCELADA).count();
            long pendentes = pagamentos.listarPorCliente(cliente.getCpf()).stream()
                    .filter(p -> p.getStatus() == Pagamento.Status.PENDENTE).count();
            return new ResumoCliente(cliente, doCliente.size(), ativas, canceladas, pendentes);
        });
    }

    // Quantas assinaturas existem em cada status (todos os status aparecem, mesmo com zero).
    public Map<Assinatura.Status, Long> contagemDeAssinaturas() {
        Map<Assinatura.Status, Long> contagem = new EnumMap<>(Assinatura.Status.class);
        for (Assinatura.Status status : Assinatura.Status.values()) {
            contagem.put(status, (long) assinaturas.listarPorStatus(status).size());
        }
        return contagem;
    }

    // Quantos pagamentos existem em cada status (todos os status aparecem, mesmo com zero).
    public Map<Pagamento.Status, Long> contagemDePagamentos() {
        Map<Pagamento.Status, Long> contagem = new EnumMap<>(Pagamento.Status.class);
        for (Pagamento.Status status : Pagamento.Status.values()) {
            contagem.put(status, (long) pagamentos.listarPorStatus(status).size());
        }
        return contagem;
    }
}