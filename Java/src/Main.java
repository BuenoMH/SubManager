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

import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    // Serviços (Injetados com dependências ao longo da execução)
    private static final AssinaturaService assinaturaService = new AssinaturaService(null, null, null);
    private static final PagamentoService pagamentoService = new PagamentoService(null, null);

    public static void main(String[] args) {
        boolean rodando = true;

        while (rodando) {
            exibirMenuPrincipal();
            int opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    simularFluxoAssinatura();
                    break;
                case 0:
                    rodando = false;
                    System.out.println("\nEncerando o SubManager. Até logo!");
                    break;
                default:
                    System.out.println("\n[!] Opção inválida. Tente novamente.");
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

        Cliente cliente = new Cliente(null, nome, email, null, null);

        System.out.println("\n--- 2. ESCOLHA O PLANO ---");
        System.out.println("[1] Básico (R$ 29,90)\n[2] Premium (R$ 59,90)");
        System.out.print("Opção: ");
        int opcaoPlano = lerOpcao();

        Plano plano = (opcaoPlano == 2)
                ? new Plano(null, "Premium", 59.90, null, null)
                : new Plano(null, "Básico", 29.90, null, null);

        System.out.println("\n--- 3. MÉTODO DE NOTIFICAÇÃO ---");
        System.out.println("[1] Email\n[2] WhatsApp");
        System.out.print("Opção: ");
        int opcaoNotificacao = lerOpcao();
        Notificador notificador = (opcaoNotificacao == 2) ? new WhatsappNotificador() : new EmailNotificador();

        System.out.println("\n--- 4. TIPO DE DESCONTO ---");
        System.out.println("[1] Cliente Novo (10%)\n[2] Cupom Promocional (R$ 15,00)\n[3] Nenhum");
        System.out.print("Opção: ");
        int opcaoDesconto = lerOpcao();

        Cupom cupomAplicado = null; // Variável para armazenar o cupom, se houver
        CalculadorDesconto calculadorDesconto = selecionarDesconto(opcaoDesconto);

        if (opcaoDesconto == 2) {
            cupomAplicado = new Cupom(null, "PROMO15", 15.0, null);
        }

        System.out.println("\n--- 5. FORMA DE PAGAMENTO ---");
        System.out.println("[1] PIX\n[2] Cartão de Crédito\n[3] Boleto");
        System.out.print("Opção: ");
        int opcaoPagamento = lerOpcao();
        MetodoPagamento metodoPagamento = selecionarMetodoPagamento(opcaoPagamento);

        System.out.println("\n========================================");
        System.out.println("          RESUMO DA ASSINATURA          ");
        System.out.println("========================================");

        Assinatura assinatura = assinaturaService.criarAssinatura(cliente, plano, cupomAplicado);

        System.out.println("Assinatura criada e aguardando pagamento...");

        try {
            pagamentoService.processarPagamento(assinatura, metodoPagamento);


            String mensagem = "Sua assinatura foi ativada com sucesso!";
            notificador.enviar(mensagem, cliente);

            System.out.println("\n[SUCESSO] " + mensagem);
        } catch (Exception e) {
            notificador.enviar("Houve um problema ao processar seu pagamento.", cliente);
            System.out.println("\n[ERRO] Falha no pagamento.");
        }

        System.out.println("========================================\n");
        pressioneEnterParaContinuar();
    }

    private static CalculadorDesconto selecionarDesconto(int opcao) {
        switch (opcao) {
            case 1:
                return new DescontoClienteNovo();
            case 2:

                return new DescontoCupom();
            default:
                return (valor, paam2) -> valor; // Retorna o próprio valor (Sem desconto - Lambda simples)
        }
    }

    private static MetodoPagamento selecionarMetodoPagamento(int opcao) {
        switch (opcao) {
            case 2:
                return new CartaoPagamento();
            case 3:
                return new BoletoPagamento();
            case 1:
            default:
                return new PixPagamento();
        }
    }

    private static int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void pressioneEnterParaContinuar() {
        System.out.println("Pressione ENTER para voltar ao menu...");
        scanner.nextLine();
    }
}