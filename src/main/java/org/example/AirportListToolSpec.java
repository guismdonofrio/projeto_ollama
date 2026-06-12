package org.example;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

public class AirportListToolSpec {

    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("listar-aeroportos")
                        .description("Lista todos os aeroportos cadastrados no sistema, com código IATA, nome, cidade, estado (quando houver) e país.")
                        .parameters(Tools.Parameters.of(Map.of()))
                        .build())
                .toolFunction(new AirportListToolFunction())
                .build();
    }
}