package org.example;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

public class FlightDetailsToolSpec {

    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("exibir-detalhes-voo")
                        .description("Mostra detalhes completos de um voo específico: horários, origem, destino, aeronave utilizada e companhia.")
                        .parameters(Tools.Parameters.of(Map.of(
                                "codigo_voo", Tools.Property.builder()
                                        .type("string")
                                        .description("Código do voo (ex: LA3200, G31422, AD4110, 2Z2234)")
                                        .required(true)
                                        .build())))
                        .build())
                .toolFunction(new FlightDetailsToolFunction())
                .build();
    }
}