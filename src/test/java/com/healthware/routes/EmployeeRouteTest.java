package com.healthware.routes;

import com.healthware.Configuration;
import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.sql.Database;
import com.healthware.base.sql.Table;
import com.healthware.models.Account;
import com.healthware.models.Employee;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class EmployeeRouteTest {
    private Table<Account> accounts = null;
    private Account testAccount = null;
    private Table<Employee> employees = null;
    private Employee testEmployee = null;

    @Before
    public void before() throws Exception {
        //Set up test database connection
        Configuration configuration = Configuration.load();
        Database database = Database.connect(configuration.testDatabaseURL, configuration.databaseUsername, configuration.databasePassword);
        accounts = database.getTable("accounts", Account.class);
        employees = database.getTable("employees", Employee.class);

        //Create an account
        testAccount = accounts.insert(new Account("BobbyJackson", "bobbyjackson@email.com", "akfhajhf", Account.Type.EMPLOYEE));

        //Create an employee
        testEmployee = employees.insert(new Employee(testAccount.id, "Bobby Jackson", "000-000-0000"));  //Employee(long, String, String)
    }

    @After
    public void after() throws Exception {
        testEmployee.delete();
    }

    @Test
    public void returnsEmployee() throws Exception {
        EmployeeRoute route = new EmployeeRoute(Collections.emptyMap(), accounts, employees);
        HTTPResponse<List<Employee>> response = route.getObject(new HTTPRequest(null, null));

        assertNotNull(response);
        assertNotNull(response.content);
        assertTrue(response.content.size() > 0);
    }
}