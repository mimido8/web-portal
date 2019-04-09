package com.healthware;

import com.healthware.annotations.GET;
import com.healthware.annotations.ControllerFactory;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.loader.ResourceLocator;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
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
    public static Jinjava templateEngine = new Jinjava(new JinjavaConfig());
    public static ResourceLocator templateFileLocator;

    public static QueryBuilder buildQuery(String fragment, Object... args) throws SQLException {
        return new QueryBuilder(databaseConnectionPool.getConnection(), fragment, args);
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

        templateFileLocator = new ResourceLocator() {
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

        Reflections reflections = new Reflections("com.healthware.controllers", new MethodAnnotationsScanner());

        logger.info("Loading routers");
        Map<Class<?>, Object> controllers = new HashMap<>();
        for (Method controllerFactory : reflections.getMethodsAnnotatedWith(ControllerFactory.class)) {
            try {
                controllers.put(controllerFactory.getDeclaringClass(), controllerFactory.invoke(null));
            } catch (Exception ex) {
                logger.error("Failed to create controller " + controllerFactory.getDeclaringClass().getSimpleName(), ex);
            }
        }

        logger.info("Registering GET routes");
        for (Method getRoute : reflections.getMethodsAnnotatedWith(GET.class)) {
            try {
                Spark.get(getRoute.getDeclaredAnnotation(GET.class).value(), (request, response) -> getRoute.invoke(controllers.get(getRoute.getDeclaringClass()), request, response));
            } catch (Exception ex) {
                logger.error("Failed to create route from " + getRoute.getName(), ex);
            }
        }
    }
}
