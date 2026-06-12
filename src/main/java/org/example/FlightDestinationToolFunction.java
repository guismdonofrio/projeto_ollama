package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.util.Map;

public class FlightDestinationToolFunction implements ToolFunction {

    @Override
    public Object apply(Map<String, Object> arguments) {
        // LOG DE DEPURAÇÃO - ESSA LINHA APARECE NO CONSOLE SE A TOOL FOR CHAMADA
        System.out.println(">>> 🔧 FlightDestinationToolFunction FOI CHAMADA! Argumentos: " + arguments);

        // Validação
        Object cidadeObj = arguments.get("cidade");
        if (cidadeObj == null) {
            return "ERRO: Parâmetro 'cidade' é obrigatório. Exemplo: cidade='Rio de Janeiro'";
        }
        String cidade = cidadeObj.toString().trim();
        if (cidade.isEmpty()) {
            return "ERRO: Nome da cidade não pode estar vazio.";
        }

        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();

        // Consulta sem filtro de data (para teste imediato)
        String sql = "SELECT v.codigo_voo, c.nome AS companhia, v.horario_partida, v.status_voo " +
                "FROM voo v " +
                "JOIN companhia c ON v.companhia_id = c.id " +
                "JOIN aeroporto a ON v.aeroporto_destino_id = a.id " +
                "WHERE a.cidade = ? " +
                "ORDER BY v.horario_partida ASC";

        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, cidade);
            ResultSet rs = stmt.executeQuery();

            StringBuilder resultado = new StringBuilder();
            boolean encontrou = false;
            while (rs.next()) {
                encontrou = true;
                String codigo = rs.getString("codigo_voo");
                String cia = rs.getString("companhia");
                Timestamp partida = rs.getTimestamp("horario_partida");
                String status = rs.getString("status_voo");
                resultado.append(String.format("- %s (%s) | Partida: %s | Status: %s\n",
                        codigo, cia, partida, status));
            }

            if (!encontrou) {
                return "Nenhum voo encontrado com destino a '" + cidade + "'.";
            }
            return "🛬 Voos com destino a " + cidade + ":\n" + resultado.toString();

        } catch (SQLException e) {
            return "Erro no banco: " + e.getMessage();
        }
    }
}