package com.healthware.routes;

import com.healthware.Session;
import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.http.Route;
import com.healthware.models.Account;

import java.util.Map;

import static com.healthware.base.http.HTTPResponse.status;

public class PatientAuthenticationFilter extends Route {
    private Map<String, Session> sessions;

    public PatientAuthenticationFilter(Map<String, Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    protected HTTPResponse<String> getResponse(HTTPRequest request) throws Exception {
        if (sessions.get(request.sessionToken).type != Account.Type.PATIENT) return status(403);
        return null;
    }
}
