package org.example;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

public class FlightsFromAirportToolSpec {

    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("listar-voos-por-origem-data")
                        .description("Lista voos que partem de um aeroporto (código IATA) em uma data específica (ou hoje). " +
                                "Use quando perguntarem: 'voos de SDU hoje', 'programados para GRU em 2026-06-10'. " +
                                "Parâmetros: codigo_iata (obrigatório), data (opcional, formato YYYY-MM-DD).")
                        .parameters(Tools.Parameters.of(Map.of(
                                "codigo_iata", Tools.Property.builder()
                                        .type("string")
                                        .description("Código IATA do aeroporto de origem (ex: SDU, GRU)")
                                        .required(true)
                                        .build(),
                                "data", Tools.Property.builder()
                                        .type("string")
                                        .description("Data no formato YYYY-MM-DD (opcional, padrão hoje)")
                                        .required(false)
                                        .build())))
                        .build())
                .toolFunction(new FlightsFromAirportToolFunction())
                .build();
    }
}