package com.healthware;

import com.healthware.base.http.*;
import com.healthware.base.sql.Database;
import com.healthware.messages.AuthorizationBody;
import com.healthware.messages.PatientAccountCreationBody;
import com.healthware.models.Account;
import com.healthware.routes.*;
import org.slf4j.Logger;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.healthware.base.http.HTTPResponse.status;
import static org.slf4j.LoggerFactory.getLogger;
import static spark.Spark.*;

public class WebPortal {

    @FunctionalInterface
    interface RouteRegistrationMethod {
        void bind(String path, spark.Route route);
    }

    private static void route(RouteRegistrationMethod method, Route route, String... paths) {
        Arrays.stream(paths).forEach(path -> method.bind(path, (request, response) -> route.handle(new HTTPRequest(request)).apply(response)));
    }

    @FunctionalInterface
    interface RouteSupplier {
        Route get(Request request, Response response);
    }

    private static void route(RouteRegistrationMethod method, RouteSupplier supplier, String... paths) {
        Arrays.stream(paths).forEach(path -> method.bind(path, (request, response) -> {
            Route route = supplier.get(request, response);
            if (route != null) return route.handle(new HTTPRequest(request)).apply(response);
            else return status(404).apply(response);
        }));
    }

    @FunctionalInterface
    interface FilterRegistrationMethod {
        void bind(String path, spark.Filter route);
    }

    private static void filter(FilterRegistrationMethod method, Route route, String... paths) {
        Arrays.stream(paths).forEach(path -> method.bind(path, (request, response) -> {
            HTTPResponse handlerResponse = route.handle(new HTTPRequest(request));
            if (handlerResponse != null) halt(handlerResponse.status, handlerResponse.content != null ? handlerResponse.content.toString() : "");
        }));
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

        logger.info("Configuring database");
        Database database;
        try {
            database = Database.connect(configuration.databaseURL, configuration.databaseUsername, configuration.databasePassword);
        } catch (Exception ex) {
            logger.error("Failed to configure database", ex);
            return;
        }

        Map<String, Session> sessions = new HashMap<>();

        logger.info("Configuring HTTP server");
        port(8080);
        externalStaticFileLocation("public");
        route(Spark::get, (request, response) -> HTMLTemplateRoute.withoutContext(request.params("view") + ".html"), ":/view");
        route(Spark::post, new AuthorizationRoute(database.getTable("accounts", Account.class), sessions), "/authorize");
        filter(Spark::before, new AuthenticationFilter(sessions), "/patient/:view", "/employee/:view");
        filter(Spark::before, new PatientAuthenticationFilter(sessions), "/patient/:view");
        filter(Spark::before, new EmployeeAuthenticationFilter(sessions), "/patient/:view");
    }
}
