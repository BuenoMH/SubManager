package submanager.factory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/*
 * Associa a opção numérica do menu ao Creator correspondente.
 * É o que substitui os "switch" da Main: para suportar uma nova opção basta
 * registrar um novo Creator (OCP), sem alterar nenhum código existente.
 */
public class RegistroDeCriadores<C> {

    private final String categoria;
    private final Map<Integer, C> criadores = new LinkedHashMap<>();

    public RegistroDeCriadores(String categoria) {
        this.categoria = Objects.requireNonNull(categoria, "categoria é obrigatória");
    }

    public RegistroDeCriadores<C> registrar(int opcao, C criador) {
        Objects.requireNonNull(criador, "criador é obrigatório");
        if (criadores.containsKey(opcao)) {
            throw new IllegalStateException("Opção " + opcao + " já registrada em " + categoria + ".");
        }
        criadores.put(opcao, criador);
        return this;
    }

    public boolean contem(int opcao) {
        return criadores.containsKey(opcao);
    }

    public C obter(int opcao) {
        C criador = criadores.get(opcao);
        if (criador == null) {
            throw new IllegalArgumentException("Opção inválida para " + categoria + ": " + opcao);
        }
        return criador;
    }
}