package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class CanceledFlightsToolFunction implements ToolFunction {

    @Override
    public Object apply(Map<String, Object> arguments) {
        // LOG DE DEPURAÇÃO
        System.out.println(">>> 🔧 PassengersByFlightToolFunction FOI CHAMADA! Argumentos: " + arguments);

        String inicioStr = arguments.containsKey("data_inicio") ? arguments.get("data_inicio").toString() : null;
        String fimStr = arguments.containsKey("data_fim") ? arguments.get("data_fim").toString() : null;
        LocalDate dataInicio = null, dataFim = null;

        if (inicioStr != null && !inicioStr.trim().isEmpty()) {
            try {
                dataInicio = LocalDate.parse(inicioStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                return "❌ Data_início inválida. Use YYYY-MM-DD.";
            }
        }
        if (fimStr != null && !fimStr.trim().isEmpty()) {
            try {
                dataFim = LocalDate.parse(fimStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                return "❌ Data_fim inválida. Use YYYY-MM-DD.";
            }
        }

        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();

        StringBuilder sql = new StringBuilder(
                "SELECT v.codigo_voo, c.nome AS companhia, orig.cidade AS origem, dest.cidade AS destino, " +
                        "v.horario_partida FROM voo v " +
                        "JOIN companhia c ON v.companhia_id = c.id " +
                        "JOIN aeroporto orig ON v.aeroporto_origem_id = orig.id " +
                        "JOIN aeroporto dest ON v.aeroporto_destino_id = dest.id " +
                        "WHERE v.status_voo = 'Cancelado'"
        );
        if (dataInicio != null) sql.append(" AND DATE(v.horario_partida) >= ?");
        if (dataFim != null) sql.append(" AND DATE(v.horario_partida) <= ?");
        sql.append(" ORDER BY v.horario_partida");

        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement stmt = con.prepareStatement(sql.toString())) {

            int idx = 1;
            if (dataInicio != null) stmt.setDate(idx++, Date.valueOf(dataInicio));
            if (dataFim != null) stmt.setDate(idx, Date.valueOf(dataFim));

            ResultSet rs = stmt.executeQuery();
            StringBuilder resultado = new StringBuilder();
            resultado.append("🛑 Voos cancelados");
            if (dataInicio != null || dataFim != null) {
                resultado.append(" no período ");
                if (dataInicio != null) resultado.append("após ").append(dataInicio);
                if (dataFim != null) resultado.append(" até ").append(dataFim);
            }
            resultado.append(":\n");

            boolean encontrou = false;
            while (rs.next()) {
                encontrou = true;
                resultado.append(String.format("- Voo %s (%s): %s → %s, Partida: %s\n",
                        rs.getString("codigo_voo"), rs.getString("companhia"),
                        rs.getString("origem"), rs.getString("destino"),
                        rs.getTimestamp("horario_partida")));
            }
            return encontrou ? resultado.toString() : "ℹ️ Nenhum voo cancelado encontrado.";
        } catch (SQLException e) {
            return "❌ Erro ao buscar cancelados: " + e.getMessage();
        }
    }
}