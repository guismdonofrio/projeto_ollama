package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.util.Map;

public class PassengerFlightsToolFunction implements ToolFunction {

    @Override
    public Object apply(Map<String, Object> arguments) {
        // LOG DE DEPURAÇÃO
        System.out.println(">>> 🔧 PassengersByFlightToolFunction FOI CHAMADA! Argumentos: " + arguments);


        Object nomeObj = arguments.get("nome_pessoa");
        if (nomeObj == null) {
            return "❌ Parâmetro 'nome_pessoa' é obrigatório. Exemplo: 'João Silva'.";
        }
        String nome = nomeObj.toString().trim();
        if (nome.isEmpty()) {
            return "❌ O nome da pessoa não pode estar vazio.";
        }

        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();

        String sql = "SELECT v.codigo_voo, c.nome AS companhia, " +
                "orig.cidade AS origem, dest.cidade AS destino, " +
                "v.horario_partida, pg.preco, pg.classe, pg.assento, pg.data_compra " +
                "FROM passagem pg " +
                "JOIN pessoa p ON pg.pessoa_id = p.id " +
                "JOIN voo v ON pg.voo_id = v.id " +
                "JOIN companhia c ON v.companhia_id = c.id " +
                "JOIN aeroporto orig ON v.aeroporto_origem_id = orig.id " +
                "JOIN aeroporto dest ON v.aeroporto_destino_id = dest.id " +
                "WHERE p.nome = ? ORDER BY v.horario_partida DESC";

        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            StringBuilder resultado = new StringBuilder();
            resultado.append("✈️ Voos comprados por ").append(nome).append(":\n");
            boolean temVoos = false;

            while (rs.next()) {
                temVoos = true;
                String voo = rs.getString("codigo_voo");
                String cia = rs.getString("companhia");
                String origem = rs.getString("origem");
                String destino = rs.getString("destino");
                Timestamp partida = rs.getTimestamp("horario_partida");
                double preco = rs.getDouble("preco");
                String classe = rs.getString("classe");
                String assento = rs.getString("assento");
                Timestamp compra = rs.getTimestamp("data_compra");

                resultado.append(String.format("- Voo %s (%s): %s → %s, Partida: %s, Classe: %s, Assento: %s, Preço: R$ %.2f, Comprado em: %s\n",
                        voo, cia, origem, destino, partida, classe, assento, preco, compra));
            }

            if (!temVoos) {
                return String.format("ℹ️ Nenhum voo encontrado para a pessoa '%s'. Verifique o nome exato.", nome);
            }
            return resultado.toString();

        } catch (SQLException e) {
            return "❌ Erro ao consultar voos: " + e.getMessage();
        }
    }
}