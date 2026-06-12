package org.example;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

public class SeatOccupancyToolSpec {

    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("consultar-ocupacao-assentos")
                        .description("Retorna a lista de assentos já ocupados e a quantidade de assentos disponíveis em um voo específico.")
                        .parameters(Tools.Parameters.of(Map.of(
                                "codigo_voo", Tools.Property.builder()
                                        .type("string")
                                        .description("Código do voo (ex: LA3200, G31422)")
                                        .required(true)
                                        .build())))
                        .build())
                .toolFunction(new SeatOccupancyToolFunction())
                .build();
    }
}