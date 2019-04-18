package com.healthware.routes;

import com.healthware.Configuration;
import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.sql.Database;
import com.healthware.base.sql.Table;
import com.healthware.models.Plan;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class PlansRouteTest {
    private Table<Plan> plans = null;

    @Before
    public void before() throws Exception {
        Configuration configuration = Configuration.load();
        Database database = Database.connect(configuration.testDatabaseURL, configuration.databaseUsername, configuration.databasePassword);
        plans = database.getTable("plans", Plan.class);
    }

    @Test
    public void passes() throws Exception {
        plans.insert(new Plan("Big Plan", new BigDecimal(0), new BigDecimal(0), new BigDecimal(0)));
        HTTPResponse<List<Plan>> response = new PlansRoute(plans, Collections.emptyMap()).getObject(new HTTPRequest(null, null));
        assertNotNull(response.content);
        assertTrue(response.content.size() > 0);
        plans.delete(Collections.singletonMap("name", "Big Plan"));
    }
}