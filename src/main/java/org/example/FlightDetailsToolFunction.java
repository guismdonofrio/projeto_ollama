package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.util.Map;

public class FlightDetailsToolFunction implements ToolFunction {

    @Override
    public Object apply(Map<String, Object> arguments) {
        // LOG DE DEPURAÇÃO
        System.out.println(">>> 🔧 PassengersByFlightToolFunction FOI CHAMADA! Argumentos: " + arguments);

        String codigoVoo = arguments.get("codigo_voo").toString();

        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();

        // Consulta detalhada do voo: horários, origem, destino, aeronave e companhia
        String sql = "SELECT " +
                "    v.codigo_voo, " +
                "    c.nome AS companhia, " +
                "    a.matricula, " +
                "    m.modelo, " +
                "    m.fabricante, " +
                "    m.capacidade_max, " +
                "    orig.codigo_iata AS origem_iata, " +
                "    orig.nome AS origem_nome, " +
                "    orig.cidade AS origem_cidade, " +
                "    orig.estado AS origem_estado, " +
                "    dest.codigo_iata AS destino_iata, " +
                "    dest.nome AS destino_nome, " +
                "    dest.cidade AS destino_cidade, " +
                "    dest.estado AS destino_estado, " +
                "    v.horario_partida, " +
                "    v.horario_chegada, " +
                "    v.status_voo " +
                "FROM voo v " +
                "JOIN companhia c ON v.companhia_id = c.id " +
                "JOIN aviao a ON v.aviao_id = a.id " +
                "JOIN modelo_aviao m ON a.modelo_id = m.id " +
                "JOIN aeroporto orig ON v.aeroporto_origem_id = orig.id " +
                "JOIN aeroporto dest ON v.aeroporto_destino_id = dest.id " +
                "WHERE v.codigo_voo = ?";

        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, codigoVoo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String companhia = rs.getString("companhia");
                String matricula = rs.getString("matricula");
                String modelo = rs.getString("modelo");
                String fabricante = rs.getString("fabricante");
                int capacidade = rs.getInt("capacidade_max");
                String origemIata = rs.getString("origem_iata");
                String origemNome = rs.getString("origem_nome");
                String origemCidade = rs.getString("origem_cidade");
                String origemEstado = rs.getString("origem_estado");
                String destinoIata = rs.getString("destino_iata");
                String destinoNome = rs.getString("destino_nome");
                String destinoCidade = rs.getString("destino_cidade");
                String destinoEstado = rs.getString("destino_estado");
                Timestamp partida = rs.getTimestamp("horario_partida");
                Timestamp chegada = rs.getTimestamp("horario_chegada");
                String status = rs.getString("status_voo");

                StringBuilder sb = new StringBuilder();
                sb.append("Detalhes do voo ").append(codigoVoo).append(":\n");
                sb.append("Companhia: ").append(companhia).append("\n");
                sb.append("Aeronave: ").append(modelo).append(" (").append(fabricante).append(") - Matrícula: ").append(matricula)
                        .append(", Capacidade: ").append(capacidade).append(" passageiros\n");
                sb.append("Origem: ").append(origemNome).append(" (").append(origemIata).append(") - ")
                        .append(origemCidade);
                if (origemEstado != null) sb.append("/").append(origemEstado);
                sb.append("\nDestino: ").append(destinoNome).append(" (").append(destinoIata).append(") - ")
                        .append(destinoCidade);
                if (destinoEstado != null) sb.append("/").append(destinoEstado);
                sb.append("\nPartida: ").append(partida);
                sb.append("\nChegada: ").append(chegada);
                sb.append("\nStatus: ").append(status);
                return sb.toString();
            } else {
                return "Voo " + codigoVoo + " não encontrado no sistema.";
            }

        } catch (SQLException e) {
            return "Erro ao conectar no banco de dados: " + e.getMessage();
        }
    }
}