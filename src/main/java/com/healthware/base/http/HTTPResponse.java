package com.healthware.base.http;

import spark.Response;

import java.util.HashMap;
import java.util.Map;

public class HTTPResponse<T> {
    public final int status;
    public final T content;
    public final String contentType;
    public final String redirect;
    public final Map<String, String> cookies = new HashMap<>();

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
        for (Map.Entry<String, String> cookie : cookies.entrySet())
            response.cookie(cookie.getKey(), cookie.getValue());
        if (redirect == null) response.status(status);
        else response.redirect(redirect);
        return content == null ? "" : content.toString();
    }

    public HTTPResponse<T> withSession(String value) {
        cookies.put(HTTPRequest.SESSION_COOKIE_NAME, value);
        return this;
    }

    @Override
    public String toString() {
        return "HTTPResponse " + status + ", " + (contentType == null ? "no Content-Type" : contentType);
    }

    public static HTTPResponse<String> status(int status) {
        return new HTTPResponse<>(status, null, null);
    }

    public static HTTPResponse<String> redirect(String url) {
        return new HTTPResponse<>(url);
    }
}
