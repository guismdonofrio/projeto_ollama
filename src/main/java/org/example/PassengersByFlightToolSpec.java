package org.example;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

public class PassengersByFlightToolSpec {

    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("passageiros_por_voo")
                        .description("Retorna a lista de passageiros (nomes e assentos) de um voo específico. Parâmetro: codigo_voo (string)")
                        .parameters(Tools.Parameters.of(Map.of(
                                "codigo_voo", Tools.Property.builder()
                                        .type("string")
                                        .description("Código do voo, ex: LA3200, G31422")
                                        .required(true)
                                        .build())))
                        .build())
                .toolFunction(new PassengersByFlightToolFunction())
                .build();
    }
}