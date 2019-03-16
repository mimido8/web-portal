package com.healthware.controllers;

import com.healthware.WebPortal;
import com.healthware.annotations.ControllerFactory;
import com.healthware.annotations.GET;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.nio.charset.Charset;

public class ViewController {

    @GET("/:view")
    public String getView(Request request, Response resposne) {
        try {
            return WebPortal.templateEngine.render(WebPortal.templateFileLocator.getString(request.params("view") + ".html", Charset.forName("UTF-8"), null), null);
        } catch (IOException ex) {
            return null;
        }
    }

    @ControllerFactory
    public static ViewController create() {
        return new ViewController();
    }
}
