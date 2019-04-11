package com.healthware.base.http;

import spark.Request;

public class HTTPRequest {
    public final Request request;

    public HTTPRequest(Request request) {
        this.request = request;
    }
}
