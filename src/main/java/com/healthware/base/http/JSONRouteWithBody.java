package com.healthware.base.http;

import com.healthware.Utilities;

public abstract class JSONRouteWithBody<T> extends RouteWithJSONBody<T> {
    protected abstract HTTPResponse respond(T body, HTTPRequest request) throws Exception;

    @Override
    protected HTTPResponse handle(T body, HTTPRequest request) throws Exception {
        HTTPResponse response = respond(body, request);
        return new HTTPResponse<>(response.status, Utilities.serializeJSON(response.content), "application/json");
    }
}
