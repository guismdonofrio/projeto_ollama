package org.example;

import io.github.ollama4j.tools.Tools;

public class ListCompaniesSpec {
    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("listar-companhias")
                        .description("Use esta ferramenta quando o usuário quiser saber quais são as companhias aéreas disponíveis.")
                        .build())
                .toolFunction(new ListCompaniesFunction())
                .build();
    }
}