package com.healthware.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Table {
    private Database database;
    private String name;

    protected Table(Database database, String name) {
        this.database = database;
        this.name = name;
    }

    public void update(String primaryKeyName, Map<String, Object> columns) throws SQLException {
        database.update(name, primaryKeyName, columns);
    }

    public <T extends Row> T insert(T row) throws Exception {
        row.table = this;
        database.insert(name, row.getColumnValues());
        return row;
    }

    public <T extends Row> T selectFirst(Class<T> type, Map<String, Object> columns) throws Exception {
        ResultSet results = database.select(name, columns);
        if (!results.next()) throw new SQLException("No rows found");
        T row = (T)type.newInstance();
        for (String name : row.getColumnNames()) {
            Field field = type.getField(name);
            String fieldType = field.getType().getSimpleName();
            Method getter = ResultSet.class.getMethod("get" + fieldType.substring(0, 1).toUpperCase() + fieldType.substring(1), String.class);
            field.set(row, getter.invoke(results, name));
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Column {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface PrimaryKey {
        String value();
    }

    public static abstract class Row {
        Table table = null;

        Map<String, Object> getColumnValues() {
            Map<String, Object> columns = new HashMap<>();
            Arrays.stream(this.getClass().getFields())
                    .filter(field -> field.getAnnotation(Column.class) != null)
                    .forEach(field -> {
                        try {
                            columns.put(field.getAnnotation(Column.class).value(), field.get(this));
                        } catch (IllegalAccessException ex) {
                            columns.put(field.getAnnotation(Column.class).value(), null);
                        }
                    });
            Arrays.stream(this.getClass().getFields())
                    .filter(field -> field.getAnnotation(PrimaryKey.class) != null)
                    .forEach(field -> {
                        try {
                            columns.put(field.getAnnotation(PrimaryKey.class).value(), field.get(this));
                        } catch (IllegalAccessException ex) {
                            columns.put(field.getAnnotation(PrimaryKey.class).value(), null);
                        }
                    });
            return columns;
        }

        Map<String, Field> getColumnFields() {
            Map<String, Field> columns = new HashMap<>();
            Arrays.stream(this.getClass().getFields())
                    .filter(field -> field.getAnnotation(Column.class) != null)
                    .forEach(field -> {
                        columns.put(field.getAnnotation(Column.class).value(), field);
                    });
            Arrays.stream(this.getClass().getFields())
                    .filter(field -> field.getAnnotation(PrimaryKey.class) != null)
                    .forEach(field -> {
                        columns.put(field.getAnnotation(PrimaryKey.class).value(), field);
                    });
            return columns;
        }

        private String getPrimaryKeyName() throws NoSuchFieldException {
            Optional<Field> primaryKeyField = Arrays.stream(this.getClass().getFields())
                    .filter(field -> field.getAnnotation(PrimaryKey.class) != null)
                    .findFirst();
            if (primaryKeyField.isPresent()) return primaryKeyField.get().getDeclaredAnnotation(PrimaryKey.class).value();
            else throw new NoSuchFieldException("Row class does not have a primary key specified");
        }

        public void update() throws Exception {
            if (table == null) throw new SQLException("Row has not been inserted into a table");
            else table.update(getPrimaryKeyName(), getColumnValues());
        }
    }
}
