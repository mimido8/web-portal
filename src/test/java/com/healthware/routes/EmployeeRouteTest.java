package com.healthware.routes;

import com.healthware.Configuration;
import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.sql.Database;
import com.healthware.base.sql.Table;
import com.healthware.models.Employee;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class EmployeeRouteTest {
    private Table<Employee> employee = null;
    private Employee testEmployee = null;

    @Before
    public void before() throws Exception {
        //Set up test database connection
        Configuration configuration = Configuration.load();
        Database database = Database.connect(configuration.testDatabaseURL, configuration.databaseUsername, configuration.databasePassword);
        employee = database.getTable("employee", Employee.class);

        //Create a plan
        testEmployee = employee.insert(new Employee(456L, "Bobby Jackson", "1234567890"));  //Employee(long, String, String)
    }

    @After
    public void after() throws Exception {
        testEmployee.delete();
    }

    @Test
    public void returnsEmployee() throws Exception {
        EmployeeRoute route = new EmployeeRoute(employee);
        HTTPResponse<List<Employee>> response = route.getObject(new HTTPRequest(null, null));

        assertNotNull(response);
        assertNotNull(response.content);
        assertTrue(response.content.size() > 0);
    }
}