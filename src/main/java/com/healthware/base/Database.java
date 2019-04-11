package com.healthware.base;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
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

    public Table getTable(String name) {
        return new Table(this, name);
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

    public void update(String into, String primaryKeyName, Map<String, Object> columns) throws SQLException {
        PreparedStatement statement = connectionPool.getConnection().prepareStatement(
                "UPDATE " + into + " SET (" + String.join(", ", columns.keySet()) + ") = (" +
                Arrays.stream(columns.values().toArray()).map(this::queryValue).collect(Collectors.joining(", ")) +
                ") WHERE " + primaryKeyName + " = " + queryValue(columns.get(primaryKeyName)));
        if (statement.executeUpdate() <= 0) throw new SQLException("No row was updated");
    }

    public void insert(String into, Map<String, Object> columns) throws SQLException {
        PreparedStatement statement = connectionPool.getConnection().prepareStatement(
                "INSERT INTO " + into + " (" + String.join(", ", columns.keySet()) + ")  VALUES (" +
                Arrays.stream(columns.values().toArray()).map(this::queryValue).collect(Collectors.joining(", ")) + ")");
        if (statement.executeUpdate() <= 0) throw new SQLException("No row was inserted");
    }

    public QueryBuilder buildQuery(String fragment, Object... args) throws SQLException {
        return new QueryBuilder(connectionPool.getConnection(), fragment, args);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.CONSTRUCTOR)
    public @interface ColumnMapping {
        String[] value();
    }

    private static Constructor<?> getColumnMappedCtor(Class<?> type) throws NoSuchElementException {
        Optional<Constructor<?>> rowCtor = Arrays.stream(type.getDeclaredConstructors())
                .filter(ctor -> ctor.getDeclaredAnnotation(ColumnMapping.class) != null).findFirst();
        if (!rowCtor.isPresent()) throw new NoSuchElementException(type.getSimpleName() + " does not have a column-mapped constructor");
        return rowCtor.get();
    }

    private static String getterName(Class<?> type) {
        return "get" + type.getSimpleName().substring(0, 1).toUpperCase() + type.getSimpleName().substring(1);
    }

    public static <T> T getRow(Class<T> type, ResultSet resultSet) throws Exception {
        if (!resultSet.next()) throw new SQLException("ResultSet is empty");
        Constructor<?> mappedCtor = getColumnMappedCtor(type);
        String[] columnNames = mappedCtor.getDeclaredAnnotation(ColumnMapping.class).value();
        Class<?>[] ctorParameterTypes = mappedCtor.getParameterTypes();
        if (columnNames.length != ctorParameterTypes.length)
            throw new SQLException("Constructor parameters do not match column mappings");
        List<Object> parameters = new ArrayList<>();
        for (int i = 0; i < ctorParameterTypes.length; i++)
            parameters.add(ResultSet.class.getMethod(getterName(ctorParameterTypes[i]), String.class).invoke(resultSet, columnNames[i]));
        return (T)mappedCtor.newInstance(parameters);
    }
}
