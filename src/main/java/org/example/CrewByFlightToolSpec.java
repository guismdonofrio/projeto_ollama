package org.example;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

public class CrewByFlightToolSpec {

    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("listar-tripulacao-voo")
                        .description("Lista todos os funcionários escalados na tripulação de um voo específico, com suas respectivas funções (Piloto, Copiloto, Comissário de Bordo, etc.).")
                        .parameters(Tools.Parameters.of(Map.of(
                                "codigo_voo", Tools.Property.builder()
                                        .type("string")
                                        .description("Código do voo (ex: LA3200, G31422)")
                                        .required(true)
                                        .build())))
                        .build())
                .toolFunction(new CrewByFlightToolFunction())
                .build();
    }
}