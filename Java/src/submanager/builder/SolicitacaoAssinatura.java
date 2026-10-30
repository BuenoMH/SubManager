package submanager.builder;

import submanager.interfaces.CalculadorDesconto;
import submanager.interfaces.MetodoPagamento;
import submanager.interfaces.Notificador;
import submanager.model.Cliente;
import submanager.model.Cupom;
import submanager.model.Plano;

/*
 * Solicitação de assinatura já validada: tudo o que o menu coletou do usuário.
 * Imutável e com construtor restrito ao pacote: só o {@link SolicitacaoAssinaturaBuilder}
 * consegue criá-la, então toda instância existente é válida.
*/
public final class SolicitacaoAssinatura {

    private final Cliente cliente;
    private final Plano plano;
    private final Notificador notificador;
    private final CalculadorDesconto calculadorDesconto;
    private final Cupom cupom; // opcional (pode ser null)
    private final MetodoPagamento metodoPagamento;

    SolicitacaoAssinatura(Cliente cliente, Plano plano, Notificador notificador,
                          CalculadorDesconto calculadorDesconto, Cupom cupom,
                          MetodoPagamento metodoPagamento) {
        this.cliente = cliente;
        this.plano = plano;
        this.notificador = notificador;
        this.calculadorDesconto = calculadorDesconto;
        this.cupom = cupom;
        this.metodoPagamento = metodoPagamento;
    }

    public Cliente getCliente() { return cliente; }
    public Plano getPlano() { return plano; }
    public Notificador getNotificador() { return notificador; }
    public CalculadorDesconto getCalculadorDesconto() { return calculadorDesconto; }
    public Cupom getCupom() { return cupom; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }

    @Override
    public String toString() {
        return String.format(
                "Solicitação | %s | Plano %s (R$ %.2f) | Notificação: %s | Desconto: %s%s | Pagamento: %s",
                cliente.getNome(), plano.getNome(), plano.getValor(),
                notificador.getClass().getSimpleName(),
                calculadorDesconto.getClass().getSimpleName(),
                cupom != null ? " (" + cupom.getCodigo() + ")" : "",
                metodoPagamento.getDescricao());
    }
}