package org.example;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

public class TopPassengersToolSpec {

    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("listar-passageiros-frequentes")
                        .description("Retorna os passageiros que mais compraram passagens (top N, padrão 5). " +
                                "Use quando perguntarem: 'passageiros que mais viajaram', 'top viajantes', 'quem mais comprou passagens'. " +
                                "Parâmetro opcional: limite (número inteiro).")
                        .parameters(Tools.Parameters.of(Map.of(
                                "limite", Tools.Property.builder()
                                        .type("integer")
                                        .description("Número máximo de passageiros (padrão 5)")
                                        .required(false)
                                        .build())))
                        .build())
                .toolFunction(new TopPassengersToolFunction())
                .build();
    }
}