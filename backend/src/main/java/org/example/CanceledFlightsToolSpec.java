package org.example;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

public class CanceledFlightsToolSpec {

    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("listar-voos-cancelados")
                        .description("Lista voos com status Cancelado. Filtro opcional por período (data_inicio, data_fim). " +
                                "Use quando perguntarem: 'voos cancelados', 'cancelados entre 2026-06-01 e 2026-06-15'.")
                        .parameters(Tools.Parameters.of(Map.of(
                                "data_inicio", Tools.Property.builder()
                                        .type("string")
                                        .description("Data inicial (YYYY-MM-DD) – opcional")
                                        .required(false)
                                        .build(),
                                "data_fim", Tools.Property.builder()
                                        .type("string")
                                        .description("Data final (YYYY-MM-DD) – opcional")
                                        .required(false)
                                        .build())))
                        .build())
                .toolFunction(new CanceledFlightsToolFunction())
                .build();
    }
}