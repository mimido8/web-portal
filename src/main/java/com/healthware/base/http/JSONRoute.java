package com.healthware.base.http;

import com.healthware.Utilities;

public abstract class JSONRoute extends Route {
    protected abstract HTTPResponse respond(HTTPRequest request) throws Exception;

    @Override
    protected HTTPResponse<String> handle(HTTPRequest request) throws Exception {
        HTTPResponse response = respond(request);
        return new HTTPResponse<>(response.status, Utilities.serializeJSON(response.content), "application/json");
    }
}
