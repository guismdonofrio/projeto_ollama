package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.util.Map;

public class AircraftCapacityToolFunction implements ToolFunction {

    @Override
    public Object apply(Map<String, Object> arguments) {
        Object codigoObj = arguments.get("codigo_voo");
        if (codigoObj == null) {
            return "❌ Parâmetro 'codigo_voo' obrigatório. Ex: 'LA3200'.";
        }
        String codigoVoo = codigoObj.toString().trim();
        if (codigoVoo.isEmpty()) return "❌ Código do voo vazio.";

        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();

        String sql = "SELECT m.modelo, m.fabricante, m.capacidade_max, a.matricula, " +
                "(SELECT COUNT(*) FROM passagem WHERE voo_id = v.id) AS ocupados " +
                "FROM voo v JOIN aviao a ON v.aviao_id = a.id " +
                "JOIN modelo_aviao m ON a.modelo_id = m.id WHERE v.codigo_voo = ?";

        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, codigoVoo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String modelo = rs.getString("modelo");
                String fabricante = rs.getString("fabricante");
                int cap = rs.getInt("capacidade_max");
                String mat = rs.getString("matricula");
                int ocup = rs.getInt("ocupados");
                return String.format("Voo %s - Aeronave: %s %s (Matr. %s)\nCapacidade total: %d passageiros\nOcupados: %d\nDisponíveis: %d",
                        codigoVoo, fabricante, modelo, mat, cap, ocup, cap - ocup);
            } else {
                return "Voo " + codigoVoo + " não encontrado.";
            }
        } catch (SQLException e) {
            return "❌ Erro ao consultar capacidade: " + e.getMessage();
        }
    }
}