package org.example;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

public class FlightFinderToolSpec {
    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("buscar-detalhes-voo")
                        .description("Busca status e detalhes de um voo pelo seu código.")
                        .parameters(Tools.Parameters.of(Map.of(
                                "codigo_voo", Tools.Property.builder()
                                        .type("string")
                                        .description("O código do voo (ex: LA3200)")
                                        .required(true)
                                        .build())))
                        .build())
                .toolFunction(new FlightFinderToolFunction())
                .build();
    }

}