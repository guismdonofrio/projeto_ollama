package org.example;

import io.github.ollama4j.tools.Tools;
import java.util.Map;

public class AveragePriceToolSpec {

    public static Tools.Tool getSpecification() {
        return Tools.Tool.builder()
                .toolSpec(Tools.ToolSpec.builder()
                        .name("calcular-preco-medio")
                        .description("Calcula preço médio de passagens. Pode filtrar por companhia e/ou classe. " +
                                "Use quando perguntarem: 'preço médio da LATAM', 'média na classe Executiva', 'preço médio por companhia'. " +
                                "Parâmetros opcionais: companhia (nome da cia), classe (Economica/Executiva/Primeira Classe).")
                        .parameters(Tools.Parameters.of(Map.of(
                                "companhia", Tools.Property.builder()
                                        .type("string")
                                        .description("Nome da companhia (ex: 'LATAM Airlines') – opcional")
                                        .required(false)
                                        .build(),
                                "classe", Tools.Property.builder()
                                        .type("string")
                                        .description("Classe (ex: 'Economica', 'Executiva') – opcional")
                                        .required(false)
                                        .build())))
                        .build())
                .toolFunction(new AveragePriceToolFunction())
                .build();
    }
}