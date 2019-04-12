package com.healthware.base.http;

import com.healthware.Utilities;
import spark.Request;
import spark.Response;

import static com.healthware.base.http.HTTPResponse.status;

public abstract class RouteWithJSONBody<T> extends Route {
    private Class<T> bodyType;

    protected RouteWithJSONBody(Class<T> bodyType) {
        this.bodyType = bodyType;
    }

    protected abstract HTTPResponse<String> getResponse(T body, HTTPRequest request) throws Exception;

    @Override
    protected HTTPResponse<String> getResponse(HTTPRequest request) throws Exception {
        T body;
        try {
            body = Utilities.deserializeJSON(request.body, bodyType);
        } catch (Exception ex) {
            return status(400);
        }
        return getResponse(body, request);
    }
}
