package com.healthware;

import com.healthware.messages.AuthorizeRequestBody;
import com.healthware.messages.CreatePatientAccountRequestBody;
import com.healthware.models.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;
import spark.utils.Assert;

import java.util.HashMap;
import java.util.Map;

public class Authorization {

    private static final String SESSION_COOKIE = "t5hsession";
    private static Logger logger = LoggerFactory.getLogger(Authorization.class);
    private static Map<String, Account> sessions = new HashMap<>();

    public static void initializeRoutes() {
        Spark.post("/api/authorize", (request, response) -> {
            AuthorizeRequestBody body;
            try {
                body = Utilities.deserializeJSON(request.body(), AuthorizeRequestBody.class);
            } catch (Exception ex) {
                logger.error("Utilities.deserializeJSON failed", ex);
                response.status(400);
                return "";
            }

            Account account;
            try {
                account = Account.getByUsername(body.username);
            }  catch (Exception ex) {
                logger.error("Account.getByUsername failed", ex);
                response.status(500);
                return "";
            }

            if (account == null || !Utilities.getSHA(body.password).equals(account.passwordHash)) {
                response.status(403);
            } else {
                String sessionID = Long.toHexString(Utilities.getNextUID());
                sessions.put(sessionID, account);
                logger.info(sessionID);
                response.cookie("/", SESSION_COOKIE, sessionID, 3600, false, true);
            }
            return "";
        });

        Spark.post("/api/create-patient-account", (request, response) -> {
            CreatePatientAccountRequestBody body;
            try {
                body = Utilities.deserializeJSON(request.body(), CreatePatientAccountRequestBody.class);
                Assert.isTrue(body.username != null && body.username.length() >= 4, "Username meets requirements");
                Assert.isTrue(body.password != null && body.password.length() >= 8, "Password meets requirements");
                Assert.isTrue(body.email != null && body.email.length() >= 5, "Email meets requirements");
            } catch (Exception ex) {
                logger.error("Utilities.deserializeJSON failed", ex);
                response.status(400);
                return "";
            }

            try {
                if (!Account.create(body.username, body.email, Utilities.getSHA(body.password), Account.Type.PATIENT)) {
                    throw new Exception("Failed to insert row");
                }
            }  catch (Exception ex) {
                logger.error("Account.create failed", ex);
                response.status(500);
            }

            return "";
        });
    }
}
