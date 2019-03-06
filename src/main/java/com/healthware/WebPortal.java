package com.healthware;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.healthware.models.Account;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.loader.ResourceLocator;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class WebPortal {

    private static Logger logger = LoggerFactory.getLogger(WebPortal.class);
    private static Configuration configuration;
    private static ComboPooledDataSource databaseConnectionPool;
    private static Jinjava templateEngine = new Jinjava(new JinjavaConfig());

    public static ResultSet executeQuery(String... query) throws SQLException {
        Connection connection = databaseConnectionPool.getConnection();
        PreparedStatement statement = connection.prepareStatement(String.join("", query));
        return statement.executeQuery();
    }

    public static int executeUpdate(Object... query) throws SQLException {
        Connection connection = databaseConnectionPool.getConnection();
        PreparedStatement statement = connection.prepareStatement(String.join("", Arrays.stream(query).map(o -> o.toString()).collect(Collectors.toList())));
        return statement.executeUpdate();
    }

    public static void main(String[] args) {
        logger.info("Loading configuration");
        try {
            configuration = Configuration.load();
        } catch (Exception ex) {
            logger.error("Failed to load configuration due to " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            return;
        }

        logger.info("Configuring database connection pool");
        try {
            databaseConnectionPool = new ComboPooledDataSource();
            databaseConnectionPool.setDriverClass("org.postgresql.Driver");
            databaseConnectionPool.setJdbcUrl(configuration.databaseURL);
            databaseConnectionPool.setUser(configuration.databaseUsername);
            databaseConnectionPool.setPassword(configuration.databasePassword);
        } catch (Exception ex) {
            logger.error("Failed to load database connection pool due to " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            return;
        }

        ResourceLocator templateFileLocator = new ResourceLocator() {
            @Override
            public String getString(String fullName, Charset encoding, JinjavaInterpreter interpreter) throws IOException {
                File importFile = new File("templates", fullName);
                File templateDirectory = new File("templates");
                if (!importFile.getAbsolutePath().startsWith(templateDirectory.getAbsolutePath()) || !importFile.isFile()) throw new IOException("Template import path must be within template folder");
                return FileUtils.readFileToString(importFile, encoding);
            }
        };

        templateEngine.setResourceLocator(templateFileLocator);

        logger.info("Configuring HTTP server");
        Spark.port(8080);
        Spark.externalStaticFileLocation("public");

        Authorization.initializeRoutes();

        Spark.get("/patient-dashboard", (request, response) -> {
            try {
                Map<String, Object> context = new HashMap<>();
                context.put("id", request.cookie("t5hsession"));
                logger.info(request.cookie("t5hsession"));
                return templateEngine.render(templateFileLocator.getString("patient-dashboard.html", Charset.forName("UTF-8"), null), context);
            } catch (IOException ex) {
                logger.error("Failed to render dashboard", ex);
                return null;
            }
        });

        Spark.get("/:view", (request, response) -> {
            try {
                return templateEngine.render(templateFileLocator.getString(request.params("view") + ".html", Charset.forName("UTF-8"), null), null);
            } catch (IOException ex) {
                return null;
            }
        });
    }
}
