package org.example;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.tools.Tools;
import java.util.Scanner;

public class ChatWithAirportDB {
    public static void main(String[] args) throws Exception {

        Ollama ollama = new Ollama("http://localhost:11434/");

        String model = "granite4:350m";

        ollama.setRequestTimeoutSeconds(300);
        OllamaChatRequest builder = OllamaChatRequest.builder().withModel(model);

        // Registra a ferramenta de busca de voo
        final Tools.Tool flightFinderTool = FlightFinderToolSpec.getSpecification();
        ollama.registerTool(flightFinderTool);

        ollama.registerTool(FlightFinderToolSpec.getSpecification());
        ollama.registerTool(ListCompaniesSpec.getSpecification());

        System.out.println("Enviando primeira pergunta...");

        Scanner scanner = new Scanner(System.in);

        // Pergunta 1: Requer o uso da ferramenta para consultar o banco
        OllamaChatRequest requestModel =
                builder.withMessage(
                                OllamaChatMessageRole.USER,
                                scanner.next())
                        .build();

        OllamaChatResult chatResult = ollama.chat(requestModel, null);
        System.out.println("Resposta 1:\n" + chatResult.getResponseModel().getMessage().getResponse() + "\n");

    }
}