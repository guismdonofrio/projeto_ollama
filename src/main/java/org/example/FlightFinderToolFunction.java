package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.util.Map;

public class FlightFinderToolFunction implements ToolFunction {

    @Override
    public Object apply(Map<String, Object> arguments) {
        String codigo = arguments.get("codigo_voo").toString();

        String url = "jdbc:mysql://localhost:3306/sistema_aeroporto";
        String user = "root";
        String pass = "";

        // Query simplificada: busca apenas na tabela voo
        String sql = "SELECT codigo_voo, status_voo FROM voo WHERE codigo_voo = ?";

        try {
            Connection con = DriverManager.getConnection(url, user, pass);
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, codigo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status_voo");
                con.close();
                return "O voo " + codigo + " está com status: " + status;
            } else {
                con.close();
                return "Não encontrei o voo " + codigo + " no sistema.";
            }

        } catch (SQLException e) {
            return "Erro ao conectar no banco: " + e.getMessage();
        }
    }
}