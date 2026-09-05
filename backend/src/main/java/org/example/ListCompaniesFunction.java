package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListCompaniesFunction implements ToolFunction {
    @Override
    public Object apply(Map<String, Object> arguments) {
        // LOG DE DEPURAÇÃO
        System.out.println(">>> 🔧 PassengersByFlightToolFunction FOI CHAMADA! Argumentos: " + arguments);

        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();

        List<String> companhias = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nome FROM companhia")) {

            while (rs.next()) {
                companhias.add(rs.getString("nome"));
            }
            return "Companhias cadastradas: " + String.join(", ", companhias);

        } catch (SQLException e) {
            return "Erro ao buscar companhias: " + e.getMessage();
        }
    }
}