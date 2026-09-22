# SubManager

Sistema de gerenciamento de assinaturas desenvolvido em **Java**, com foco na aplicação prática dos princípios **SOLID**.

O projeto simula uma plataforma onde clientes podem contratar planos, realizar pagamentos, utilizar descontos, receber notificações e gerenciar suas assinaturas.

A aplicação inicialmente será executada pelo **terminal da IDE IntelliJ IDEA**, utilizando `Scanner` para interação com o usuário.

---

## 🎯 Objetivo do projeto

O principal objetivo do SubManager é demonstrar, na prática, a aplicação dos princípios:

* **S — Single Responsibility Principle**
* **O — Open/Closed Principle**
* **L — Liskov Substitution Principle**
* **I — Interface Segregation Principle**
* **D — Dependency Inversion Principle**

A aplicação será dividida em entidades, interfaces, implementações e serviços, mantendo as responsabilidades separadas.

Um dos principais objetivos da arquitetura é permitir adicionar novos comportamentos sem precisar modificar os serviços principais.

---

# 🏗️ Arquitetura

```text
src/
└── br.com.submanager/
    │
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
    │   └── CalculadoraDesconto.java
    │
    ├── implementations/
    │   ├── pagamento/
    │   │   ├── PixPagamento.java
    │   │   ├── CartaoPagamento.java
    │   │   └── BoletoPagamento.java
    │   │
    │   ├── notificacao/
    │   │   ├── EmailNotificador.java
    │   │   └── WhatsAppNotificador.java
    │   │
    │   └── desconto/
    │       ├── DescontoCupom.java
    │       └── DescontoClienteNovo.java
    │
    ├── service/
    │   ├── AssinaturaService.java
    │   └── PagamentoService.java
    │
    └── Main.java
```

---

# 🧩 Funcionamento do sistema

O fluxo principal do sistema será:

```text
Cliente
   │
   ▼
Escolhe um Plano
   │
   ▼
Calcula desconto
   │
   ▼
Calcula valor final
   │
   ▼
PagamentoService
   │
   ▼
MetodoPagamento
   │
   ├── Pix
   ├── Cartão
   └── Boleto
   │
   ▼
Pagamento aprovado
   │
   ▼
Cria Assinatura
   │
   ▼
Notificador
   │
   ├── E-mail
   └── WhatsApp
```

O `Main.java` será responsável pela interação com o usuário.

As regras de negócio deverão ficar nos **Services** e nas classes responsáveis por cada comportamento.

---

# 📦 Entidades

## Cliente

Representa o usuário que possui uma assinatura.

Principais atributos:

```text
id
nome
email
cpf
telefone
```

---

## Plano

Representa um plano disponível para contratação.

Principais atributos:

```text
id
nome
descricao
valor
periodo
```

Exemplos:

* Básico
* Premium
* Empresarial

---

## Assinatura

Representa a contratação de um plano por um cliente.

Principais atributos:

```text
id
cliente
plano
dataInicio
dataFinal
status
```

Possíveis status:

```text
ATIVA
PENDENTE
CANCELADA
EXPIRADA
```

---

## Pagamento

Representa um pagamento relacionado a uma assinatura.

Principais atributos:

```text
id
assinatura
metodo
valor
status
dataPagamento
```

---

## Cupom

Representa um desconto que pode ser aplicado à assinatura.

Exemplo:

```text
Código: PRIMEIRA10
Percentual: 10%
```

---

# 🔌 Interfaces

## MetodoPagamento

Define o comportamento que qualquer método de pagamento deve possuir.

```java
public interface MetodoPagamento {

    boolean pagar(double valor);

    String getDescricao();
}
```

Implementações:

```text
PixPagamento
CartaoPagamento
BoletoPagamento
```

---

## Notificador

Define o comportamento responsável pelo envio de notificações.

```java
public interface Notificador {

    void enviar(String mensagem, String destinatario);
}
```

Implementações:

```text
EmailNotificador
WhatsAppNotificador
```

---

## CalculadoraDesconto

Define o comportamento responsável pelo cálculo de descontos.

```java
public interface CalculadoraDesconto {

    double calcularDesconto(double valor, Cupom cupom);
}
```

Implementações:

```text
DescontoCupom
DescontoClienteNovo
```

---

# ⚙️ Services

## PagamentoService

Responsável por coordenar o processamento de pagamentos.

Recebe suas dependências através do construtor:

```java
public PagamentoService(
        MetodoPagamento metodoPagamento,
        Notificador notificador
) {
    this.metodoPagamento = metodoPagamento;
    this.notificador = notificador;
}
```

O serviço deverá:

1. Receber o valor;
2. Utilizar o método de pagamento;
3. Verificar se o pagamento foi aprovado;
4. Enviar uma notificação.

Fluxo:

```text
PagamentoService
       │
       ├── MetodoPagamento
       │       └── PixPagamento
       │
       └── Notificador
               └── WhatsAppNotificador
```

---

## AssinaturaService

Responsável por coordenar o ciclo de vida das assinaturas.

Principais operações:

```text
criarAssinatura()
renovarAssinatura()
cancelarAssinatura()
```

O serviço deverá coordenar:

```text
Cliente
Plano
CalculadoraDesconto
PagamentoService
Notificador
```

O `AssinaturaService` não deverá implementar diretamente regras específicas de Pix, cartão, boleto, e-mail etc.

---

# 🧠 Aplicação dos princípios SOLID

## S — Single Responsibility

Cada classe deve possuir uma responsabilidade específica.

Exemplo:

```text
PixPagamento
    → responsável pelo pagamento via Pix

EmailNotificador
    → responsável pelo envio de notificações por e-mail

PagamentoService
    → responsável por coordenar o processo de pagamento
```

---

## O — Open/Closed

O sistema deve estar aberto para extensão e fechado para modificação.

Por exemplo, para adicionar um novo método:

```text
CriptoPagamento
```

não devemos precisar alterar o `PagamentoService`.

Basta criar:

```java
public class CriptoPagamento implements MetodoPagamento {

    // implementação
}
```

Dessa forma, novos comportamentos podem ser adicionados utilizando as abstrações existentes.

---

## L — Liskov Substitution

As implementações de `MetodoPagamento` devem poder substituir umas às outras.

Por exemplo:

```java
MetodoPagamento pagamento;
```

pode receber:

```java
new PixPagamento();
```

ou:

```java
new CartaoPagamento();
```

ou:

```java
new BoletoPagamento();
```

O código que utiliza a interface não precisa conhecer a implementação específica.

---

## I — Interface Segregation

As interfaces devem ser pequenas e específicas.

Em vez de criar uma interface gigante contendo:

```text
pagar()
enviarEmail()
calcularDesconto()
cancelarAssinatura()
```

cada responsabilidade possui sua própria abstração:

```text
MetodoPagamento
Notificador
CalculadoraDesconto
```

---

## D — Dependency Inversion

Os serviços devem depender de abstrações e não diretamente das implementações.

Exemplo:

```java
private MetodoPagamento metodoPagamento;
```

em vez de:

```java
private PixPagamento pixPagamento;
```

As dependências serão recebidas através do construtor.

Isso permite trocar facilmente uma implementação por outra.

---

# 👥 Divisão do projeto entre 4 integrantes

A divisão foi feita para que cada integrante tenha arquivos próprios, mas que as partes tenham dependências claras entre si.

---

# 👤 Integrante 1 — Modelagem do domínio

## Responsabilidade

Criar as entidades principais do sistema.

## Arquivos

```text
model/
├── Cliente.java
├── Plano.java
└── Assinatura.java
```

## Cliente.java

Funções principais:

```java
getId()
getNome()
getEmail()
getCpf()
getTelefone()
```

## Plano.java

Funções principais:

```java
getId()
getNome()
getDescricao()
getValor()
getPeriodo()
```

## Assinatura.java

Funções principais:

```java
getId()
getCliente()
getPlano()
getDataInicio()
getDataFinal()
getStatus()

ativar()
cancelar()
renovar()
```

## Conexão

O trabalho do Integrante 1 será utilizado principalmente por:

```text
Integrante 2 → Pagamentos
Integrante 3 → Assinaturas e descontos
Integrante 4 → Terminal/Main
```

Fluxo:

```text
Cliente ──────┐
              ├──> AssinaturaService
Plano ────────┘
```

---

# 👤 Integrante 2 — Pagamentos e notificações

## Responsabilidade

Implementar toda a parte relacionada ao processamento de pagamentos e comunicação com o cliente.

## Arquivos

```text
interfaces/
└── MetodoPagamento.java

implementations/pagamento/
├── PixPagamento.java
├── CartaoPagamento.java
└── BoletoPagamento.java

interfaces/
└── Notificador.java

implementations/notificacao/
├── EmailNotificador.java
└── WhatsAppNotificador.java

service/
└── PagamentoService.java
```

## Funções

### MetodoPagamento

```java
pagar()
getDescricao()
```

### PixPagamento

```java
pagar()
getDescricao()
```

### CartaoPagamento

```java
pagar()
getDescricao()
```

### BoletoPagamento

```java
pagar()
getDescricao()
```

### Notificador

```java
enviar()
```

### EmailNotificador

```java
enviar()
```

### WhatsAppNotificador

```java
enviar()
```

### PagamentoService

```java
processarPagamento()
```

## Conexão

O `PagamentoService` será utilizado pelo `AssinaturaService`.

```text
AssinaturaService
       │
       ▼
PagamentoService
       │
       ├── MetodoPagamento
       │       ├── Pix
       │       ├── Cartão
       │       └── Boleto
       │
       └── Notificador
               ├── Email
               └── WhatsApp
```

Essa área demonstra principalmente:

```text
OCP
LSP
DIP
```

---

# 👤 Integrante 3 — Descontos e gerenciamento de assinaturas

## Responsabilidade

Implementar as regras relacionadas a descontos e coordenar o ciclo de vida das assinaturas.

## Arquivos

```text
interfaces/
└── CalculadoraDesconto.java

implementations/desconto/
├── DescontoCupom.java
└── DescontoClienteNovo.java

service/
└── AssinaturaService.java

model/
├── Pagamento.java
└── Cupom.java
```

## Funções

### Cupom.java

```java
getCodigo()
getTipo()
getPercentual()
getValidade()
```

### Pagamento.java

```java
getId()
getAssinatura()
getMetodo()
getValor()
getStatus()
getDataPagamento()
```

### CalculadoraDesconto

```java
calcularDesconto()
```

### DescontoCupom

```java
calcularDesconto()
```

### DescontoClienteNovo

```java
calcularDesconto()
```

### AssinaturaService

```java
criarAssinatura()
renovarAssinatura()
cancelarAssinatura()
```

## Conexão

O `AssinaturaService` será o principal ponto de integração do projeto.

```text
Cliente
   │
   ▼
AssinaturaService
   │
   ├── Plano
   │
   ├── CalculadoraDesconto
   │
   └── PagamentoService
```

Exemplo:

```text
Cliente escolhe plano
        ↓
AssinaturaService
        ↓
CalculadoraDesconto
        ↓
valor final
        ↓
PagamentoService
        ↓
pagamento aprovado
        ↓
Assinatura criada
```

Essa área demonstra principalmente:

```text
SRP
OCP
DIP
```

---

# 👤 Integrante 4 — Terminal e integração

## Responsabilidade

Criar a interface de terminal e integrar todas as partes desenvolvidas pelos outros integrantes.

## Arquivo

```text
Main.java
```

## Funções principais

```java
main()
exibirMenu()
criarAssinatura()
listarAssinaturas()
renovarAssinatura()
cancelarAssinatura()
lerOpcao()
```

O `Main` deverá cuidar da interação com o usuário.

Ele **não deve concentrar as regras de negócio**.

O ideal é:

```java
assinaturaService.criarAssinatura(...);
```

e não:

```java
// Main calculando desconto
// Main processando Pix
// Main criando assinatura
// Main enviando e-mail
```

## Conexão

O Integrante 4 conecta todos os outros:

```text
                    ┌──> Cliente
                    │
                    ├──> Plano
                    │
Main ──> AssinaturaService
                    │
                    ├──> CalculadoraDesconto
                    │
                    └──> PagamentoService
                              │
                              ├──> MetodoPagamento
                              └──> Notificador
```

---

# 🔗 Dependências entre os integrantes

A integração pode seguir esta ordem:

```text
INTEGRANTE 1
Modelos
   │
   ├──────────────┐
   ▼              ▼
INTEGRANTE 2   INTEGRANTE 3
Pagamento      Assinaturas
   │              │
   └──────┬───────┘
          ▼
     INTEGRANTE 4
         Main
```

Mais especificamente:

```text
Integrante 1
    │
    │ fornece Cliente, Plano e Assinatura
    ▼
Integrante 3
    │
    │ utiliza PagamentoService
    ▼
Integrante 2
    │
    │ utiliza interfaces de pagamento
    ▼
Implementações
```

E finalmente:

```text
Integrante 4
     │
     ▼
AssinaturaService
     │
     ├── Cliente
     ├── Plano
     ├── CalculadoraDesconto
     └── PagamentoService
              │
              ├── MetodoPagamento
              └── Notificador
```

---

# 🧪 Cenários que deverão ser demonstrados

O `Main.java` deverá permitir testar pelo menos dois fluxos diferentes.

## Cenário 1 — Assinatura com Pix

```text
Cliente: João
Plano: Premium
Pagamento: Pix
Notificação: WhatsApp
Desconto: nenhum
```

Fluxo:

```text
João
 ↓
Premium
 ↓
Sem desconto
 ↓
Pix
 ↓
Pagamento aprovado
 ↓
Assinatura criada
 ↓
WhatsApp
```

---

## Cenário 2 — Assinatura com desconto

```text
Cliente: Maria
Plano: Básico
Pagamento: Cartão
Notificação: E-mail
Desconto: PRIMEIRA10
```

Fluxo:

```text
Maria
 ↓
Básico
 ↓
Cupom PRIMEIRA10
 ↓
Valor com desconto
 ↓
Cartão
 ↓
Pagamento aprovado
 ↓
Assinatura criada
 ↓
E-mail
```

---

# 🚫 O que evitar

O projeto não deverá utilizar estruturas como:

```java
if (tipoPagamento.equals("PIX")) {
    // ...
} else if (tipoPagamento.equals("CARTAO")) {
    // ...
}
```

para decidir qual comportamento executar.

Também deve ser evitado:

```java
instanceof
```

para controlar regras de negócio.

A ideia é utilizar **polimorfismo e interfaces**.

Em vez de:

```java
if (tipo == PIX) {
    // Pix
}
```

utilizar:

```java
MetodoPagamento metodoPagamento;
```

e permitir que a implementação execute o comportamento correspondente.

---

# 📌 Regras para integração da equipe

1. Cada integrante deve trabalhar prioritariamente nos arquivos de sua responsabilidade.
2. Interfaces devem ser definidas antes das implementações que dependem delas.
3. Não alterar a assinatura de métodos públicos de outro integrante sem avisar a equipe.
4. O `Main.java` deve utilizar os Services, e não implementar regras de negócio.
5. Os Services devem depender de interfaces.
6. Novos comportamentos devem preferencialmente ser adicionados criando novas implementações.
7. Todos devem testar suas classes antes de integrar com o restante do projeto.

---

# 🌿 Sugestão de branches

Cada integrante pode trabalhar em uma branch própria:

```text
main
│
├── feature/modelos
├── feature/pagamentos
├── feature/assinaturas
└── feature/main-integracao
```

Depois, as branches podem ser integradas na `main`.

---

# 📝 Resumo da divisão

| Integrante | Área                                     | Arquivos principais                                                         |
| ---------- | ---------------------------------------- | --------------------------------------------------------------------------- |
| 1          | Modelagem                                | `Cliente`, `Plano`, `Assinatura`                                            |
| 2          | Pagamentos e notificações                | `MetodoPagamento`, `PagamentoService`, Pix, Cartão, Boleto, Email, WhatsApp |
| 3          | Descontos e gerenciamento de assinaturas | `Cupom`, `Pagamento`, `CalculadoraDesconto`, `AssinaturaService`            |
| 4          | Terminal e integração                    | `Main.java`                                                                 |

A divisão foi pensada para que **ninguém trabalhe isoladamente**: o Integrante 1 fornece o domínio, o Integrante 2 fornece o processamento de pagamentos e notificações, o Integrante 3 coordena a assinatura e os descontos, e o Integrante 4 integra tudo através do terminal.

---

# 🚀 Possíveis extensões futuras

A arquitetura permite adicionar novos comportamentos sem alterar o núcleo do sistema.

Por exemplo:

```text
Novos pagamentos:
    CriptoPagamento
    ApplePayPagamento

Novos notificadores:
    SMSNotificador
    PushNotificador

Novos descontos:
    DescontoFidelidade
    DescontoAniversario

Novos planos:
    Empresarial
    Familiar
    Estudante
```

A adição dessas funcionalidades deve ocorrer principalmente através de novas implementações das interfaces existentes.

---

# 📚 Tecnologias

* Java
* IntelliJ IDEA
* Programação Orientada a Objetos
* Interfaces
* Polimorfismo
* Princípios SOLID
* `Scanner` para interação via terminal

Inicialmente, os dados podem ser mantidos **em memória**, utilizando estruturas como `List`/`ArrayList`, sem necessidade de banco de dados ou framework externo.

---

# 👨‍💻 Equipe

| Integrante   | Responsabilidade                         |
| ------------ | ---------------------------------------- |
| Integrante 1 | Modelagem do domínio                     |
| Integrante 2 | Pagamentos e notificações                |
| Integrante 3 | Descontos e gerenciamento de assinaturas |
| Integrante 4 | Terminal e integração                    |

---
