package submanager.desconto;

import submanager.interfaces.CalculadorDesconto;
import submanager.model.Cupom;

// SRP: esta classe faz UMA coisa — calcula desconto baseado em cupom.
// OCP: para um novo tipo de desconto, cria-se uma nova classe (ex.: DescontoFidelidade),
//       sem precisar modificar esta.
// LSP: pode substituir qualquer CalculadorDesconto sem quebrar o comportamento esperado.

public class DescontoCupom implements CalculadorDesconto {

    @Override
    public double calcularDesconto(double valor, Cupom cupom) {
        // Se não há cupom ou ele está vencido, retorna o valor original sem desconto
        if (cupom == null || !cupom.estaValido()) {
            System.out.println("  [DescontoCupom] Cupom inválido ou vencido. Sem desconto aplicado.");
            return valor;
        }

        // Calcula o valor do desconto com base no percentual do cupom
        double valorDesconto = valor * (cupom.getPercentual() / 100.0);

        // Garante que o valor final nunca fique negativo
        double valorFinal = Math.max(valor - valorDesconto, 0);

        System.out.println("  [DescontoCupom] Cupom '" + cupom.getCodigo() + "' aplicado com sucesso.");
        System.out.println("  [DescontoCupom] Desconto de " + String.format("%.0f", cupom.getPercentual()) + "% = R$ " + String.format("%.2f", valorDesconto));
        System.out.println("  [DescontoCupom] Valor final: R$ " + String.format("%.2f", valorFinal));

        return valorFinal;
    }

    @Override
    public String getDescricao() {
        return "Desconto aplicado via cupom promocional";
    }
}
