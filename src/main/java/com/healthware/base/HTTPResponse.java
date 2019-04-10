package com.healthware.base;

import spark.Response;

public class HTTPResponse<T> {
    public final int status;
    public final T content;
    public final String contentType;
    public final String redirect;

    public HTTPResponse(int status, T content, String contentType, String redirect) {
        this.status = status;
        this.content = content;
        this.contentType = contentType;
        this.redirect = redirect;
    }

    public HTTPResponse(int status, T content, String contentType) {
        this(status, content, contentType, null);
    }

    public HTTPResponse(String redirect) {
        this(302, null, null, redirect);
    }

    public String apply(Response response) {
        if (contentType != null) response.type(contentType);
        if (redirect == null) response.status(status);
        else response.redirect(redirect);
        return content == null ? "" : content.toString();
    }

    public static HTTPResponse<String> status(int status) {
        return new HTTPResponse<>(status, null, null);
    }
}
