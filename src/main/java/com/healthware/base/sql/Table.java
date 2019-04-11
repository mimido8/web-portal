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

    public T selectFirst(Map<String, Object> conditions) throws Exception {
        ResultSet results = database.select(name, conditions);
        if (!results.next()) throw new SQLException("No rows found");
        T row = (T)type.newInstance();
        for (String name : row.getColumnFields()) {
            Field field = type.getField(name);
            String fieldType = field.getType().getSimpleName();
            Method getter = ResultSet.class.getMethod("get" + fieldType.substring(0, 1).toUpperCase() + fieldType.substring(1), String.class);
            field.set(row, getter.invoke(results, name));
        }
    }
}
