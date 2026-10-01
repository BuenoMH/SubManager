# SubManager

Sistema de gerenciamento de assinaturas desenvolvido em **Java**, com foco na aplicação prática dos princípios **SOLID**.

O programa simula uma plataforma em que um cliente contrata um plano, escolhe como ser notificado, aplica um desconto (ou não) e paga por Pix, cartão ou boleto. Tudo roda pelo **terminal**, usando `Scanner`, e os dados ficam apenas **em memória** (sem banco de dados e sem frameworks externos).

---

## Sumário

1. [Como executar](#-como-executar)
2. [Funcionamento do programa](#-funcionamento-do-programa)
3. [Arquitetura](#-arquitetura)
4. [Fluxo de execução](#-fluxo-de-execução)
5. [Camadas e responsabilidades](#-camadas-e-responsabilidades)
6. [SOLID no projeto](#-solid-no-projeto)
7. [Como estender o sistema](#-como-estender-o-sistema)
8. [Decisões de projeto e limitações](#-decisões-de-projeto-e-limitações)
9. [Equipe](#-equipe)

---

##  Como executar

**Requisitos:** JDK 17 ou superior (o `Main` usa `switch` com seta e `switch` como expressão) e, de preferência, o IntelliJ IDEA.

**Pelo IntelliJ**

1. Abra a pasta do projeto.
2. Marque `Java/src` como *Sources Root*.
3. Execute a classe `Main` (ela não tem `package`, fica direto em `src/`).

**Pelo terminal**

```bash
cd Java/src
javac -d out $(find . -name "*.java")
java -cp out Main
```

---

##  Funcionamento do programa

Ao iniciar, o programa mostra o menu principal:

```text
========================================
       SUBMANAGER - MENU PRINCIPAL
========================================
  [1] Simular Nova Assinatura
  [0] Sair
========================================
Escolha uma opção:
```

Ao escolher `[1]`, o usuário passa por cinco etapas:

| Etapa | O que é pedido | Opções |
| ----- | -------------- | ------ |
| 1. Dados do cliente | Nome, e-mail, CPF e telefone | Texto livre (validado pelo modelo `Cliente`) |
| 2. Plano | Plano a contratar | `[1]` Básico (R$ 29,90) · `[2]` Premium (R$ 59,90) |
| 3. Notificação | Canal de aviso ao cliente | `[1]` E-mail · `[2]` WhatsApp |
| 4. Desconto | Tipo de desconto | `[1]` Cliente Novo (15%) · `[2]` Cupom Promocional (10%) · `[3]` Nenhum |
| 5. Pagamento | Forma de pagamento | `[1]` Pix · `[2]` Cartão de Crédito · `[3]` Boleto |

Depois disso, o programa processa a assinatura e exibe o resumo:

```text
========================================
          RESUMO DA ASSINATURA
========================================
Processando assinatura e pagamento...

Gerando QR Code e processando Pix no valor de R$ 53,91
Enviando WhatsApp para 11999999999 | Mensagem: Pagamento de R$ 53,91 aprovado via Pix.
Enviando WhatsApp para 11999999999 | Mensagem: Olá Maria! Sua assinatura do plano Premium foi criada com sucesso. Valor: R$ 53,91

[SUCESSO] Sua assinatura foi ativada com sucesso!
Assinatura #1 | Maria | Plano Premium | 01/10/2026 a 01/11/2026 | ATIVA
========================================
```

*(Exemplo: plano Premium, cupom promocional de 10%, Pix e WhatsApp.)*

### Tratamento de erros

* **Dados inválidos** (nome vazio, e-mail sem `@`, CPF ou telefone em branco): o modelo `Cliente` lança `IllegalArgumentException`, e a `Main` mostra `[ERRO]` com a mensagem e volta ao menu.
* **Opção inválida** no menu principal: mostra `[!] Opção inválida`.
* **Opção inválida nos submenus**: cai na opção padrão (Plano Básico, E-mail, Sem desconto, Pix).
* **Pagamento recusado**: a assinatura permanece `PENDENTE` e a `Main` mostra `[ERRO] Falha no pagamento`.

---

##  Arquitetura

```text
src/
├── Main.java
└── submanager/
    ├── model/
    │   ├── Cliente.java
    │   ├── Plano.java
    │   ├── Assinatura.java
    │   ├── Pagamento.java
    │   └── Cupom.java
    │
    ├── interfaces/
    │   ├── MetodoPagamento.java
    │   ├── Notificador.java
    │   └── CalculadorDesconto.java
    │
    ├── implementations/
    │   ├── pagamento/
    │   │   ├── PixPagamento.java
    │   │   ├── CartaoPagamento.java
    │   │   └── BoletoPagamento.java
    │   ├── notificacao/
    │   │   ├── EmailNotificador.java
    │   │   └── WhatsappNotificador.java
    │   └── desconto/
    │       ├── DescontoCupom.java
    │       └── DescontoClienteNovo.java
    │
    └── service/
        ├── AssinaturaService.java
        └── PagamentoService.java
```

### Diagrama de dependências

```text
                         ┌──────────┐
                         │   Main   │  (interação + composição)
                         └────┬─────┘
                              │ cria e injeta
                              ▼
                    ┌───────────────────┐
                    │ AssinaturaService │
                    └─────────┬─────────┘
          ┌───────────────────┼────────────────────┐
          ▼                   ▼                    ▼
  «interface»          «interface»          ┌────────────────┐
  CalculadorDesconto    Notificador         │PagamentoService│
          ▲                   ▲             └───────┬────────┘
          │                   │                     │
  ┌───────┴────────┐   ┌──────┴───────┐     ┌───────┴────────┐
  │ DescontoCupom  │   │ Email        │     ▼                ▼
  │ DescontoCliente│   │ Whatsapp     │  «interface»     «interface»
  │ Novo           │   └──────────────┘  MetodoPagamento  Notificador
  └────────────────┘                          ▲
                                     ┌────────┼────────┐
                                     │        │        │
                                    Pix    Cartão   Boleto
```

Os *services* dependem **somente de interfaces**. As classes concretas só aparecem na `Main`, que monta o conjunto.

---

##  Fluxo de execução

```text
Usuário
   │  informa dados, plano, notificação, desconto e pagamento
   ▼
Main
   │  1. valida o Cliente (via construtor do modelo)
   │  2. escolhe as implementações (Notificador, CalculadorDesconto, MetodoPagamento)
   │  3. cria PagamentoService(metodoPagamento, notificador)
   │  4. cria AssinaturaService(pagamentoService, notificador, calculadorDesconto)
   │  5. chama assinaturaService.criarAssinatura(cliente, plano, cupom)
   ▼
AssinaturaService.criarAssinatura()
   │  a. cria Assinatura (nasce PENDENTE)
   │  b. calcularDesconto(valor, cupom) → valor final = valor - desconto
   │  c. pagamentoService.processarPagamento(assinatura, valorFinal)
   ▼
PagamentoService.processarPagamento()
   │  a. cria Pagamento (nasce PENDENTE)
   │  b. metodoPagamento.pagar(valor)
   │  c. aprova ou recusa o Pagamento
   │  d. notifica o cliente sobre o resultado do pagamento
   ▼
De volta ao AssinaturaService
   │  • aprovado → assinatura.ativar() (PENDENTE → ATIVA) + notifica o cliente
   │  • recusado → devolve a assinatura ainda PENDENTE
   ▼
Main
      mostra [SUCESSO] ou [ERRO] e o resumo da assinatura
```

### Ciclo de vida da `Assinatura`

```text
            ativar()                    renovar()
 PENDENTE ───────────▶ ATIVA ◀───────────────────── EXPIRADA
                         │                              ▲
                         │  verificarExpiracao()        │
                         └──────────────────────────────┘
                         │
                         │  cancelar()
                         ▼
                    CANCELADA   (não pode ser renovada)
```

### Ciclo de vida do `Pagamento`

```text
 PENDENTE ──aprovar()──▶ APROVADO
    │
    └────recusar()─────▶ RECUSADO
```

Depois de finalizado (aprovado ou recusado), o pagamento não muda mais de estado.

---

##  Camadas e responsabilidades

### `model` — domínio

Entidades que guardam dados e protegem as próprias regras (validações no construtor, transições de estado).

| Classe | Papel |
| ------ | ----- |
| `Cliente` | Dados do cliente: `id`, `nome`, `email`, `cpf`, `telefone`. Valida os campos obrigatórios. |
| `Plano` | Plano contratável: `id`, `nome`, `descricao`, `valor`, `periodo` (em meses). |
| `Assinatura` | Contratação de um plano. Controla o próprio ciclo de vida: `ativar()`, `renovar()`, `cancelar()`, `verificarExpiracao()`. |
| `Pagamento` | Registro de uma cobrança: `aprovar()` e `recusar()`. Não processa pagamento, só guarda o resultado. |
| `Cupom` | Dados de um cupom: `codigo`, `tipo`, `percentual`, `validade`. Informa se está válido. |

### `interfaces` — contratos

| Interface | Contrato |
| --------- | -------- |
| `MetodoPagamento` | `boolean pagar(double valor)` e `String getDescricao()` |
| `Notificador` | `void enviar(String mensagem, Cliente destinatario)` |
| `CalculadorDesconto` | `double calcularDesconto(double valor, Cupom cupom)`: retorna o **valor do desconto**, nunca o preço final |

### `implementations` — comportamentos concretos

| Pacote | Classes | Comportamento |
| ------ | ------- | ------------- |
| `pagamento` | `PixPagamento`, `CartaoPagamento`, `BoletoPagamento` | Simulam a cobrança e sempre aprovam. |
| `notificacao` | `EmailNotificador`, `WhatsappNotificador` | E-mail usa `getEmail()`, WhatsApp usa `getTelefone()`. |
| `desconto` | `DescontoClienteNovo`, `DescontoCupom` | 15% fixo (ignora o cupom) / percentual do cupom (zero se `null` ou vencido). |

### `service` — orquestração

| Service | Responsabilidade |
| ------- | ---------------- |
| `PagamentoService` | Coordena **uma** cobrança: cria o `Pagamento`, chama o `MetodoPagamento`, registra o resultado e notifica. |
| `AssinaturaService` | Coordena o ciclo de vida: `criarAssinatura()`, `renovarAssinatura()`, `cancelarAssinatura()`. |

### `Main` — terminal

Cuida **apenas** de ler e exibir dados e de montar as dependências. Não calcula desconto, não cobra, não ativa assinatura e não envia mensagem.

---

## SOLID no projeto

### S — Single Responsibility Principle

> Cada classe tem um único motivo para mudar.

| Classe | Única responsabilidade |
| ------ | ---------------------- |
| `PixPagamento` | Cobrar via Pix |
| `EmailNotificador` | Enviar notificação por e-mail |
| `DescontoCupom` | Calcular desconto de cupom |
| `Assinatura` | Controlar o estado da assinatura |
| `Pagamento` | Registrar o resultado de uma cobrança |
| `PagamentoService` | Coordenar o processo de pagamento |
| `AssinaturaService` | Coordenar o ciclo de vida da assinatura |
| `Main` | Interagir com o usuário |

O `AssinaturaService` **não** calcula desconto, não cobra e não envia mensagem: ele delega cada uma dessas tarefas. A `Main` também não: ela só delega ao service.

### O — Open/Closed Principle

> Aberto para extensão, fechado para modificação.

Para adicionar um novo meio de pagamento, basta criar uma nova classe:

```java
public class CriptoPagamento implements MetodoPagamento {

    @Override
    public boolean pagar(double valor) {
        // ...
        return true;
    }

    @Override
    public String getDescricao() {
        return "Criptomoeda";
    }
}
```

`PagamentoService` e `AssinaturaService` **não mudam**. O mesmo vale para novos notificadores (SMS, push) e novos descontos (fidelidade, aniversário). A única alteração fica no ponto de composição (`Main`), que passa a oferecer a nova opção no menu.

### L — Liskov Substitution Principle

> Qualquer implementação pode substituir sua interface sem quebrar o código que a usa.

```java
MetodoPagamento metodo = new PixPagamento();     // ou
MetodoPagamento metodo = new CartaoPagamento();  // ou
MetodoPagamento metodo = new BoletoPagamento();
```

O `PagamentoService` chama `pagar()` e `getDescricao()` sem saber qual implementação recebeu, e todas respeitam o contrato (retornam `true` se aprovado). O mesmo vale para `Notificador` e `CalculadorDesconto`:

* A interface `Notificador` recebe o **`Cliente`** (e não uma `String`), porque cada canal usa um contato diferente. Assim nenhuma implementação precisa de tratamento especial.
* `CalculadorDesconto` define que o retorno é sempre o *valor do desconto*. `DescontoClienteNovo` ignora o cupom e `DescontoCupom` aceita `null`, e ambas respeitam o contrato sem surpresas.

### I — Interface Segregation Principle

> Interfaces pequenas e específicas.

Em vez de uma interface gigante com `pagar()`, `enviarEmail()` e `calcularDesconto()`, existem três contratos mínimos:

```text
MetodoPagamento     →  pagar(), getDescricao()
Notificador         →  enviar()
CalculadorDesconto  →  calcularDesconto()
```

Nenhuma classe é obrigada a implementar métodos que não usa. Como `CalculadorDesconto` tem um único método, ele também aceita **lambda** (usado na `Main` para o caso "sem desconto").

### D — Dependency Inversion Principle

> Depender de abstrações, não de implementações.

Os services recebem as dependências pelo construtor, sempre como interfaces:

```java
public PagamentoService(MetodoPagamento metodoPagamento, Notificador notificador) { ... }

public AssinaturaService(PagamentoService pagamentoService,
                         Notificador notificador,
                         CalculadorDesconto calculadoraDesconto) { ... }
```

As implementações concretas são escolhidas e injetadas pela `Main` (a "raiz de composição"):

```java
PagamentoService pagamentoService = new PagamentoService(metodoPagamento, notificador);
AssinaturaService assinaturaService = new AssinaturaService(pagamentoService, notificador, calculadorDesconto);
```

Nenhum service importa `PixPagamento`, `EmailNotificador` ou qualquer outra classe concreta.

### Resumo

| Princípio | Onde aparece |
| --------- | ------------ |
| **S** | Classes pequenas e focadas; services só coordenam; `Main` só faz interface com o usuário |
| **O** | Novo pagamento, canal ou desconto = nova classe, sem alterar os services |
| **L** | Pix, Cartão e Boleto (e os demais pares) são intercambiáveis |
| **I** | Três interfaces mínimas, uma por responsabilidade |
| **D** | Injeção por construtor de interfaces nos services |

### O que o projeto evita de propósito

* Cadeias de `if (tipoPagamento.equals("PIX")) ... else if ...` dentro dos services para decidir o comportamento.
* Uso de `instanceof` para controlar regras de negócio.
* Regras de negócio dentro da `Main`.

A escolha do comportamento é feita por **polimorfismo**: o código chama a interface e a implementação correta executa.

---

## ➕ Como estender o sistema

| Quero adicionar... | O que criar | O que alterar |
| ------------------ | ----------- | ------------- |
| Novo meio de pagamento | Classe que implementa `MetodoPagamento` | Opção no menu e no `selecionarMetodoPagamento` da `Main` |
| Novo canal de notificação | Classe que implementa `Notificador` | Opção no menu e no `selecionarNotificador` da `Main` |
| Novo tipo de desconto | Classe que implementa `CalculadorDesconto` | Opção no menu e no `selecionarDesconto` da `Main` |
| Novo plano | Nada (usa o modelo `Plano`) | Opção no menu e no `selecionarPlano` da `Main` |

Os services (`PagamentoService` e `AssinaturaService`) permanecem intactos em todos os casos.

---

##  Decisões de projeto e limitações

* **Services montados a cada fluxo.** O `PagamentoService` recebe um único `MetodoPagamento` no construtor, então a `Main` cria os services depois que o usuário escolhe pagamento, notificação e desconto.
* **Pagamentos simulados.** Pix, cartão e boleto apenas imprimem uma mensagem e retornam `true`, então hoje o fluxo de recusa só pode ser exercitado com uma nova implementação de teste.
* **Dois avisos por fluxo.** O `PagamentoService` notifica o resultado da cobrança e o `AssinaturaService` notifica a criação da assinatura, por isso o cliente recebe duas mensagens.
* **Cupom é percentual.** O modelo `Cupom` guarda um percentual (10.0 = 10%), e não um valor em reais. O cupom promocional do menu (`PROMO10`, 10%, válido por 30 dias) é criado na `Main`.
* **Dados em memória.** IDs são gerados por contadores simples e nada é persistido entre execuções.
* **Operações ainda não expostas no menu.** `renovarAssinatura()` e `cancelarAssinatura()` já existem no `AssinaturaService`, mas o menu atual cobre apenas a simulação de nova assinatura.

---

##  Equipe

| Integrante | Responsabilidade |
| ---------- | ---------------- |
| Matheus Bueno | Modelagem do domínio (`Cliente`, `Plano`, `Assinatura`) |
| Gabriel Canoff| Pagamentos e notificações (`MetodoPagamento`, `Notificador`, implementações e `PagamentoService`) |
| Pedro Basilio | Descontos e assinaturas (`Cupom`, `Pagamento`, `CalculadorDesconto`, `AssinaturaService`) |
| Lucas Perusselli | Terminal e integração (`Main`) |