package org.example;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

public class AircraftCapacityToolSpec {

    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("consultar-capacidade-voo")
                        .description("Retorna capacidade total da aeronave e assentos ocupados em um voo. " +
                                "Use quando perguntarem: 'capacidade do voo LA3200', 'quantos assentos ocupados no voo G31422'.")
                        .parameters(Tools.Parameters.of(Map.of(
                                "codigo_voo", Tools.Property.builder()
                                        .type("string")
                                        .description("Código do voo (ex: LA3200)")
                                        .required(true)
                                        .build())))
                        .build())
                .toolFunction(new AircraftCapacityToolFunction())
                .build();
    }
}