package com.healthware.base.sql;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Table<T extends Row> {
    private Database database;
    private String name;
    private Class<T> type;

    protected Table(Database database, String name, Class<T> type) {
        this.database = database;
        this.name = name;
        this.type = type;
    }

    public void update(Map<String, Object> conditions, Map<String, Object> values) throws SQLException {
        database.update(name, conditions, values);
    }

    public T insert(T row) throws Exception {
        row.table = this;
        database.insert(name, row.getColumnValues());
        return row;
    }

    private T getNextRow(ResultSet results) throws Exception {
        T row = type.newInstance();
        row.table = this;
        for (Map.Entry<String, Field> entry : row.getColumnFields().entrySet()) {
            if (Enum.class.isAssignableFrom(entry.getValue().getType())) {
                Method valueOf = entry.getValue().getType().getMethod("valueOf", String.class);
                entry.getValue().set(row, valueOf.invoke(null, results.getString(entry.getKey())));
            } else {
                String fieldType = entry.getValue().getType().getSimpleName();
                Method getter = ResultSet.class.getMethod("get" + fieldType.substring(0, 1).toUpperCase() + fieldType.substring(1), String.class);
                entry.getValue().set(row, getter.invoke(results, entry.getValue().getDeclaredAnnotation(Column.class).value()));
            }
        }
        return row;
    }

    public T selectFirstWhere(Map<String, Object> conditions) throws Exception {
        ResultSet results = database.select(name, conditions);
        if (!results.next()) throw new SQLException("No rows found");
        return getNextRow(results);
    }

    public T selectFirstWhere(String key, Object value) throws Exception {
        return selectFirstWhere(Collections.singletonMap(key, value));
    }

    public T selectFirst() throws Exception {
        return selectFirstWhere(Collections.emptyMap());
    }

    public List<T> selectAll(Map<String, Object> conditions) throws Exception {
        ResultSet results = database.select(name, conditions);
        List<T> selection = new ArrayList<>();
        while (results.next()) selection.add(getNextRow(results));
        return selection;
    }

    public List<T> selectAll() throws Exception {
        return selectAll(Collections.emptyMap());
    }

    public int countWhere(Map<String, Object> conditions) throws SQLException {
        return database.countWhere(name, conditions);
    }

    public int countWhere(String key, Object value) throws SQLException {
        return database.countWhere(name, Collections.singletonMap(key, value));
    }

    public int count() throws SQLException {
        return countWhere(Collections.emptyMap());
    }

    public void delete(Map<String, Object> conditions) throws SQLException {
        database.delete(name, conditions);
    }
}
