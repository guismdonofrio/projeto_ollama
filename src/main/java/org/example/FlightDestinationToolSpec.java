package org.example;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

public class FlightDestinationToolSpec {

    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("voos_por_destino")  // nome curto, sem verbos
                        .description("Retorna lista de voos que chegam a uma cidade. Parâmetro: cidade (string)")
                        .parameters(Tools.Parameters.of(Map.of(
                                "cidade", Tools.Property.builder()
                                        .type("string")
                                        .description("Nome da cidade de destino")
                                        .required(true)
                                        .build())))
                        .build())
                .toolFunction(new FlightDestinationToolFunction())
                .build();
    }
}