package com.healthware;

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

public class WebPortal {

    private static Logger logger = LoggerFactory.getLogger(WebPortal.class);
    private static Configuration configuration;
    private static ComboPooledDataSource connectionPool;
    private static Jinjava templateEngine = new Jinjava(new JinjavaConfig());

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
            connectionPool = new ComboPooledDataSource();
            connectionPool.setDriverClass("org.postgresql.Driver");
            connectionPool.setJdbcUrl(configuration.databaseURL);
            connectionPool.setUser(configuration.databaseUsername);
            connectionPool.setPassword(configuration.databasePassword);
        } catch (Exception ex) {
            logger.error("Failed to load database connection pool due to " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            return;
        }

        ResourceLocator templateFileLocator = new ResourceLocator() {
            @Override
            public String getString(String fullName, Charset encoding, JinjavaInterpreter interpreter) throws IOException {
                File importFile = new File("templates", fullName);
                File templateDirectory = new File("templates");
                if (!importFile.getAbsolutePath().startsWith(templateDirectory.getAbsolutePath())) {
                    throw new IOException("Template import path is within template folder");
                }
                return FileUtils.readFileToString(importFile, encoding);
            }
        };

        templateEngine.setResourceLocator(templateFileLocator);

        logger.info("Configuring HTTP server");
        Spark.port(8080);
        Spark.externalStaticFileLocation("public");

        Spark.get("/:view", (request, response) -> {
            return templateEngine.render(templateFileLocator.getString(request.params("view") + ".html", Charset.forName("UTF-8"), null), null);
        });
    }
}
