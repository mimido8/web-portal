package com.healthware.base.sql;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Database {
    private ComboPooledDataSource connectionPool;

    private Database() { }

    public static Database connect(String url, String username, String password) throws Exception {
        Database database = new Database();
        database.connectionPool = new ComboPooledDataSource();
        database.connectionPool.setDriverClass("org.postgresql.Driver");
        database.connectionPool.setJdbcUrl(url);
        database.connectionPool.setUser(username);
        database.connectionPool.setPassword(password);
        return database;
    }

    public <T extends Row> Table<T> getTable(String name, Class<T> type) {
        return new Table(this, name, type);
    }

    private String queryValue(Object value) {
        if (value instanceof String || Enum.class.isAssignableFrom(value.getClass())) {
            return "'" + value + "'";
        } else if (value instanceof Date) {
            return "date '" + value + "'";
        } else {
            return value.toString();
        }
    }

    private String columnList(Collection<String> names) {
        return "(" + String.join(", ", names) + ")";
    }

    private String queryValueList(Collection<Object> values) {
        return "(" + values.stream().map(this::queryValue).collect(Collectors.joining(", ")) + ")";
    }

    private String conditionList(Collection<Map.Entry<String, Object>> entries) {
        if (entries.size() > 0) {
            return " WHERE " + entries.stream()
                .map(entry -> entry.getKey() + " = " + queryValue(entry.getValue()))
                .collect(Collectors.joining(" AND "));
        } else {
            return "";
        }
    }

    void update(String into, Map<String, Object> conditions, Map<String, Object> values) throws SQLException {
        PreparedStatement statement = connectionPool.getConnection().prepareStatement(
                "UPDATE " + into +
                " SET " + columnList(values.keySet()) +
                " = " + queryValueList(values.values()) + conditionList(conditions.entrySet()));
        if (statement.executeUpdate() <= 0) throw new SQLException("No row was updated");
    }

    void insert(String into, Map<String, Object> columns) throws SQLException {
        PreparedStatement statement = connectionPool.getConnection().prepareStatement(
                "INSERT INTO " + into +  " " + columnList(columns.keySet()) +
                " VALUES " + queryValueList(columns.values()));
        if (statement.executeUpdate() <= 0) throw new SQLException("No row was inserted");
    }

    ResultSet select(String into, Map<String, Object> conditions) throws SQLException {
        PreparedStatement statement = connectionPool.getConnection().prepareStatement(
                "SELECT * FROM " + into + conditionList(conditions.entrySet()));
        return statement.executeQuery();
    }

    int countWhere(String into, Map<String, Object> conditions) throws SQLException {
        PreparedStatement statement = connectionPool.getConnection().prepareStatement(
                "SELECT COUNT(*) FROM " + into + conditionList(conditions.entrySet()));
        ResultSet results =  statement.executeQuery();
        results.next();
        return results.getInt(1);
    }

    void delete(String into, Map<String, Object> conditions) throws SQLException {
        PreparedStatement statement = connectionPool.getConnection().prepareStatement(
                "DELETE FROM " + into + conditionList(conditions.entrySet()));
        if (statement.executeUpdate() <= 0) throw new SQLException("No row was deleted");
    }
}
