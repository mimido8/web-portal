package com.healthware.routes;

import com.healthware.Session;
import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.http.Route;
import com.healthware.models.Account;

import java.util.Map;

import static com.healthware.base.http.HTTPResponse.status;

public class EmployeeAuthenticationFilter extends Route {
    private Map<String, Session> sessions;

    public EmployeeAuthenticationFilter(Map<String, Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    protected HTTPResponse<String> getResponse(HTTPRequest request) throws Exception {
        if (sessions.get(request.sessionToken).type != Account.Type.EMPLOYEE) return status(403);
        return null;
    }
}
