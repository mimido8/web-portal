package com.healthware;

import com.healthware.models.Account;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;

import java.sql.SQLException;

import static org.slf4j.LoggerFactory.getLogger;
import static spark.Spark.port;

public class WebPortal {
    private static ComboPooledDataSource databaseConnectionPool;

    public static QueryBuilder buildQuery(String fragment, Object... args) throws SQLException {
        return new QueryBuilder(databaseConnectionPool.getConnection(), fragment, args);
    }

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
        try {
            databaseConnectionPool = new ComboPooledDataSource();
            databaseConnectionPool.setDriverClass("org.postgresql.Driver");
            databaseConnectionPool.setJdbcUrl(configuration.databaseURL);
            databaseConnectionPool.setUser(configuration.databaseUsername);
            databaseConnectionPool.setPassword(configuration.databasePassword);
        } catch (Exception ex) {
            logger.error("Failed to load database connection pool", ex);
            return;
        }

        try {
           Account a = SQLRow.construct(Account.class, WebPortal.buildQuery("SELECT * FROM accounts WHERE username = 'lukas'").andExecute());
           logger.info(a.username);
        } catch (Exception ex) {
            logger.error("Mapper failed", ex);
        }

        return;

        /*logger.info("Configuring HTTP server");
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
            patientAccountCreationRoute.handle(request, response, PatientAccountCreationBody.class));*/

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
