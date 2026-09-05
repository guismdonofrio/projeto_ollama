package org.example;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

public class OccupancyRateToolSpec {

    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("calcular-taxa-ocupacao")
                        .description("Calcula a taxa média de ocupação (percentual de assentos ocupados) dos voos agrupados por companhia aérea.")
                        .parameters(Tools.Parameters.of(Map.of())) // sem parâmetros
                        .build())
                .toolFunction(new OccupancyRateToolFunction())
                .build();
    }
}