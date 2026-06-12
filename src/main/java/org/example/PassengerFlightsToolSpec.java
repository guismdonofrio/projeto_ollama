package org.example;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

public class PassengerFlightsToolSpec {

    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("listar-voos-por-passageiro")
                        .description("Mostra todos os voos que uma pessoa comprou, com datas, preços e assentos. " +
                                "Use quando perguntarem: 'voos que João Silva comprou', 'viagens de Ana Costa'. " +
                                "Parâmetro obrigatório: nome_pessoa (nome completo).")
                        .parameters(Tools.Parameters.of(Map.of(
                                "nome_pessoa", Tools.Property.builder()
                                        .type("string")
                                        .description("Nome completo do passageiro (ex: 'João Silva', 'Maria Souza')")
                                        .required(true)
                                        .build())))
                        .build())
                .toolFunction(new PassengerFlightsToolFunction())
                .build();
    }
}