package com.healthware;

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
import java.sql.ResultSet;
import java.sql.SQLException;

public class WebPortal {

    private static Logger logger = LoggerFactory.getLogger(WebPortal.class);
    private static Configuration configuration;
    private static ComboPooledDataSource databaseConnectionPool;
    private static Jinjava templateEngine = new Jinjava(new JinjavaConfig());
    private static long lastTime = 0;
    private static long nextID = 0;

    public static ComboPooledDataSource getDatabaseConnectionPool() {
        return databaseConnectionPool;
    }

    public static long getNextID() {
        long time = System.currentTimeMillis();
        if (time != lastTime) {
            nextID = 0;
            return (lastTime = time) * 1000;
        } else {
            return (lastTime * 1000) + nextID++;
        }
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

        Spark.get("/test", (request, response) -> {
            try {
                logger.info("Do requets");
                logger.info(Boolean.toString(Account.create("quick", "jimmy@email.com", "I have password", Account.Type.PATIENT)));
            } catch (Throwable ex) {
                logger.error("Failed to create account due to " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
            return "";
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
