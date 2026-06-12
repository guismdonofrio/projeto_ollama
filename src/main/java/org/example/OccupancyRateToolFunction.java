package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.util.Map;

public class OccupancyRateToolFunction implements ToolFunction {

    @Override
    public Object apply(Map<String, Object> arguments) {
        // LOG DE DEPURAÇÃO
        System.out.println(">>> 🔧 PassengersByFlightToolFunction FOI CHAMADA! Argumentos: " + arguments);

        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();

        String sql = "SELECT c.nome AS companhia, " +
                "AVG( (SELECT COUNT(*) FROM passagem pg WHERE pg.voo_id = v.id) * 100.0 / m.capacidade_max ) AS taxa_media " +
                "FROM voo v " +
                "JOIN companhia c ON v.companhia_id = c.id " +
                "JOIN aviao a ON v.aviao_id = a.id " +
                "JOIN modelo_aviao m ON a.modelo_id = m.id " +
                "GROUP BY c.id, c.nome " +
                "ORDER BY taxa_media DESC";

        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            StringBuilder resultado = new StringBuilder();
            resultado.append("Taxa média de ocupação por companhia:\n");
            boolean encontrou = false;

            while (rs.next()) {
                encontrou = true;
                String companhia = rs.getString("companhia");
                double taxa = rs.getDouble("taxa_media");
                resultado.append(String.format("- %s: %.2f%%\n", companhia, taxa));
            }

            if (!encontrou) {
                return "Nenhum dado de ocupação disponível.";
            }
            return resultado.toString();

        } catch (SQLException e) {
            return "Erro ao calcular taxa de ocupação: " + e.getMessage();
        }
    }
}