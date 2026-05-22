## Guia de Integração com Inteligência Artificial

Este documento explica como foi feita a integração da Inteligência Artificial no sistema utilizando execução local (*On-Premise*), sem depender de APIs externas.

---

## Arquitetura da IA Local

O projeto utiliza uma abordagem de IA local, onde os dados do sistema permanecem no próprio computador/servidor, aumentando a privacidade e segurança das informações.

## Fluxo da Arquitetura

<p align="center">
  <img src="img/WhatsApp Image 2026-05-22 at 16.26.52.jpeg" height="550">
</p>

---

## Tecnologias Utilizadas na IA

## Ollama

Responsável por executar o modelo de IA localmente utilizando os recursos do computador.

---

## Modelo `qwen3.5:2b`

Modelo utilizado no projeto.

Características:
- leve e rápido;
- suporte a português;
- otimizado para tarefas estruturadas;
- suporte nativo a *Function Calling*.

---

## Configuração no Spring Boot

A configuração principal da IA é feita na classe:

```java
AIConfig
```

Nela são definidos os componentes responsáveis pela comunicação entre:
- Spring Boot;
- LangChain4J;
- Ollama;
- ferramentas Java (*Tools*).

---

## OllamaChatModel

O modelo foi configurado utilizando temperatura baixa:

```java
temperature(0.3)
```

Isso ajuda a:
- diminuir respostas aleatórias;
- evitar alucinações da IA;
- manter respostas mais objetivas.

---

## AiServices.builder

O `AiServices.builder()` conecta:
- o modelo de IA;
- a interface `Assistant`;
- os métodos Java anotados com `@Tool`.

Assim a IA consegue executar funções reais do sistema.

---

## Como Funcionam as Tools

O sistema utiliza o conceito de:

```text
Function Calling / Tools
```

A IA interpreta a pergunta do usuário e decide se precisa consultar o banco de dados.

Se necessário, ela executa automaticamente métodos Java do sistema.

---

## Exemplo de Tool

```java
@Tool("Return fluxo info based on company")
public void getFluxoInfo(String nomeCompanhia) {

    List<Fluxo> fluxos =
        repository.findByCompanhiaNome(nomeCompanhia);

    fluxos.forEach(fluxo ->
        System.out.println(fluxo.getLocal())
    );
}
```

---

## Fluxo de Funcionamento

## Usuário faz uma pergunta

```text
"Onde fica a área de manutenção da Azul?"
```

---

## IA interpreta a intenção

A IA identifica que precisa buscar informações no banco de dados.

Ela extrai:

```text
nomeCompanhia = "Azul"
```

---

## LangChain4J executa a Tool

O framework interrompe a resposta da IA temporariamente e chama:

```java
getFluxoInfo("Azul")
```

---

## Spring Data JPA gera a consulta SQL

```sql
SELECT f.*
FROM fluxo f
JOIN companhia c
ON f.companhia_id = c.id
WHERE c.nome = 'Azul';
```

---

## Resultado retorna para a IA

Os dados encontrados são processados e devolvidos ao usuário em linguagem natural.
