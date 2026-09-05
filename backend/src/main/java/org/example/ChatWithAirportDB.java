package org.example;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.tools.Tools;
import java.util.Scanner;

public class ChatWithAirportDB {
    public static void main(String[] args) throws Exception {

        //CONFIGURAÇÃO DO AGENTE:
        Ollama ollama = new Ollama("http://localhost:11434/");
        String model = "granite4:350m";
        //llama3.2:3b
        //granite4:350m
        ollama.setRequestTimeoutSeconds(300);

        // INSERÇÃO DOS TOOLS PARA USO DA IA:
        ollama.registerTool(FlightFinderToolSpec.getSpecification());
        ollama.registerTool(ListCompaniesSpec.getSpecification());
        ollama.registerTool(AirportListToolSpec.getSpecification());
        ollama.registerTool(FlightFinderToolSpec.getSpecification());
        ollama.registerTool(FlightDetailsToolSpec.getSpecification());
        ollama.registerTool(FlightDestinationToolSpec.getSpecification());
        ollama.registerTool(SeatOccupancyToolSpec.getSpecification());
        ollama.registerTool(PassengersByFlightToolSpec.getSpecification());
        ollama.registerTool(CrewByFlightToolSpec.getSpecification());
        ollama.registerTool(PassengerFlightsToolSpec.getSpecification());
        ollama.registerTool(FlightsFromAirportToolSpec.getSpecification());
        ollama.registerTool(AircraftCapacityToolSpec.getSpecification());
        ollama.registerTool(AveragePriceToolSpec.getSpecification());
        ollama.registerTool(CanceledFlightsToolSpec.getSpecification());
        ollama.registerTool(TopPassengersToolSpec.getSpecification());
        ollama.registerTool(OccupancyRateToolSpec.getSpecification());

        exibirMenu();

        //DIRETRIZES DO AGENTE:
        String systemMessage =
                "Você é um assistente que usa ferramentas para consultar um banco de dados. " +
                        "Quando uma ferramenta retornar um texto, você DEVE exibir EXATAMENTE esse texto, " +
                        "sem adicionar nenhuma palavra, explicação ou formatação extra. " +
                        "Não invente respostas. " +
                        "As ferramentas disponíveis são: 'voos_por_destino' (parâmetro cidade), " +
                        "'passageiros_por_voo' (parâmetro codigo_voo). " +
                        "Exemplo: pergunta 'Listar passageiros do voo LA3200' -> chame passageiros_por_voo com codigo_voo='LA3200'. " +
                        "Após a ferramenta retornar, apenas copie o texto retornado. " +
                        "NUNCA escreva nada além do texto retornado pela ferramenta.";
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("👉 Digite sua pergunta: ");
            String pergunta = scanner.nextLine().trim();

            // Verifica comando de saída
            if (pergunta.equalsIgnoreCase("0") ||
                    pergunta.equalsIgnoreCase("sair") ||
                    pergunta.equalsIgnoreCase("exit") ||
                    pergunta.equalsIgnoreCase("quit")) {
                System.out.println("Encerrando o sistema. Até mais!");
                break;
            }

            if (pergunta.isEmpty()) {
                System.out.println("Por favor, digite uma pergunta válida.\n");
                continue;
            }

            // Prepara a requisição para o Ollama
            OllamaChatRequest requestModel = OllamaChatRequest.builder()
                    .withModel(model)
                    .withMessage(OllamaChatMessageRole.SYSTEM, systemMessage)
                    .withMessage(OllamaChatMessageRole.USER, pergunta)
                    .build();

            try {
                OllamaChatResult chatResult = ollama.chat(requestModel, null);
                String resposta = chatResult.getResponseModel().getMessage().getResponse();
                System.out.println("\n🤖 RESPOSTA:\n" + resposta + "\n");
            } catch (Exception e) {
                System.err.println("❌ Erro ao processar a pergunta: " + e.getMessage());
                System.out.println("Tente novamente com uma pergunta diferente.\n");
            }
        }

        scanner.close();
    }

    private static void exibirMenu(){
        System.out.println("╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    SISTEMA DE CONSULTA AEROPORTUÁRIA COM IA                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("\n✈️  O assistente consegue responder perguntas como:\n");
        System.out.println(" 1. Listar companhias aéreas disponíveis.");
        System.out.println(" 2. Buscar status e detalhes de um voo pelo código (ex: LA3200).");
        System.out.println(" 3. Listar todos os aeroportos cadastrados (código IATA, cidade, estado, país).");
        System.out.println(" 4. Listar voos com destino a uma cidade nos próximos dias (companhia, horário, status).");
        System.out.println(" 5. Mostrar detalhes completos de um voo (horários, origem, destino, aeronave, companhia).");
        System.out.println(" 6. Consultar assentos ocupados/disponíveis em um voo específico.");
        System.out.println(" 7. Listar todos os passageiros de um voo (nome e número do assento).");
        System.out.println(" 8. Mostrar a tripulação de um voo (funcionários e suas funções).");
        System.out.println(" 9. Exibir todos os voos que uma determinada pessoa já comprou (datas, preços).");
        System.out.println("10. Listar voos programados para hoje (ou data específica) com origem em um aeroporto (código IATA).");
        System.out.println("11. Saber a capacidade total da aeronave e quantos assentos estão ocupados em um voo.");
        System.out.println("12. Calcular o preço médio das passagens por companhia e/ou por classe.");
        System.out.println("13. Listar voos cancelados (com filtro opcional por período).");
        System.out.println("14. Mostrar os passageiros que mais viajaram (top N).");
        System.out.println("15. Calcular a taxa média de ocupação (percentual) dos voos por companhia.");
        System.out.println("\n💡 Você pode fazer perguntas em linguagem natural, combinando critérios.");
        System.out.println("🔁 Digite '0' ou 'sair' para encerrar o programa.\n");
    }
}