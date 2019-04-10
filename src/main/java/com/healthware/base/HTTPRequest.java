package com.healthware.base;

import spark.Request;

public class HTTPRequest {
    public final Request request;

    public HTTPRequest(Request request) {
        this.request = request;
    }
}
