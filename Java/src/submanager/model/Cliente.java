package submanager.model;

// Representa o usuário que possui ou deseja contratar uma assinatura.

public class Cliente {

    private final int id;
    private final String nome;
    private final String email;
    private final String cpf;
    private final String telefone;

    public Cliente(int id, String nome, String email, String cpf, String telefone) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do cliente é obrigatório.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("E-mail do cliente inválido.");
        }
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("O CPF do cliente é obrigatório.");
        }
        if (telefone == null || telefone.isBlank()) {
            throw new IllegalArgumentException("O telefone do cliente é obrigatório.");
        }

        this.id = id;
        this.nome = nome.trim();
        this.email = email.trim();
        this.cpf = cpf.trim();
        this.telefone = telefone.trim();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    @Override
    public String toString() {
        return "Cliente #" + id + " - " + nome + " (" + email + " | " + telefone + ")";
    }
}