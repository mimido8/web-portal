package com.healthware.routes;

import com.healthware.Utilities;
import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.http.RouteWithJSONBody;
import com.healthware.base.sql.Table;
import com.healthware.messages.PatientAccountCreationBody;
import com.healthware.models.Account;

import static com.healthware.base.http.HTTPResponse.redirect;
import static com.healthware.base.http.HTTPResponse.status;

public class PatientAccountCreationRoute extends RouteWithJSONBody<PatientAccountCreationBody> {
    private Table<Account> accounts;

    public PatientAccountCreationRoute(Table<Account> accounts) {
        super(PatientAccountCreationBody.class);
        this.accounts = accounts;
    }

    @Override
    protected HTTPResponse<String> getResponse(PatientAccountCreationBody body, HTTPRequest request) throws Exception {
        if (body.username == null
                || body.email == null
                || body.password == null
                || accounts.countWhere("username", body.username) > 0)
            return status(400);
        accounts.insert(new Account(body.username, body.email, Utilities.getSHA(body.password), Account.Type.PATIENT));
        return redirect("/account-creation-succeeded");
    }
}
