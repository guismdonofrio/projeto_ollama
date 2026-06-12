package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class FlightsFromAirportToolFunction implements ToolFunction {

    @Override
    public Object apply(Map<String, Object> arguments) {
        // LOG DE DEPURAÇÃO
        System.out.println(">>> 🔧 PassengersByFlightToolFunction FOI CHAMADA! Argumentos: " + arguments);

        // Valida código IATA
        Object iataObj = arguments.get("codigo_iata");
        if (iataObj == null) {
            return "❌ Parâmetro 'codigo_iata' é obrigatório (ex: 'SDU', 'GRU').";
        }
        String codigoIata = iataObj.toString().trim().toUpperCase();
        if (codigoIata.isEmpty()) {
            return "❌ Código IATA não pode estar vazio.";
        }

        // Data opcional
        String dataStr = arguments.containsKey("data") ? arguments.get("data").toString() : null;
        LocalDate data;
        if (dataStr == null || dataStr.trim().isEmpty()) {
            data = LocalDate.now();
        } else {
            try {
                data = LocalDate.parse(dataStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                return "❌ Formato de data inválido. Use YYYY-MM-DD (ex: 2026-06-12).";
            }
        }

        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();

        String sql = "SELECT v.codigo_voo, c.nome AS companhia, v.horario_partida, v.horario_chegada, " +
                "dest.codigo_iata AS destino_iata, dest.cidade AS destino_cidade, v.status_voo " +
                "FROM voo v " +
                "JOIN companhia c ON v.companhia_id = c.id " +
                "JOIN aeroporto orig ON v.aeroporto_origem_id = orig.id " +
                "JOIN aeroporto dest ON v.aeroporto_destino_id = dest.id " +
                "WHERE orig.codigo_iata = ? AND DATE(v.horario_partida) = ? " +
                "ORDER BY v.horario_partida";

        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, codigoIata);
            stmt.setDate(2, Date.valueOf(data));
            ResultSet rs = stmt.executeQuery();

            StringBuilder resultado = new StringBuilder();
            resultado.append(String.format("🛫 Voos com origem em %s no dia %s:\n", codigoIata, data));
            boolean encontrou = false;

            while (rs.next()) {
                encontrou = true;
                String voo = rs.getString("codigo_voo");
                String cia = rs.getString("companhia");
                Timestamp partida = rs.getTimestamp("horario_partida");
                Timestamp chegada = rs.getTimestamp("horario_chegada");
                String destIata = rs.getString("destino_iata");
                String destCid = rs.getString("destino_cidade");
                String status = rs.getString("status_voo");
                resultado.append(String.format("- Voo %s (%s): %s → %s (%s), Partida: %s, Chegada: %s, Status: %s\n",
                        voo, cia, codigoIata, destIata, destCid, partida, chegada, status));
            }

            if (!encontrou) {
                return String.format("ℹ️ Nenhum voo encontrado com origem em %s na data %s.", codigoIata, data);
            }
            return resultado.toString();

        } catch (SQLException e) {
            return "❌ Erro ao consultar voos: " + e.getMessage();
        }
    }
}