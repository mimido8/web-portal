package com.healthware.routes;

import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.http.Route;

import java.util.Map;

public class AuthenticationFilter extends Route {
    private Map<String, Long> sessions;

    public AuthenticationFilter(Map<String, Long> sessions) {
        this.sessions = sessions;
    }

    @Override
    protected HTTPResponse<String> handle(HTTPRequest request) {
        return null;
    }
}
