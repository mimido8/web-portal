package com.healthware.base.http;

import spark.Request;

public class HTTPRequest {
    public static final String SESSION_COOKIE_NAME = "t5session";

    public final String body;
    public final String sessionToken;

    public HTTPRequest(String body, String sessionToken) {
        this.body = body;
        this.sessionToken = sessionToken;
    }

    public HTTPRequest(Request request) {
        this.body = request.body();
        this.sessionToken = request.cookie(SESSION_COOKIE_NAME);
    }
}
