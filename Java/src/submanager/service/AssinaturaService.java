package submanager.service;

import submanager.interfaces.CalculadorDesconto;
import submanager.interfaces.Notificador;
import submanager.model.Assinatura;
import submanager.model.Cliente;
import submanager.model.Cupom;
import submanager.model.Plano;

// SRP: esta classe faz UMA coisa — coordena o ciclo de vida das assinaturas.
//      Ela NÃO calcula desconto (delega para CalculadorDesconto),
//      NÃO processa pagamento (delega para PagamentoService),
//      NÃO envia notificação (delega para Notificador).
//
// DIP: depende das ABSTRAÇÕES (CalculadorDesconto, Notificador, PagamentoService),
//      nunca das implementações concretas.
//      As dependências são injetadas pelo construtor.
//
// OCP: se surgir um novo tipo de desconto, notificador ou pagamento,
//      basta criar uma nova implementação — não precisa alterar esta classe.

public class AssinaturaService {

    private final PagamentoService pagamentoService;
    private final Notificador notificador;
    private final CalculadorDesconto desconto;

    // Contador simples para gerar IDs de assinatura (em memória)
    private int proximoId = 1;

    // Injeção de dependências via construtor (DIP)
    public AssinaturaService(PagamentoService pagamentoService, Notificador notificador, CalculadorDesconto desconto) {
        this.pagamentoService = pagamentoService;
        this.notificador = notificador;
        this.desconto = desconto;
    }

    // =========================================================================
    // criarAssinatura — fluxo completo: desconto → pagamento → ativação → notificação
    // =========================================================================
    public Assinatura criarAssinatura(Cliente cliente, Plano plano, Cupom cupom) {
        System.out.println("\n>> Iniciando criação de assinatura...");
        System.out.println("   Cliente: " + cliente.getNome());
        System.out.println("   Plano: " + plano.getNome() + " — R$ " + String.format("%.2f", plano.getValor()));

        // 1. Calcula o valor com desconto (delega para CalculadorDesconto — DIP)
        double valorOriginal = plano.getValor();
        double valorFinal = desconto.calcularDesconto(valorOriginal, cupom);

        System.out.println("   Tipo de desconto: " + desconto.getDescricao());
        System.out.println("   Valor original: R$ " + String.format("%.2f", valorOriginal));
        System.out.println("   Valor final: R$ " + String.format("%.2f", valorFinal));

        // 2. Cria a assinatura (nasce PENDENTE)
        Assinatura assinatura = new Assinatura(proximoId++, cliente, plano);

        // 3. Processa o pagamento (delega para PagamentoService — DIP)
        if (pagamentoService != null) {
            boolean pagamentoAprovado = pagamentoService.processarPagamento(assinatura, valorFinal);

            if (!pagamentoAprovado) {
                System.out.println("   ✗ Pagamento recusado. Assinatura não foi ativada.");
                return assinatura; // retorna com status PENDENTE
            }
        }

        // 4. Ativa a assinatura após pagamento aprovado
        assinatura.ativar();
        System.out.println("   ✓ Assinatura ativada: " + assinatura);

        // 5. Envia notificação (delega para Notificador — DIP)
        notificador.enviar(
                "Olá " + cliente.getNome() + "! Sua assinatura do plano "
                        + plano.getNome() + " foi criada com sucesso. Valor: R$ "
                        + String.format("%.2f", valorFinal),
                cliente.getEmail(),
                "Assinatura criada"
        );

        return assinatura;
    }

    // Sobrecarga: criar assinatura sem cupom
    public Assinatura criarAssinatura(Cliente cliente, Plano plano) {
        return criarAssinatura(cliente, plano, null);
    }

    // =========================================================================
    // renovarAssinatura — renova por mais um período do plano
    // =========================================================================
    public void renovarAssinatura(Assinatura assinatura) {
        System.out.println("\n>> Iniciando renovação de assinatura...");
        System.out.println("   Assinatura: " + assinatura);

        // 1. Calcula o valor da renovação (pode ter desconto de fidelidade no futuro — OCP)
        double valorRenovacao = assinatura.getPlano().getValor();

        // 2. Processa o pagamento da renovação
        if (pagamentoService != null) {
            boolean pagamentoAprovado = pagamentoService.processarPagamento(assinatura, valorRenovacao);

            if (!pagamentoAprovado) {
                System.out.println("   ✗ Pagamento da renovação recusado.");
                return;
            }
        }

        // 3. Renova a assinatura (delega para o próprio model Assinatura)
        assinatura.renovar();
        System.out.println("   ✓ Assinatura renovada: " + assinatura);

        // 4. Notifica o cliente
        Cliente cliente = assinatura.getCliente();
        notificador.enviar(
                "Olá " + cliente.getNome() + "! Sua assinatura do plano "
                        + assinatura.getPlano().getNome() + " foi renovada com sucesso. "
                        + "Nova data de vencimento: " + assinatura.getDataFinal(),
                cliente.getEmail(),
                "Assinatura renovada"
        );
    }

    // =========================================================================
    // cancelarAssinatura — cancela e notifica o cliente
    // =========================================================================
    public void cancelarAssinatura(Assinatura assinatura) {
        System.out.println("\n>> Iniciando cancelamento de assinatura...");
        System.out.println("   Assinatura: " + assinatura);

        // 1. Cancela a assinatura (delega para o próprio model Assinatura)
        assinatura.cancelar();
        System.out.println("   ✓ Assinatura cancelada: " + assinatura);

        // 2. Notifica o cliente
        Cliente cliente = assinatura.getCliente();
        notificador.enviar(
                "Olá " + cliente.getNome() + ". Sua assinatura do plano "
                        + assinatura.getPlano().getNome() + " foi cancelada. "
                        + "Esperamos vê-lo de volta em breve!",
                cliente.getEmail(),
                "Assinatura cancelada"
        );
    }
}
