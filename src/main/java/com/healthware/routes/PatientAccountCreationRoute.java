package com.healthware.routes;

import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.http.RouteWithJSONBody;
import com.healthware.messages.PatientAccountCreationBody;
import spark.utils.Assert;

import static com.healthware.base.http.HTTPResponse.status;

public class PatientAccountCreationRoute extends RouteWithJSONBody<PatientAccountCreationBody> {
    public PatientAccountCreationRoute() {
        super(PatientAccountCreationBody.class);
    }

    @Override
    protected HTTPResponse<String> getResponse(PatientAccountCreationBody body, HTTPRequest request) throws Exception {
        try {
            Assert.isTrue(body.username != null && body.username.length() >= 4, "Username meets requirements");
            Assert.isTrue(body.password != null && body.password.length() >= 8, "Password meets requirements");
            Assert.isTrue(body.email != null && body.email.length() >= 5, "Email meets requirements");
        } catch (Exception ex) {
            return status(400);
        }

        //if (!Account.create(body.username, body.email, Utilities.getSHA(body.password), Account.Type.PATIENT))
        //   throw new Exception("Failed to insert row");

        return status(200);
    }
}
