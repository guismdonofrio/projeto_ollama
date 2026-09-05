package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.util.Map;

public class CrewByFlightToolFunction implements ToolFunction {

    @Override
    public Object apply(Map<String, Object> arguments) {
        // LOG DE DEPURAÇÃO
        System.out.println(">>> 🔧 PassengersByFlightToolFunction FOI CHAMADA! Argumentos: " + arguments);

        String codigoVoo = arguments.get("codigo_voo").toString();

        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();

        String sql = "SELECT p.nome AS funcionario_nome, tv.funcao " +
                "FROM tripulacao_voo tv " +
                "JOIN voo v ON tv.voo_id = v.id " +
                "JOIN funcionario f ON tv.funcionario_id = f.id " +
                "JOIN pessoa p ON f.pessoa_id = p.id " +
                "WHERE v.codigo_voo = ? " +
                "ORDER BY tv.funcao, p.nome";

        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, codigoVoo);
            ResultSet rs = stmt.executeQuery();

            StringBuilder resultado = new StringBuilder();
            resultado.append("Tripulação do voo ").append(codigoVoo).append(":\n");
            boolean encontrou = false;

            while (rs.next()) {
                encontrou = true;
                String nome = rs.getString("funcionario_nome");
                String funcao = rs.getString("funcao");
                resultado.append("- ").append(nome).append(" (").append(funcao).append(")\n");
            }

            if (!encontrou) {
                return "Nenhum funcionário encontrado na tripulação do voo " + codigoVoo + ".";
            }
            return resultado.toString();

        } catch (SQLException e) {
            return "Erro ao consultar tripulação: " + e.getMessage();
        }
    }
}