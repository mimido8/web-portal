package com.healthware.base;

import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.loader.ResourceLocator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public abstract class HTMLTemplateRoute extends Route {
    private static ResourceLocator templateFileLocator = (fullName, encoding, interpreter) -> {
        File importFile = new File("templates", fullName);
        File templateDirectory = new File("templates");
        if (!importFile.getAbsolutePath().startsWith(templateDirectory.getAbsolutePath()) || !importFile.isFile())
            throw new IOException("Template import path must be within template folder");
        return FileUtils.readFileToString(importFile, encoding);
    };
    private static Jinjava templateEngine = new Jinjava(new JinjavaConfig()) {{
        setResourceLocator(templateFileLocator);
    }};

    private String template;
    protected boolean errorOnMissingTemplates;

    protected HTMLTemplateRoute(String template, boolean errorOnMissingTemplates) {
        this.template = template;
        this.errorOnMissingTemplates = errorOnMissingTemplates;
    }

    protected HTMLTemplateRoute(String template) {
        this.template = template;
        this.errorOnMissingTemplates = true;
    }

    protected abstract Map<String, Object> render(HTTPRequest request) throws Exception;

    @Override
    protected HTTPResponse<String> handle(HTTPRequest r) throws Exception {
        Map<String, Object> context = render(r);
        try {
            return new HTTPResponse<>(200, templateEngine.render(templateFileLocator.getString(template, Charset.forName("UTF-8"), null), context), "text/html");
        } catch (IOException ex) {
            if (errorOnMissingTemplates) throw ex;
            else return HTTPResponse.status(404);
        }
    }

    public static HTMLTemplateRoute withoutContext(String template) {
        return new HTMLTemplateRoute(template, false) {
            @Override
            protected Map<String, Object> render(HTTPRequest request) {
                return null;
            }
        };
    }
}
