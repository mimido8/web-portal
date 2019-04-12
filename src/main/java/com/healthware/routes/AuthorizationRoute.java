package com.healthware.routes;

import com.healthware.Session;
import com.healthware.Utilities;
import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.http.RouteWithJSONBody;
import com.healthware.base.sql.Table;
import com.healthware.messages.AuthorizationBody;
import com.healthware.models.Account;

import java.util.Map;

import static com.healthware.base.http.HTTPResponse.redirect;
import static com.healthware.base.http.HTTPResponse.status;

public class AuthorizationRoute extends RouteWithJSONBody<AuthorizationBody> {
    private Table<Account> accounts;
    private Map<String, Session> sessions;

    public AuthorizationRoute(Table<Account> accounts, Map<String, Session> sessions) {
        super(AuthorizationBody.class);
        this.accounts = accounts;
        this.sessions = sessions;
    }

    @Override
    protected HTTPResponse<String> getResponse(AuthorizationBody body, HTTPRequest request) throws Exception {
        if (body.username == null) return status(400);
        if (body.password == null) return status(400);
        Account account;
        try {
            account = accounts.selectFirstWhere("username", body.username);
        } catch (Exception ex) {
            return status(403);
        }
        if (!Utilities.getSHA(body.password).equals(account.passwordHash)) return status(403);
        String token = Long.toHexString(Utilities.getNextUID());
        sessions.put(token, new Session(System.currentTimeMillis(), account.id, account.type));
        return redirect("/patient/dashboard").withSession(token);
    }
}
