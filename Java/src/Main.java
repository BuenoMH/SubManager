import submanager.model.Cliente;
import submanager.model.Plano;
import submanager.model.Assinatura;
import submanager.model.Cupom;
import submanager.model.Pagamento;

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

import submanager.strategy.DescontoContexto;
import submanager.strategy.MetodoPagamentoContexto;
import submanager.strategy.NotificadorContexto;

import submanager.interfaces.AssinaturaRepository;
import submanager.interfaces.ClienteRepository;
import submanager.interfaces.PagamentoRepository;
import submanager.implementations.desconto.SemDesconto;
import submanager.implementations.notificacao.EmailNotificador;
import submanager.implementations.pagamento.PixPagamento;
import submanager.implementations.repositorio.AssinaturaRepositoryMemoria;
import submanager.implementations.repositorio.ClienteRepositoryMemoria;
import submanager.implementations.repositorio.PagamentoRepositoryMemoria;

import submanager.service.AssinaturaService;
import submanager.service.ClienteService;
import submanager.service.ConsultaService;
import submanager.service.PagamentoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

// Main: apenas interação com o usuário e montagem (composição) das dependências.
// Nenhuma regra de negócio fica aqui: desconto, cobrança, ativação e notificação são dos Services.
// Padrões usados aqui: Factory Method (via Creators registrados) e Builder (a Main é o Director).
// Adapter e Composite entram como opções do menu, criadas pelos próprios Creators.
// Strategy: a cada operação, pagamento, canal e desconto escolhidos TROCAM as estratégias dos contextos.
// State: renovar, cancelar, expirar e confirmar pagamento mostram as transições do ciclo de vida da assinatura.
// Repositórios (em memória) guardam clientes, assinaturas e pagamentos; o ConsultaService alimenta as consultas.
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

    // Armazenamento compartilhado por todas as operações (somente em memória)
    private static final ClienteRepository REPOSITORIO_CLIENTES = new ClienteRepositoryMemoria();
    private static final AssinaturaRepository REPOSITORIO_ASSINATURAS = new AssinaturaRepositoryMemoria();
    private static final PagamentoRepository REPOSITORIO_PAGAMENTOS = new PagamentoRepositoryMemoria();

    // Contextos Strategy: guardam a estratégia ATUAL e são trocados a cada operação (valores iniciais padrão).
    private static final MetodoPagamentoContexto PAGAMENTO_ATUAL = new MetodoPagamentoContexto(new PixPagamento());
    private static final NotificadorContexto NOTIFICADOR_ATUAL = new NotificadorContexto(new EmailNotificador());
    private static final DescontoContexto DESCONTO_ATUAL = new DescontoContexto(new SemDesconto());

    // Composição das dependências (DIP): os Services só conhecem as abstrações.
    private static final ClienteService CLIENTES = new ClienteService(REPOSITORIO_CLIENTES);
    private static final PagamentoService PAGAMENTO_SERVICE =
            new PagamentoService(PAGAMENTO_ATUAL, NOTIFICADOR_ATUAL, REPOSITORIO_PAGAMENTOS);
    private static final AssinaturaService ASSINATURA_SERVICE =
            new AssinaturaService(PAGAMENTO_SERVICE, NOTIFICADOR_ATUAL, DESCONTO_ATUAL, REPOSITORIO_ASSINATURAS);
    private static final ConsultaService CONSULTAS =
            new ConsultaService(REPOSITORIO_CLIENTES, REPOSITORIO_ASSINATURAS, REPOSITORIO_PAGAMENTOS);

    // Textos dos menus de escolha
    private static final String MENU_NOTIFICACAO = "[1] Email\n[2] WhatsApp\n[3] Email + WhatsApp (ambos)";
    private static final String MENU_DESCONTO =
            "[1] Cliente Novo (15%)\n[2] Cupom Promocional (10%)\n[3] Nenhum\n[4] Cliente Novo + Cupom (cumulativo)";
    private static final String MENU_PAGAMENTO = "[1] PIX\n[2] Cartão de Crédito\n[3] Boleto\n[4] Gateway Externo (PayFlow)";

    public static void main(String[] args) {
        boolean rodando = true;

        while (rodando) {
            exibirMenuPrincipal();
            int opcao = lerOpcao();

            switch (opcao) {
                case 1 -> simularNovaAssinatura();
                case 2 -> renovarAssinatura();
                case 3 -> cancelarAssinatura();
                case 4 -> expirarAssinatura();
                case 5 -> confirmarPagamentosPendentes();
                case 6 -> menuConsultas();
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
        System.out.println("  [2] Renovar Assinatura");
        System.out.println("  [3] Cancelar Assinatura");
        System.out.println("  [4] Simular Expiração de Assinatura");
        System.out.println("  [5] Confirmar Pagamentos Pendentes");
        System.out.println("  [6] Consultas (clientes, assinaturas, pagamentos)");
        System.out.println("  [0] Sair");
        System.out.println("========================================");
        System.out.print("Escolha uma opção: ");
    }

    // ---------------------------------------------------------------- Nova assinatura

    private static void simularNovaAssinatura() {
        System.out.println("\n--- 1. DADOS DO CLIENTE ---");
        Optional<Cliente> cliente = identificarCliente();
        if (cliente.isEmpty()) {
            return;
        }

        // Builder: a solicitação é montada passo a passo, na ordem do menu.
        SolicitacaoAssinaturaBuilder builder = new SolicitacaoAssinaturaBuilder().comCliente(cliente.get());

        System.out.println("\n--- 2. ESCOLHA O PLANO ---");
        System.out.println("[1] Básico (R$ 29,90)\n[2] Premium (R$ 59,90)");
        System.out.print("Opção: ");
        builder.comPlano(selecionarPlano(lerOpcao()));

        coletarEstrategias(builder, 3);

        System.out.println("\n========================================");
        System.out.println("          RESUMO DA ASSINATURA          ");
        System.out.println("========================================");

        try {
            // build() valida todos os passos de uma vez.
            SolicitacaoAssinatura solicitacao = builder.build();
            System.out.println(solicitacao);
            aplicarEstrategias(solicitacao);

            System.out.println("\nRegistrando assinatura e cobrança...\n");
            Assinatura assinatura = ASSINATURA_SERVICE.criarAssinatura(
                    solicitacao.getCliente(), solicitacao.getPlano(), solicitacao.getCupom());

            System.out.println("\n" + assinatura);
            exibirSituacaoDoUltimoPagamento(assinatura);
        } catch (Exception e) {
            System.out.println("\n[ERRO] Falha ao criar a assinatura: " + e.getMessage());
        }
        System.out.println("========================================");
    }

    // O CPF identifica o cliente: se já estiver cadastrado, os dados são reaproveitados.
    private static Optional<Cliente> identificarCliente() {
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();

        Optional<Cliente> existente = CLIENTES.buscarPorCpf(cpf);
        if (existente.isPresent()) {
            System.out.println("[CADASTRO] Cliente já cadastrado, dados reaproveitados: " + existente.get());
            return existente;
        }

        System.out.print("Nome do Cliente: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();

        try {
            Cliente novo = CLIENTES.cadastrar(nome, email, cpf, telefone);
            System.out.println("[CADASTRO] Novo cliente cadastrado: " + novo);
            return Optional.of(novo);
        } catch (IllegalArgumentException e) {
            System.out.println("\n[ERRO] " + e.getMessage());
            pressioneEnterParaContinuar();
            return Optional.empty();
        }
    }

    // ---------------------------------------------------------------- Renovar

    private static void renovarAssinatura() {
        System.out.println("\n--- RENOVAR ASSINATURA ---");
        Optional<Assinatura> escolhida = escolherAssinaturaDoCliente();
        if (escolhida.isEmpty()) {
            return;
        }
        Assinatura assinatura = escolhida.get();

        try {
            assinatura.validarRenovacao(); // State: recusa antes de pedir qualquer outro dado
        } catch (IllegalStateException e) {
            System.out.println("\n[ERRO] " + e.getMessage());
            return;
        }

        SolicitacaoAssinaturaBuilder builder = new SolicitacaoAssinaturaBuilder()
                .comCliente(assinatura.getCliente())
                .comPlano(assinatura.getPlano());
        coletarEstrategias(builder, 1);

        try {
            SolicitacaoAssinatura solicitacao = builder.build();
            aplicarEstrategias(solicitacao);

            Pagamento pagamento = ASSINATURA_SERVICE.renovarAssinatura(assinatura, solicitacao.getCupom());
            System.out.println("\n" + assinatura);
            exibirSituacao(pagamento);
        } catch (Exception e) {
            System.out.println("\n[ERRO] Falha ao renovar a assinatura: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------- Cancelar e expirar

    private static void cancelarAssinatura() {
        System.out.println("\n--- CANCELAR ASSINATURA ---");
        Optional<Assinatura> escolhida = escolherAssinaturaDoCliente();
        if (escolhida.isEmpty()) {
            return;
        }
        escolherCanalDeAviso();

        try {
            ASSINATURA_SERVICE.cancelarAssinatura(escolhida.get());
            System.out.println("\n[SUCESSO] Assinatura cancelada.");
            System.out.println(escolhida.get());
        } catch (IllegalStateException e) {
            System.out.println("\n[ERRO] " + e.getMessage()); // transição inválida do State
        }
    }

    private static void expirarAssinatura() {
        System.out.println("\n--- SIMULAR EXPIRAÇÃO ---");
        Optional<Assinatura> escolhida = escolherAssinaturaDoCliente();
        if (escolhida.isEmpty()) {
            return;
        }
        escolherCanalDeAviso();

        try {
            ASSINATURA_SERVICE.expirarAssinatura(escolhida.get());
            System.out.println("\n[SUCESSO] Assinatura marcada como expirada.");
            System.out.println(escolhida.get());
        } catch (IllegalStateException e) {
            System.out.println("\n[ERRO] " + e.getMessage()); // transição inválida do State
        }
    }

    // ---------------------------------------------------------------- Confirmação de pagamentos

    // Validação manual: lê os pagamentos PENDENTES gravados no repositório e confirma ou recusa um deles.
    // Ao confirmar, a assinatura é efetivada (ativada ou renovada, conforme o estado dela).
    private static void confirmarPagamentosPendentes() {
        List<Pagamento> pendentes = CONSULTAS.pagamentosPorStatus(Pagamento.Status.PENDENTE);
        imprimirLista("Pagamentos pendentes", pendentes);
        if (pendentes.isEmpty()) {
            return;
        }

        System.out.print("ID do pagamento: ");
        int id = lerOpcao();
        Optional<Pagamento> escolhido = pendentes.stream().filter(p -> p.getId() == id).findFirst();
        if (escolhido.isEmpty()) {
            System.out.println("[!] ID inválido.");
            return;
        }
        Pagamento pagamento = escolhido.get();

        System.out.println("[1] Confirmar pagamento\n[2] Recusar pagamento\n[0] Voltar");
        System.out.print("Opção: ");
        int acao = lerOpcao();
        if (acao != 1 && acao != 2) {
            return;
        }
        escolherCanalDeAviso();

        try {
            if (acao == 1) {
                ASSINATURA_SERVICE.confirmarPagamento(pagamento);
                System.out.println("\n[SUCESSO] Pagamento confirmado e assinatura efetivada.");
            } else {
                ASSINATURA_SERVICE.recusarPagamento(pagamento);
                System.out.println("\n[SUCESSO] Pagamento recusado. A assinatura não foi alterada.");
            }
            System.out.println(pagamento.getAssinatura());
        } catch (IllegalStateException e) {
            // Ex.: assinatura CANCELADA não aceita efetivar pagamento; o pagamento continua PENDENTE.
            System.out.println("\n[ERRO] " + e.getMessage() + " Recuse o pagamento se for o caso.");
        }
    }

    // ---------------------------------------------------------------- Consultas

    // Menu de consultas: lê o que foi gravado nos repositórios (via ConsultaService).
    private static void menuConsultas() {
        boolean consultando = true;

        while (consultando) {
            System.out.println("\n========================================");
            System.out.println("   CONSULTAS");
            System.out.println("========================================");
            System.out.println("  [1] Assinaturas de um cliente (CPF)");
            System.out.println("  [2] Assinaturas por status");
            System.out.println("  [3] Pagamentos de um cliente (CPF)");
            System.out.println("  [4] Pagamentos por status (inclui PENDENTE)");
            System.out.println("  [5] Todos os pagamentos");
            System.out.println("  [6] Clientes cadastrados");
            System.out.println("  [7] Resumo de um cliente (CPF)");
            System.out.println("  [8] Painel geral");
            System.out.println("  [0] Voltar ao menu principal");
            System.out.println("========================================");
            System.out.print("Escolha uma opção: ");

            switch (lerOpcao()) {
                case 1 -> {
                    System.out.print("CPF do cliente: ");
                    imprimirLista("Assinaturas do cliente", CONSULTAS.assinaturasDoCliente(scanner.nextLine()));
                }
                case 2 -> {
                    Assinatura.Status status = escolherOpcao(Assinatura.Status.values());
                    if (status != null) {
                        imprimirLista("Assinaturas " + status, CONSULTAS.assinaturasPorStatus(status));
                    }
                }
                case 3 -> {
                    System.out.print("CPF do cliente: ");
                    imprimirLista("Pagamentos do cliente", CONSULTAS.pagamentosDoCliente(scanner.nextLine()));
                }
                case 4 -> {
                    Pagamento.Status status = escolherOpcao(Pagamento.Status.values());
                    if (status != null) {
                        imprimirLista("Pagamentos " + status, CONSULTAS.pagamentosPorStatus(status));
                    }
                }
                case 5 -> imprimirLista("Todos os pagamentos", CONSULTAS.todosOsPagamentos());
                case 6 -> imprimirLista("Clientes cadastrados", CONSULTAS.clientesCadastrados());
                case 7 -> {
                    System.out.print("CPF do cliente: ");
                    Optional<ConsultaService.ResumoCliente> resumo = CONSULTAS.resumoDoCliente(scanner.nextLine());
                    System.out.println("\n--- Resumo do cliente ---");
                    System.out.println(resumo.map(Object::toString).orElse("Cliente não cadastrado."));
                }
                case 8 -> exibirPainelGeral();
                case 0 -> consultando = false;
                default -> System.out.println("\n[!] Opção inválida. Tente novamente.");
            }
        }
    }

    private static void exibirPainelGeral() {
        System.out.println("\n--- Painel geral ---");
        System.out.println("Clientes cadastrados: " + CONSULTAS.clientesCadastrados().size());
        System.out.println("Assinaturas por status: " + formatarContagem(CONSULTAS.contagemDeAssinaturas()));
        System.out.println("Pagamentos por status:  " + formatarContagem(CONSULTAS.contagemDePagamentos()));
    }

    // ---------------------------------------------------------------- Escolhas compartilhadas

    // Passos de notificação, desconto e pagamento (Factory Method), usados ao criar e ao renovar.
    private static void coletarEstrategias(SolicitacaoAssinaturaBuilder builder, int passoInicial) {
        System.out.println("\n--- " + passoInicial + ". MÉTODO DE NOTIFICAÇÃO ---");
        System.out.println(MENU_NOTIFICACAO);
        int opcaoNotificacao = lerOpcaoRegistrada(NOTIFICADORES);
        builder.comNotificador(NOTIFICADORES.obter(opcaoNotificacao).obterNotificador());

        System.out.println("\n--- " + (passoInicial + 1) + ". TIPO DE DESCONTO ---");
        System.out.println(MENU_DESCONTO);
        int opcaoDesconto = lerOpcaoRegistrada(DESCONTOS);
        builder.comDesconto(DESCONTOS.obter(opcaoDesconto).obterCalculador());
        if (OPCOES_DESCONTO_COM_CUPOM.contains(opcaoDesconto)) {
            builder.comCupom(criarCupomPromocional());
        }

        System.out.println("\n--- " + (passoInicial + 2) + ". FORMA DE PAGAMENTO ---");
        System.out.println(MENU_PAGAMENTO);
        int opcaoPagamento = lerOpcaoRegistrada(PAGAMENTOS);
        builder.comMetodoPagamento(PAGAMENTOS.obter(opcaoPagamento).obterMetodo());
    }

    // Strategy em ação: as escolhas do usuário substituem as estratégias dos contextos em tempo de execução.
    private static void aplicarEstrategias(SolicitacaoAssinatura solicitacao) {
        PAGAMENTO_ATUAL.trocarEstrategia(solicitacao.getMetodoPagamento());
        NOTIFICADOR_ATUAL.trocarEstrategia(solicitacao.getNotificador());
        DESCONTO_ATUAL.trocarEstrategia(solicitacao.getCalculadorDesconto());
    }

    // Usado por cancelar, expirar e confirmar pagamento: escolhe por qual canal o cliente será avisado.
    private static void escolherCanalDeAviso() {
        System.out.println("\n--- CANAL PARA AVISAR O CLIENTE ---");
        System.out.println(MENU_NOTIFICACAO);
        int opcao = lerOpcaoRegistrada(NOTIFICADORES);
        NOTIFICADOR_ATUAL.trocarEstrategia(NOTIFICADORES.obter(opcao).obterNotificador());
    }

    // Pede o CPF, lista as assinaturas do cliente e devolve a escolhida pelo ID.
    private static Optional<Assinatura> escolherAssinaturaDoCliente() {
        System.out.print("CPF do cliente: ");
        List<Assinatura> doCliente = CONSULTAS.assinaturasDoCliente(scanner.nextLine());
        if (doCliente.isEmpty()) {
            System.out.println("[!] Nenhuma assinatura encontrada para este CPF.");
            return Optional.empty();
        }
        imprimirLista("Assinaturas do cliente", doCliente);

        System.out.print("ID da assinatura: ");
        int id = lerOpcao();
        Optional<Assinatura> escolhida = doCliente.stream().filter(a -> a.getId() == id).findFirst();
        if (escolhida.isEmpty()) {
            System.out.println("[!] ID inválido.");
        }
        return escolhida;
    }

    private static void exibirSituacaoDoUltimoPagamento(Assinatura assinatura) {
        List<Pagamento> pagamentos = CONSULTAS.pagamentosDaAssinatura(assinatura.getId());
        if (!pagamentos.isEmpty()) {
            exibirSituacao(pagamentos.get(pagamentos.size() - 1));
        }
    }

    private static void exibirSituacao(Pagamento pagamento) {
        String mensagem = switch (pagamento.getStatus()) {
            case PENDENTE -> "[PENDENTE] Pagamento #" + pagamento.getId() + " de R$ "
                    + String.format("%.2f", pagamento.getValor())
                    + " aguardando confirmação. Use a opção [5] do menu principal para validá-lo.";
            case RECUSADO -> "[ERRO] Pagamento #" + pagamento.getId() + " recusado na cobrança.";
            case APROVADO -> "[SUCESSO] Pagamento #" + pagamento.getId() + " aprovado.";
        };
        System.out.println(mensagem);
    }

    // ---------------------------------------------------------------- Utilitários

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

    private static <E extends Enum<E>> String formatarContagem(Map<E, Long> contagem) {
        StringBuilder texto = new StringBuilder();
        contagem.forEach((status, quantidade) -> {
            if (texto.length() > 0) {
                texto.append(" | ");
            }
            texto.append(status).append(": ").append(quantidade);
        });
        return texto.toString();
    }

    // Mostra as constantes de um enum como opções numeradas; devolve null se a escolha for inválida.
    private static <E extends Enum<E>> E escolherOpcao(E[] opcoes) {
        for (int i = 0; i < opcoes.length; i++) {
            System.out.println("[" + (i + 1) + "] " + opcoes[i]);
        }
        System.out.print("Opção: ");
        int escolha = lerOpcao();
        if (escolha < 1 || escolha > opcoes.length) {
            System.out.println("[!] Opção inválida.");
            return null;
        }
        return opcoes[escolha - 1];
    }

    private static <T> void imprimirLista(String titulo, List<T> itens) {
        System.out.println("\n--- " + titulo + " (" + itens.size() + ") ---");
        if (itens.isEmpty()) {
            System.out.println("Nenhum registro encontrado.");
        } else {
            itens.forEach(System.out::println);
        }
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