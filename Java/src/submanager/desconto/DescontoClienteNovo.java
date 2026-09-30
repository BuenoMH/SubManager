package submanager.desconto;

import submanager.interfaces.CalculadorDesconto;
import submanager.model.Cupom;

// SRP: esta classe faz UMA coisa — aplica desconto fixo de 15% para clientes novos.
// OCP: se surgir outro tipo de desconto (ex.: DescontoFidelidade), basta criar
//       uma nova classe que implementa CalculadorDesconto. Não precisa mexer aqui.
// LSP: pode ser usada no lugar de qualquer CalculadorDesconto sem surpresas.

public class DescontoClienteNovo implements CalculadorDesconto {

    // Percentual fixo de desconto para novos clientes
    private static final double PERCENTUAL_CLIENTE_NOVO = 15.0;

    @Override
    public double calcularDesconto(double valor, Cupom cupom) {
        // Este desconto não depende de cupom — é automático para clientes novos.
        // O parâmetro cupom é ignorado (a interface exige, mas cada implementação
        // decide se usa ou não).

        double valorDesconto = valor * (PERCENTUAL_CLIENTE_NOVO / 100.0);
        double valorFinal = Math.max(valor - valorDesconto, 0);

        System.out.println("  [DescontoClienteNovo] Desconto de " + String.format("%.0f", PERCENTUAL_CLIENTE_NOVO) + "% para novo cliente.");
        System.out.println("  [DescontoClienteNovo] Desconto: R$ " + String.format("%.2f", valorDesconto));
        System.out.println("  [DescontoClienteNovo] Valor final: R$ " + String.format("%.2f", valorFinal));

        return valorFinal;
    }

    @Override
    public String getDescricao() {
        return "Desconto automático de " + String.format("%.0f", PERCENTUAL_CLIENTE_NOVO) + "% para cliente novo";
    }
}
