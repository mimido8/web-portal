package com.healthware.base;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryBuilder {
    private Connection connection;
    private StringBuilder expression;

    public QueryBuilder(Connection connection, String fragment, Object... args) {
        this.connection = connection;
        expression = new StringBuilder(String.format(fragment, args));
    }

    public QueryBuilder value(Object value) {
        expression.append(" ");
        if (value instanceof String || Enum.class.isAssignableFrom(value.getClass())) {
            expression.append("'");
            expression.append(value);
            expression.append("'");
        } else if (value instanceof Date) {
            expression.append("date '");
            expression.append(value);
            expression.append("'");
        } else {
            expression.append(value);
        }
        return this;
    }

    public QueryBuilder values(Object... values) {
        expression.append(" VALUES (");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) expression.append(",");
            value(values[i]);
        }
        expression.append(")");
        return this;
    }

    public ResultSet andExecute() throws SQLException {
        return connection.prepareStatement(expression.toString()).executeQuery();
    }

    public int andUpdate() throws SQLException {
        return connection.prepareStatement(expression.toString()).executeUpdate();
    }

    public boolean andTryUpdate() throws SQLException {
        return andUpdate() > 0;
    }
}
