package com.healthware;

import org.apache.commons.io.FileUtils;
import spark.utils.Assert;

import java.io.File;
import java.nio.charset.Charset;

public class Configuration {
    public String databaseURL = null;
    public String databaseUsername = null;
    public String databasePassword = null;
    public String testDatabaseURL = null;

    public static Configuration load() throws Exception {
        Configuration configuration = Utilities.deserializeJSON(
                FileUtils.readFileToString(new File("config.json"), Charset.forName("UTF-8")),
                Configuration.class);
        Assert.notNull(configuration.databaseURL);
        Assert.notNull(configuration.databaseUsername);
        Assert.notNull(configuration.databasePassword);
        return configuration;
    }
}
