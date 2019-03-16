package com.healthware.testing;

import com.healthware.Configuration;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TestDatabase {

    private static Configuration configuration = null;
    private static ComboPooledDataSource databaseConnectionPool;

    private static Connection getConnection() throws Exception {
        if (configuration == null) {
            try {
                configuration = Configuration.load();
                databaseConnectionPool = new ComboPooledDataSource();
                databaseConnectionPool.setDriverClass("org.postgresql.Driver");
                databaseConnectionPool.setJdbcUrl(configuration.databaseURL);
                databaseConnectionPool.setUser(configuration.databaseUsername);
                databaseConnectionPool.setPassword(configuration.databasePassword);
            } catch (Exception ex) {
                throw new Exception("Failed to load development database. Verify that your config file is correct.", ex);
            }
        }
        return databaseConnectionPool.getConnection();
    }

    public static ResultSet executeQuery(Object... query) throws Exception {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(String.join("", Arrays.stream(query).map(o -> o.toString()).collect(Collectors.toList())));
        return statement.executeQuery();
    }

    public static int executeUpdate(Object... query) throws Exception {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(String.join("", Arrays.stream(query).map(o -> o.toString()).collect(Collectors.toList())));
        return statement.executeUpdate();
    }
}
