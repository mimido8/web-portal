package com.healthware.base;

import com.healthware.Utilities;
import spark.Request;
import spark.Response;

public abstract class RouteWithJSONBody<T> {
    protected abstract HTTPResponse handle(T body, HTTPRequest request) throws Exception;

    public String handle(Request q, Response r, Class<T> bodyType) {
        T body;
        try {
            body = Utilities.deserializeJSON(q.body(), bodyType);
        } catch (Exception ex) {
            return HTTPResponse.status(400).apply(r);
        }

        HTTPResponse response;
        try {
            response = handle(body, new HTTPRequest(q));
        } catch (Exception ex) {
            return HTTPResponse.status(500).apply(r);
        }
        return response.apply(r);
    }
}
