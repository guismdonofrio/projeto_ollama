package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.util.Map;

public class TopPassengersToolFunction implements ToolFunction {

    @Override
    public Object apply(Map<String, Object> arguments) {
        int limite = 5;
        if (arguments.containsKey("limite")) {
            try {
                limite = Integer.parseInt(arguments.get("limite").toString());
                if (limite <= 0) limite = 5;
            } catch (NumberFormatException e) { /* mantém 5 */ }
        }

        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();

        String sql = "SELECT p.nome, COUNT(pg.id) AS total_voos FROM pessoa p " +
                "JOIN passagem pg ON p.id = pg.pessoa_id GROUP BY p.id ORDER BY total_voos DESC LIMIT ?";

        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, limite);
            ResultSet rs = stmt.executeQuery();

            StringBuilder resultado = new StringBuilder();
            resultado.append(String.format("🏆 Top %d passageiros que mais viajaram:\n", limite));
            int pos = 1;
            boolean tem = false;
            while (rs.next()) {
                tem = true;
                resultado.append(String.format("%d. %s - %d voo(s)\n", pos++, rs.getString("nome"), rs.getInt("total_voos")));
            }
            return tem ? resultado.toString() : "ℹ️ Nenhum passageiro encontrado.";
        } catch (SQLException e) {
            return "❌ Erro ao consultar top passageiros: " + e.getMessage();
        }
    }
}