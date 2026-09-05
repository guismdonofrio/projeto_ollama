package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.util.Map;

public class PassengersByFlightToolFunction implements ToolFunction {

    @Override
    public Object apply(Map<String, Object> arguments) {
        // LOG DE DEPURAÇÃO
        System.out.println(">>> 🔧 PassengersByFlightToolFunction FOI CHAMADA! Argumentos: " + arguments);

        // Validação do parâmetro
        Object codigoObj = arguments.get("codigo_voo");
        if (codigoObj == null) {
            return "ERRO: Parâmetro 'codigo_voo' é obrigatório. Exemplo: codigo_voo='LA3200'";
        }
        String codigoVoo = codigoObj.toString().trim();
        if (codigoVoo.isEmpty()) {
            return "ERRO: Código do voo não pode estar vazio.";
        }

        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();

        String sql = "SELECT p.nome, pg.assento " +
                "FROM passagem pg " +
                "JOIN pessoa p ON pg.pessoa_id = p.id " +
                "JOIN voo v ON pg.voo_id = v.id " +
                "WHERE v.codigo_voo = ? " +
                "ORDER BY pg.assento";

        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, codigoVoo);
            ResultSet rs = stmt.executeQuery();

            StringBuilder resultado = new StringBuilder();
            boolean temPassageiros = false;
            while (rs.next()) {
                temPassageiros = true;
                String nome = rs.getString("nome");
                String assento = rs.getString("assento");
                resultado.append(String.format("- %s (Assento %s)\n", nome, assento));
            }

            if (!temPassageiros) {
                return "Nenhum passageiro encontrado para o voo " + codigoVoo + ".";
            }
            return "🧑‍✈️ Passageiros do voo " + codigoVoo + ":\n" + resultado.toString();

        } catch (SQLException e) {
            return "Erro no banco de dados: " + e.getMessage();
        }
    }
}