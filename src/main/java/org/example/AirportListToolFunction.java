package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.util.Map;

public class AirportListToolFunction implements ToolFunction {

    @Override
    public Object apply(Map<String, Object> arguments) {
        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();

        String sql = "SELECT codigo_iata, nome, cidade, estado, pais FROM aeroporto ORDER BY cidade";

        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            StringBuilder result = new StringBuilder("Aeroportos disponíveis:\n");
            boolean found = false;

            while (rs.next()) {
                found = true;
                String iata = rs.getString("codigo_iata");
                String nome = rs.getString("nome");
                String cidade = rs.getString("cidade");
                String estado = rs.getString("estado");
                String pais = rs.getString("pais");

                result.append(String.format("- %s (%s) - %s", nome, iata, cidade));
                if (estado != null && !estado.isEmpty()) {
                    result.append("/").append(estado);
                }
                result.append(", ").append(pais).append("\n");
            }

            if (!found) {
                return "Nenhum aeroporto cadastrado no sistema.";
            }
            return result.toString();

        } catch (SQLException e) {
            return "Erro ao conectar no banco de dados: " + e.getMessage();
        }
    }
}