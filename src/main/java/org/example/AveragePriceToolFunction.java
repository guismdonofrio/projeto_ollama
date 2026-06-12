package org.example;

import io.github.ollama4j.tools.ToolFunction;
import java.sql.*;
import java.util.Map;

public class AveragePriceToolFunction implements ToolFunction {

    @Override
    public Object apply(Map<String, Object> arguments) {
        String companhia = arguments.containsKey("companhia") ? arguments.get("companhia").toString() : null;
        String classe = arguments.containsKey("classe") ? arguments.get("classe").toString() : null;

        // Se ambos forem nulos, retorna resumo geral
        if ((companhia == null || companhia.trim().isEmpty()) && (classe == null || classe.trim().isEmpty())) {
            return getResumoGeral();
        }

        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();

        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            // Caso 1: companhia e classe específicos
            if (companhia != null && !companhia.trim().isEmpty() && classe != null && !classe.trim().isEmpty()) {
                String sql = "SELECT AVG(pg.preco) AS media FROM passagem pg " +
                        "JOIN voo v ON pg.voo_id = v.id " +
                        "JOIN companhia c ON v.companhia_id = c.id " +
                        "WHERE c.nome = ? AND pg.classe = ?";
                try (PreparedStatement stmt = con.prepareStatement(sql)) {
                    stmt.setString(1, companhia);
                    stmt.setString(2, classe);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next() && rs.getObject("media") != null) {
                        return String.format("💰 Preço médio da %s na classe %s: R$ %.2f", companhia, classe, rs.getDouble("media"));
                    } else {
                        return String.format("ℹ️ Nenhuma passagem encontrada para %s na classe %s.", companhia, classe);
                    }
                }
            }
            // Caso 2: apenas companhia
            else if (companhia != null && !companhia.trim().isEmpty()) {
                String sql = "SELECT pg.classe, AVG(pg.preco) AS media FROM passagem pg " +
                        "JOIN voo v ON pg.voo_id = v.id " +
                        "JOIN companhia c ON v.companhia_id = c.id " +
                        "WHERE c.nome = ? GROUP BY pg.classe";
                try (PreparedStatement stmt = con.prepareStatement(sql)) {
                    stmt.setString(1, companhia);
                    ResultSet rs = stmt.executeQuery();
                    StringBuilder sb = new StringBuilder();
                    sb.append("📊 Preços médios por classe para ").append(companhia).append(":\n");
                    boolean has = false;
                    while (rs.next()) {
                        has = true;
                        sb.append(String.format("- %s: R$ %.2f\n", rs.getString("classe"), rs.getDouble("media")));
                    }
                    return has ? sb.toString() : "ℹ️ Nenhuma passagem para " + companhia;
                }
            }
            // Caso 3: apenas classe
            else if (classe != null && !classe.trim().isEmpty()) {
                String sql = "SELECT c.nome AS companhia, AVG(pg.preco) AS media FROM passagem pg " +
                        "JOIN voo v ON pg.voo_id = v.id " +
                        "JOIN companhia c ON v.companhia_id = c.id " +
                        "WHERE pg.classe = ? GROUP BY c.nome";
                try (PreparedStatement stmt = con.prepareStatement(sql)) {
                    stmt.setString(1, classe);
                    ResultSet rs = stmt.executeQuery();
                    StringBuilder sb = new StringBuilder();
                    sb.append("📊 Preços médios por companhia na classe ").append(classe).append(":\n");
                    boolean has = false;
                    while (rs.next()) {
                        has = true;
                        sb.append(String.format("- %s: R$ %.2f\n", rs.getString("companhia"), rs.getDouble("media")));
                    }
                    return has ? sb.toString() : "ℹ️ Nenhuma passagem na classe " + classe;
                }
            }
        } catch (SQLException e) {
            return "❌ Erro ao calcular média: " + e.getMessage();
        }
        return "❌ Não foi possível processar a solicitação.";
    }

    private String getResumoGeral() {
        String dbUrl = ConfigDB.getUrl() + ConfigDB.getName();
        String dbUser = ConfigDB.getUsername();
        String dbPass = ConfigDB.getPassword();
        StringBuilder saida = new StringBuilder();
        try (Connection con = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            saida.append("=== Preços médios por companhia ===\n");
            String sqlComp = "SELECT c.nome, AVG(pg.preco) AS media FROM passagem pg " +
                    "JOIN voo v ON pg.voo_id = v.id " +
                    "JOIN companhia c ON v.companhia_id = c.id GROUP BY c.nome";
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sqlComp)) {
                while (rs.next()) {
                    saida.append(String.format("- %s: R$ %.2f\n", rs.getString("nome"), rs.getDouble("media")));
                }
            }
            saida.append("\n=== Preços médios por classe ===\n");
            String sqlClasse = "SELECT classe, AVG(preco) AS media FROM passagem GROUP BY classe";
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sqlClasse)) {
                while (rs.next()) {
                    saida.append(String.format("- %s: R$ %.2f\n", rs.getString("classe"), rs.getDouble("media")));
                }
            }
        } catch (SQLException e) {
            return "❌ Erro ao obter resumo: " + e.getMessage();
        }
        return saida.toString();
    }
}