package service;

import implementations.notificacao.WhatsAppNotificador;
import implementations.pagamento.PixPagamento;
import interfaces.MetodoPagamento;
import interfaces.Notificador;

public class AssinaturaService {
    
    public void criarAssinatura() {
        // Escolhe os métodos que o cliente vai usar
        MetodoPagamento metodoPix = new PixPagamento();
        Notificador notificacaoWhats = new WhatsAppNotificador();
        
        // Injeta as dependências no serviço
        PagamentoService pagamentoService = new PagamentoService(metodoPix, notificacaoWhats);
        
        // Executa a cobrança
        pagamentoService.processarPagamento(99.90, "+5541999999999");
    }
}
