package com.healthware;

import com.healthware.base.sql.Database;
import com.healthware.base.http.HTMLTemplateRoute;
import com.healthware.messages.PatientAccountCreationBody;
import com.healthware.routes.AuthenticationFilter;
import com.healthware.routes.PatientAccountCreationRoute;
import org.slf4j.Logger;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;
import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.port;

public class WebPortal {

    public static void main(String[] args) {
        Logger logger = getLogger(WebPortal.class);

        logger.info("Loading configuration");
        Configuration configuration;
        try {
            configuration = Configuration.load();
        } catch (Exception ex) {
            logger.error("Failed to load configuration", ex);
            return;
        }

        logger.info("Configuring database connection pool");
        Database database;
        try {
            database = Database.connect(configuration.databaseURL, configuration.databaseUsername, configuration.databasePassword);
        } catch (Exception ex) {
            logger.error("Failed to load database connection pool", ex);
            return;
        }

        logger.info("Configuring HTTP server");
        port(8080);
        externalStaticFileLocation("public");

        Map<String, Long> sessions = new HashMap<>();

        AuthenticationFilter authFilter = new AuthenticationFilter(sessions);
        asList("/patient/:view", "/admin/:view").forEach(path ->
            Spark.before(path, authFilter::handle));

        asList("/:view", "/patient/:view", "/admin/:view").forEach(path ->
            Spark.get(path, (request, response) ->
                HTMLTemplateRoute.withoutContext(request.params("view") + ".html").handle(request, response)));

        PatientAccountCreationRoute patientAccountCreationRoute = new PatientAccountCreationRoute();
        Spark.post("/create-patient-account", (request, response) ->
            patientAccountCreationRoute.handle(request, response, PatientAccountCreationBody.class));

        /*Spark.get("/patient-dashboard", (request, response) -> {
            try {
                Map<String, Object> context = new HashMap<>();
                context.put("id", request.cookie("t5hsession"));
                logger.info(request.cookie("t5hsession"));
                return templateEngine.render(templateFileLocator.getString("patient-dashboard.html", Charset.forName("UTF-8"), null), context);
            } catch (IOException ex) {
                logger.error("Failed to render dashboard", ex);
                return null;
            }
        });*/
    }
}
