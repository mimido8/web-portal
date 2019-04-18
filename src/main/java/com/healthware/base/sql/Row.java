package com.healthware.base.sql;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;

public abstract class Row {
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
        return columns;
    }

    Map<String, Field> getColumnFields() {
        Map<String, Field> columns = new HashMap<>();
        Arrays.stream(this.getClass().getFields())
            .filter(field -> field.getAnnotation(Column.class) != null)
            .forEach(field -> {
                columns.put(field.getAnnotation(Column.class).value(), field);
            });
        return columns;
    }

    private String getPrimaryKeyName() throws NoSuchFieldException {
        Optional<Field> primaryKeyField = Arrays.stream(this.getClass().getFields())
            .filter(field -> field.getAnnotation(PrimaryKey.class) != null)
            .findFirst();
        if (primaryKeyField.isPresent()) return primaryKeyField.get().getDeclaredAnnotation(Column.class).value();
        else throw new NoSuchFieldException("Row class does not have a primary key specified");
    }

    public void update() throws Exception {
        if (table == null) {
            throw new SQLException("Row has not been inserted into a table");
        } else {
            Map<String, Object> columnValues = getColumnValues();
            String primaryKeyName = getPrimaryKeyName();
            table.update(Collections.singletonMap(primaryKeyName, columnValues.get(primaryKeyName)), columnValues);
        }
    }

    public void delete() throws Exception {
        String primaryKey = getPrimaryKeyName();
        table.delete(Collections.singletonMap(primaryKey, getColumnValues().get(primaryKey)));
    }
}
