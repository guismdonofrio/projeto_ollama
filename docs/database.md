## Documentação do Banco de Dados

Este documento explica como o banco de dados do projeto foi estruturado, além das principais relações entre as tabelas e das regras utilizadas para manter a integridade dos dados.

O script principal do banco está localizado em:

```text
aviao.sql
```

---

## Estrutura do Banco de Dados

O banco foi modelado seguindo conceitos de normalização até a 3FN (Terceira Forma Normal), buscando evitar:
- dados duplicados;
- inconsistências;
- problemas de atualização.

---

## Principais Tabelas

## `pessoa` e `funcionario`

A tabela `pessoa` guarda informações gerais:

- nome;
- documento;
- dados pessoais.

Já a tabela `funcionario` complementa essas informações com dados internos do sistema:

- matrícula;
- data de admissão;
- cargo.

Essa separação ajuda a evitar repetição de dados.

---

## `modelo_aviao` e `aviao`

A tabela `modelo_aviao` armazena características técnicas do modelo da aeronave:

- fabricante;
- capacidade;
- modelo.

A tabela `aviao` guarda informações específicas de cada avião físico:

- matrícula;
- situação;
- identificação individual.

---

## `aviao_companhia`

Tabela responsável pela relação entre:
- aviões;
- companhias aéreas.

Ela permite que diferentes companhias possam utilizar determinadas aeronaves.

---

## `tripulacao_voo`

Tabela utilizada para controlar a tripulação de cada voo.

Ela relaciona:
- voo;
- funcionário;
- função exercida.

Exemplos:
- Piloto;
- Copiloto;
- Comissário de bordo.

---

## `fluxo`

Tabela usada para representar áreas operacionais e logísticas do aeroporto.

Exemplos:
- terminais;
- portões;
- áreas de manutenção;
- canais de embarque.

Cada fluxo fica associado a uma companhia aérea.

---

## Regras de Integridade

O banco utiliza algumas regras importantes para manter os dados organizados.

---

## `ON DELETE CASCADE`

Quando um registro principal é removido, os dados relacionados também são apagados automaticamente.

Exemplo:
- exclusão de uma companhia;
- remoção automática de fluxos e relações associadas.

Isso evita registros órfãos no sistema.

---

## `ON DELETE SET NULL`

Utilizado na relação entre:
- `aviao`
- `modelo_aviao`

Caso um modelo seja removido, o avião continua existindo no banco, mas sem referência ao modelo.

---

## `UNIQUE`

Garantia de valores únicos em campos importantes:

- CPF/documentos;
- matrícula de funcionário;
- matrícula de aeronave;
- código IATA.

---

## Massa de Dados para Testes

O script SQL já possui dados iniciais para facilitar os testes da aplicação.

---

## Aeroportos cadastrados

- Guarulhos (`GRU`)
- Santos Dumont (`SDU`)
- São José dos Campos (`SJK`)

---

## Companhias aéreas

- Azul (`AZU`)
- Gol (`GLO`)
- LATAM (`TAM`)

---

## Dados adicionais

O banco também possui:
- voos cadastrados;
- passageiros fictícios;
- tripulações;
- escalas operacionais.

Tudo isso ajuda nos testes da aplicação e da integração com IA.

---

## Objetivo da Modelagem

A modelagem do banco foi criada buscando:

- organização dos dados;
- facilidade de manutenção;
- integração com IA;
- melhor desempenho nas consultas;
- aplicação de boas práticas de banco de dados.
