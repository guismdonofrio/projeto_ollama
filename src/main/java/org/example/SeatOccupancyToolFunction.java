package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeatOccupancyToolFunction implements ToolFunction {

    @Override
    public Object apply(Map<String, Object> arguments) {
        String codigoVoo = arguments.get("codigo_voo").toString();

        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();

        // 1. Obter ID do voo, capacidade máxima da aeronave e matrícula
        String sqlVoo = "SELECT v.id, m.capacidade_max, a.matricula " +
                "FROM voo v " +
                "JOIN aviao a ON v.aviao_id = a.id " +
                "JOIN modelo_aviao m ON a.modelo_id = m.id " +
                "WHERE v.codigo_voo = ?";

        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            Integer vooId = null;
            Integer capacidadeMax = null;
            String matricula = null;

            try (PreparedStatement stmtVoo = con.prepareStatement(sqlVoo)) {
                stmtVoo.setString(1, codigoVoo);
                ResultSet rsVoo = stmtVoo.executeQuery();
                if (rsVoo.next()) {
                    vooId = rsVoo.getInt("id");
                    capacidadeMax = rsVoo.getInt("capacidade_max");
                    matricula = rsVoo.getString("matricula");
                } else {
                    return "Voo " + codigoVoo + " não encontrado.";
                }
            }

            // 2. Obter lista de assentos ocupados neste voo
            String sqlOcupados = "SELECT assento FROM passagem WHERE voo_id = ? ORDER BY assento";
            List<String> ocupados = new ArrayList<>();
            try (PreparedStatement stmtOcup = con.prepareStatement(sqlOcupados)) {
                stmtOcup.setInt(1, vooId);
                ResultSet rsOcup = stmtOcup.executeQuery();
                while (rsOcup.next()) {
                    ocupados.add(rsOcup.getString("assento"));
                }
            }

            int ocupadosCount = ocupados.size();
            int disponiveisCount = capacidadeMax - ocupadosCount;

            StringBuilder resultado = new StringBuilder();
            resultado.append("Voo ").append(codigoVoo).append(" (Aeronave ").append(matricula).append(")\n");
            resultado.append("Capacidade total: ").append(capacidadeMax).append(" assentos\n");
            resultado.append("Assentos ocupados (").append(ocupadosCount).append("): ");
            if (ocupados.isEmpty()) {
                resultado.append("nenhum");
            } else {
                resultado.append(String.join(", ", ocupados));
            }
            resultado.append("\n");
            resultado.append("Assentos disponíveis: ").append(disponiveisCount);
            if (disponiveisCount > 0) {
                resultado.append(" (consulte o mapa de assentos para detalhes)");
            }
            return resultado.toString();

        } catch (SQLException e) {
            return "Erro ao consultar ocupação de assentos: " + e.getMessage();
        }
    }
}