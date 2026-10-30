import submanager.model.Cliente;
import submanager.model.Plano;
import submanager.model.Assinatura;
import submanager.model.Cupom;

import submanager.builder.SolicitacaoAssinatura;
import submanager.builder.SolicitacaoAssinaturaBuilder;

import submanager.factory.RegistroDeCriadores;
import submanager.factory.pagamento.MetodoPagamentoCreator;
import submanager.factory.pagamento.PixCreator;
import submanager.factory.pagamento.CartaoCreator;
import submanager.factory.pagamento.BoletoCreator;
import submanager.factory.pagamento.GatewayExternoCreator;
import submanager.factory.notificacao.NotificadorCreator;
import submanager.factory.notificacao.EmailCreator;
import submanager.factory.notificacao.WhatsappCreator;
import submanager.factory.notificacao.EmailEWhatsappCreator;
import submanager.factory.desconto.DescontoCreator;
import submanager.factory.desconto.DescontoClienteNovoCreator;
import submanager.factory.desconto.DescontoCupomCreator;
import submanager.factory.desconto.SemDescontoCreator;
import submanager.factory.desconto.ClienteNovoECupomCreator;

import submanager.service.AssinaturaService;
import submanager.service.PagamentoService;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.Set;

// Main: apenas interação com o usuário e montagem (composição) das dependências.
// Nenhuma regra de negócio fica aqui: desconto, cobrança, ativação e notificação são dos Services.
// Padrões usados aqui: Factory Method (via Creators registrados) e Builder (a Main é o Director).
// Adapter e Composite entram como opções do menu, criadas pelos próprios Creators.
public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    // Cupom promocional oferecido no menu (percentual, conforme o modelo Cupom)
    private static final String CODIGO_CUPOM_PROMO = "PROMO10";
    private static final double PERCENTUAL_CUPOM_PROMO = 10.0;
    private static final int VALIDADE_CUPOM_DIAS = 30;

    // Opções do menu de desconto que exigem um cupom (cupom sozinho e cliente novo + cupom)
    private static final int OPCAO_DESCONTO_CUPOM = 2;
    private static final int OPCAO_DESCONTO_CLIENTE_NOVO_E_CUPOM = 4;
    private static final Set<Integer> OPCOES_DESCONTO_COM_CUPOM =
            Set.of(OPCAO_DESCONTO_CUPOM, OPCAO_DESCONTO_CLIENTE_NOVO_E_CUPOM);

    // Opção do menu -> Creator (Factory Method). Substitui os antigos switch.
    private static final RegistroDeCriadores<NotificadorCreator> NOTIFICADORES =
            new RegistroDeCriadores<NotificadorCreator>("notificação")
                    .registrar(1, new EmailCreator())
                    .registrar(2, new WhatsappCreator())
                    .registrar(3, new EmailEWhatsappCreator()); // Composite

    private static final RegistroDeCriadores<DescontoCreator> DESCONTOS =
            new RegistroDeCriadores<DescontoCreator>("desconto")
                    .registrar(1, new DescontoClienteNovoCreator())
                    .registrar(OPCAO_DESCONTO_CUPOM, new DescontoCupomCreator())
                    .registrar(3, new SemDescontoCreator())
                    .registrar(OPCAO_DESCONTO_CLIENTE_NOVO_E_CUPOM, new ClienteNovoECupomCreator()); // Composite

    private static final RegistroDeCriadores<MetodoPagamentoCreator> PAGAMENTOS =
            new RegistroDeCriadores<MetodoPagamentoCreator>("forma de pagamento")
                    .registrar(1, new PixCreator())
                    .registrar(2, new CartaoCreator())
                    .registrar(3, new BoletoCreator())
                    .registrar(4, new GatewayExternoCreator()); // Adapter

    // Contador simples para IDs de cliente (em memória)
    private static int proximoIdCliente = 1;

    public static void main(String[] args) {
        boolean rodando = true;

        while (rodando) {
            exibirMenuPrincipal();
            int opcao = lerOpcao();

            switch (opcao) {
                case 1 -> simularFluxoAssinatura();
                case 0 -> {
                    rodando = false;
                    System.out.println("\nEncerrando o SubManager. Até logo!");
                }
                default -> System.out.println("\n[!] Opção inválida. Tente novamente.");
            }
        }
        scanner.close();
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n========================================");
        System.out.println("       SUBMANAGER - MENU PRINCIPAL      ");
        System.out.println("========================================");
        System.out.println("  [1] Simular Nova Assinatura");
        System.out.println("  [0] Sair");
        System.out.println("========================================");
        System.out.print("Escolha uma opção: ");
    }

    private static void simularFluxoAssinatura() {
        System.out.println("\n--- 1. DADOS DO CLIENTE ---");
        System.out.print("Nome do Cliente: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();

        Cliente cliente;
        try {
            cliente = new Cliente(proximoIdCliente++, nome, email, cpf, telefone);
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERRO] " + e.getMessage());
            pressioneEnterParaContinuar();
            return;
        }

        // Builder: a solicitação é montada passo a passo, na ordem do menu.
        SolicitacaoAssinaturaBuilder builder = new SolicitacaoAssinaturaBuilder().comCliente(cliente);

        System.out.println("\n--- 2. ESCOLHA O PLANO ---");
        System.out.println("[1] Básico (R$ 29,90)\n[2] Premium (R$ 59,90)");
        System.out.print("Opção: ");
        builder.comPlano(selecionarPlano(lerOpcao()));

        System.out.println("\n--- 3. MÉTODO DE NOTIFICAÇÃO ---");
        System.out.println("[1] Email\n[2] WhatsApp\n[3] Email + WhatsApp (ambos)");
        int opcaoNotificacao = lerOpcaoRegistrada(NOTIFICADORES);
        builder.comNotificador(NOTIFICADORES.obter(opcaoNotificacao).obterNotificador());

        System.out.println("\n--- 4. TIPO DE DESCONTO ---");
        System.out.println("[1] Cliente Novo (15%)\n[2] Cupom Promocional (10%)\n[3] Nenhum\n[4] Cliente Novo + Cupom (cumulativo)");
        int opcaoDesconto = lerOpcaoRegistrada(DESCONTOS);
        builder.comDesconto(DESCONTOS.obter(opcaoDesconto).obterCalculador());
        if (OPCOES_DESCONTO_COM_CUPOM.contains(opcaoDesconto)) {
            builder.comCupom(criarCupomPromocional());
        }

        System.out.println("\n--- 5. FORMA DE PAGAMENTO ---");
        System.out.println("[1] PIX\n[2] Cartão de Crédito\n[3] Boleto\n[4] Gateway Externo (PayFlow)");
        int opcaoPagamento = lerOpcaoRegistrada(PAGAMENTOS);
        builder.comMetodoPagamento(PAGAMENTOS.obter(opcaoPagamento).obterMetodo());

        System.out.println("\n========================================");
        System.out.println("          RESUMO DA ASSINATURA          ");
        System.out.println("========================================");

        try {
            // build() valida todos os passos de uma vez.
            SolicitacaoAssinatura solicitacao = builder.build();
            System.out.println(solicitacao);
            System.out.println("\nProcessando assinatura e pagamento...\n");

            // Composição das dependências (DIP): os Services só conhecem as abstrações.
            PagamentoService pagamentoService =
                    new PagamentoService(solicitacao.getMetodoPagamento(), solicitacao.getNotificador());
            AssinaturaService assinaturaService =
                    new AssinaturaService(pagamentoService, solicitacao.getNotificador(), solicitacao.getCalculadorDesconto());

            // O service aplica o desconto, cobra, ativa a assinatura e notifica o cliente.
            Assinatura assinatura = assinaturaService.criarAssinatura(
                    solicitacao.getCliente(), solicitacao.getPlano(), solicitacao.getCupom());

            if (assinatura.getStatus() == Assinatura.Status.ATIVA) {
                System.out.println("\n[SUCESSO] Sua assinatura foi ativada com sucesso!");
            } else {
                System.out.println("\n[ERRO] Falha no pagamento. Assinatura permanece "
                        + assinatura.getStatus() + ".");
            }
            System.out.println(assinatura);
        } catch (Exception e) {
            System.out.println("\n[ERRO] Falha ao criar a assinatura: " + e.getMessage());
        }

        System.out.println("========================================\n");
        pressioneEnterParaContinuar();
    }

    private static Plano selecionarPlano(int opcao) {
        return switch (opcao) {
            case 2 -> new Plano(2, "Premium", "Acesso completo a todos os recursos", 59.90, 1);
            default -> new Plano(1, "Básico", "Acesso aos recursos essenciais", 29.90, 1);
        };
    }

    private static Cupom criarCupomPromocional() {
        return new Cupom(
                CODIGO_CUPOM_PROMO,
                Cupom.Tipo.PROMOCIONAL,
                PERCENTUAL_CUPOM_PROMO,
                LocalDate.now().plusDays(VALIDADE_CUPOM_DIAS));
    }

    // Repete a pergunta até o usuário digitar uma opção registrada.
    private static int lerOpcaoRegistrada(RegistroDeCriadores<?> registro) {
        while (true) {
            System.out.print("Opção: ");
            int opcao = lerOpcao();
            if (registro.contem(opcao)) {
                return opcao;
            }
            System.out.println("[!] Opção inválida. Tente novamente.");
        }
    }

    private static int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void pressioneEnterParaContinuar() {
        System.out.println("Pressione ENTER para voltar ao menu...");
        scanner.nextLine();
    }
}
