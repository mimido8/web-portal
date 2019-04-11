package com.healthware.base.http;

import spark.Request;
import spark.Response;

public abstract class Route {
    protected abstract HTTPResponse<String> handle(HTTPRequest request) throws Exception;

    public String handle(Request q, Response r) {
        HTTPResponse response;
        try {
            response = handle(new HTTPRequest(q));
        } catch (Exception ex) {
            return HTTPResponse.status(500).apply(r);
        }
        return response.apply(r);
    }
}
