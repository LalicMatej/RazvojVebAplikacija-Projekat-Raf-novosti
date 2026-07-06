package org.raflab.backendprojekat.repository;

import java.sql.*;
import java.util.Optional;

public abstract class MySqlAbstractRepository {

    public MySqlAbstractRepository() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected Connection newConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDatabaseName() +
                        "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8mb4_unicode_ci",
                getUsername(),
                getPassword()
        );
    }

    protected String getHost()         { return "localhost"; }
    protected int    getPort()         { return 3306; }
    protected String getDatabaseName() { return "raf_news"; }
    protected String getUsername()     { return "root"; }
    protected String getPassword()     { return "root"; }

    protected void closeStatement(Statement statement) {
        try {
            Optional.ofNullable(statement).ifPresent(s -> {
                try { s.close(); } catch (SQLException e) { e.printStackTrace(); }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    protected void closeResultSet(ResultSet resultSet) {
        try {
            Optional.ofNullable(resultSet).ifPresent(rs -> {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    protected void closeConnection(Connection connection) {
        try {
            Optional.ofNullable(connection).ifPresent(c -> {
                try { c.close(); } catch (SQLException e) { e.printStackTrace(); }
            });
        } catch (Exception e) { e.printStackTrace(); }
    }
}
