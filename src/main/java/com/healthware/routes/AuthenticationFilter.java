package com.healthware.routes;

import com.healthware.Session;
import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.http.Route;

import java.util.Map;

import static com.healthware.base.http.HTTPResponse.status;

public class AuthenticationFilter extends Route {
    private static final long SESSION_TIMEOUT_MS = 360000;

    private Map<String, Session> sessions;

    public AuthenticationFilter(Map<String, Session> sessions) {
        this.sessions = sessions;
    }

    @Override
    protected HTTPResponse<String> getResponse(HTTPRequest request) {
        String token = request.sessionToken;
        if (token == null || !sessions.containsKey(token)) return status(403);
        Session session = sessions.get(token);
        if (System.currentTimeMillis() > session.created + SESSION_TIMEOUT_MS) {
            sessions.remove(token);
            return status(403);
        }
        return null;
    }
}
