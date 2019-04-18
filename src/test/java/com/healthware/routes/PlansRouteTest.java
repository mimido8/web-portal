package com.healthware.routes;

import com.healthware.Configuration;
import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.sql.Database;
import com.healthware.base.sql.Table;
import com.healthware.models.Plan;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class PlansRouteTest {
    private Table<Plan> plans = null;
    private Plan testPlan = null;

    @Before
    public void before() throws Exception {
        //Set up test database connection
        Configuration configuration = Configuration.load();
        Database database = Database.connect(configuration.testDatabaseURL, configuration.databaseUsername, configuration.databasePassword);
        plans = database.getTable("plans", Plan.class);

        //Create a plan
        testPlan = plans.insert(new Plan("Big Plan", new BigDecimal(0), new BigDecimal(0), new BigDecimal(0)));
    }

    @After
    public void after() throws Exception {
        testPlan.delete();
    }

    @Test
    public void returnsPlans() throws Exception {
        PlansRoute route = new PlansRoute(plans);
        HTTPResponse<List<Plan>> response = route.getObject(new HTTPRequest(null, null));

        assertNotNull(response);
        assertNotNull(response.content);
        assertTrue(response.content.size() > 0);
    }
}