import submanager.model.Cliente;
import submanager.model.Plano;
import submanager.model.Assinatura;
import submanager.model.Cupom;

import submanager.interfaces.MetodoPagamento;
import submanager.interfaces.Notificador;
import submanager.interfaces.CalculadorDesconto;

import submanager.implementations.pagamento.PixPagamento;
import submanager.implementations.pagamento.CartaoPagamento;
import submanager.implementations.pagamento.BoletoPagamento;

import submanager.implementations.notificacao.EmailNotificador;
import submanager.implementations.notificacao.WhatsappNotificador;

import submanager.implementations.desconto.DescontoCupom;
import submanager.implementations.desconto.DescontoClienteNovo;

import submanager.service.AssinaturaService;
import submanager.service.PagamentoService;

import java.time.LocalDate;
import java.util.Scanner;

// Main: apenas interação com o usuário e montagem (composição) das dependências.
// Nenhuma regra de negócio fica aqui: desconto, cobrança, ativação e notificação são dos Services.
public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    // Cupom promocional oferecido no menu (percentual, conforme o modelo Cupom)
    private static final String CODIGO_CUPOM_PROMO = "PROMO10";
    private static final double PERCENTUAL_CUPOM_PROMO = 10.0;
    private static final int VALIDADE_CUPOM_DIAS = 30;

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

        System.out.println("\n--- 2. ESCOLHA O PLANO ---");
        System.out.println("[1] Básico (R$ 29,90)\n[2] Premium (R$ 59,90)");
        System.out.print("Opção: ");
        int opcaoPlano = lerOpcao();
        Plano plano = selecionarPlano(opcaoPlano);

        System.out.println("\n--- 3. MÉTODO DE NOTIFICAÇÃO ---");
        System.out.println("[1] Email\n[2] WhatsApp");
        System.out.print("Opção: ");
        int opcaoNotificacao = lerOpcao();
        Notificador notificador = selecionarNotificador(opcaoNotificacao);

        System.out.println("\n--- 4. TIPO DE DESCONTO ---");
        System.out.println("[1] Cliente Novo (15%)\n[2] Cupom Promocional (10%)\n[3] Nenhum");
        System.out.print("Opção: ");
        int opcaoDesconto = lerOpcao();
        CalculadorDesconto calculadorDesconto = selecionarDesconto(opcaoDesconto);
        Cupom cupomAplicado = (opcaoDesconto == 2) ? criarCupomPromocional() : null;

        System.out.println("\n--- 5. FORMA DE PAGAMENTO ---");
        System.out.println("[1] PIX\n[2] Cartão de Crédito\n[3] Boleto");
        System.out.print("Opção: ");
        int opcaoPagamento = lerOpcao();
        MetodoPagamento metodoPagamento = selecionarMetodoPagamento(opcaoPagamento);

        // Composição das dependências (DIP): os Services só conhecem as abstrações.
        PagamentoService pagamentoService = new PagamentoService(metodoPagamento, notificador);
        AssinaturaService assinaturaService = new AssinaturaService(pagamentoService, notificador, calculadorDesconto);

        System.out.println("\n========================================");
        System.out.println("          RESUMO DA ASSINATURA          ");
        System.out.println("========================================");
        System.out.println("Processando assinatura e pagamento...\n");

        try {
            // O service aplica o desconto, cobra, ativa a assinatura e notifica o cliente.
            Assinatura assinatura = assinaturaService.criarAssinatura(cliente, plano, cupomAplicado);

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

    private static Notificador selecionarNotificador(int opcao) {
        return switch (opcao) {
            case 2 -> new WhatsappNotificador();
            default -> new EmailNotificador();
        };
    }

    private static CalculadorDesconto selecionarDesconto(int opcao) {
        return switch (opcao) {
            case 1 -> new DescontoClienteNovo();
            case 2 -> new DescontoCupom();
            default -> (valor, cupom) -> 0.0; // Sem desconto: o calculador devolve o VALOR DO DESCONTO (zero)
        };
    }

    private static Cupom criarCupomPromocional() {
        return new Cupom(
                CODIGO_CUPOM_PROMO,
                Cupom.Tipo.PROMOCIONAL,
                PERCENTUAL_CUPOM_PROMO,
                LocalDate.now().plusDays(VALIDADE_CUPOM_DIAS));
    }

    private static MetodoPagamento selecionarMetodoPagamento(int opcao) {
        return switch (opcao) {
            case 2 -> new CartaoPagamento();
            case 3 -> new BoletoPagamento();
            default -> new PixPagamento();
        };
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