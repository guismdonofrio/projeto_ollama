# Documentação do Banco de Dados

Esta seção detalha a modelagem física, integridade referencial e massa de testes contidas no script estrutural `aviao.sql`.

## Arquitetura das Tabelas e Normalização

O banco de dados foi projetado para mitigar anomalias de inserção e atualização, aplicando regras estritas de Normalização (até a 3FN):

1. **`pessoa` & `funcionario` (Especialização 1:1):** Centraliza dados civis em `pessoa`, enquanto `funcionario` estende essas características adicionando regras corporativas de admissão e identificação por matrícula única.
2. **`modelo_aviao` & `aviao` (Redundância Isolada):** Especificações de engenharia (como fabricante e capacidade máxima) são mantidas em `modelo_aviao`. A tabela `aviao` armazena apenas dados voláteis e específicos de cada fuselagem física (como o prefixo da matrícula aeronáutica).
3. **`aviao_companhia` (Relação NxM):** Tabela associativa que viabiliza o mapeamento lógico e operacional de frotas compartilhadas ou alocadas dinamicamente entre diferentes companhias aéreas.
4. **`tripulacao_voo` (Escala Operacional):** Utiliza uma chave composta combinando o identificador do voo e o funcionário escalado, guardando o metadado `funcao` (Piloto, Copiloto, Comissário de Bordo) específico daquela missão.
5. **`fluxo` (Monitoramento Logístico):** Relação controlada por chave estrangeira associando canais logísticos, terminais e portões diretamente ao controle de uma companhia.

---

## Regras de Restrição e Integridade

* **`ON DELETE CASCADE`**: Aplicado em amarrações críticas como as tabelas associativas e a entidade `fluxo`. Caso uma companhia ou voo seja excluído, todas as dependências lógicas de frota, tripulação alocada e fluxos são removidos automaticamente, evitando registros órfãos.
* **`ON DELETE SET NULL`**: Na tabela `aviao`, caso um `modelo_aviao` técnico venha a ser removido, o registro da aeronave física permanece intacto, anulando temporariamente a sua referência técnica.
* **`UNIQUE Constraints`**: Garantia absoluta de unicidade em campos cruciais como documentos de identificação (`documento`), matrículas corporativas (`matricula`), prefixos aeronáuticos (`matricula`) e códigos internacionais padrão IATA de aeroportos e companhias aéreas.

---

## Massa de Dados para Testes

O arquivo SQL aplica automaticamente dados iniciais que simulam o ecossistema nacional:
* **Aeroportos Cadastrados:** Guarulhos (`GRU`), Santos Dumont (`SDU`) e São José dos Campos (`SJK`).
* **Companhias Aéreas:** Azul (`AZU`), Gol (`GLO`) e LATAM (`TAM`).
* **Voos e Escalas:** Contém rotas pré-configuradas com decolagens programadas para o ano corrente, com passageiros fictícios e tripulações vinculadas aos seus respectivos cargos técnicos.
