package service;
import interfaces.MetodoPagamento;
import interfaces.Notificador;

// Orquestra o pagamento confiando apenas nas interfaces (Inversão de Dependência)
public class PagamentoService {
    
    private final MetodoPagamento metodoPagamento;
    private final Notificador notificador;

    // Recebe as ferramentas prontas de fora, não cria elas aqui dentro
    public PagamentoService(MetodoPagamento metodoPagamento, Notificador notificador) {
        this.metodoPagamento = metodoPagamento;
        this.notificador = notificador;
    }

    // Processa a cobrança e já dispara a notificação
    public boolean processarPagamento(Double valor, String destinatario) {
        System.out.println("Iniciando o processamento do pagamento...");
        
        boolean aprovado = metodoPagamento.pagar(valor);
        
        if (aprovado) {
            String mensagemSucesso = "Pagamento de R$ " + valor + " aprovado via " + metodoPagamento.getDescricao() + ".";
            notificador.enviar(mensagemSucesso, destinatario);
            gerarComprovante(); 
        } else {
            notificador.enviar("Falha ao processar seu pagamento de R$ " + valor + ".", destinatario);
        }
        
        return aprovado;
    }
    
    public void gerarComprovante() {
        System.out.println("Gerando comprovante de pagamento no sistema...");
    }

    public void verificarStatus() {
        System.out.println("Consultando status da transação financeira...");
    }
}
