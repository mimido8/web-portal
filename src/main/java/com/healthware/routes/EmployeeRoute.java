package com.healthware.routes;

import com.healthware.Session;
import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.http.JSONRoute;
import com.healthware.base.sql.Table;
import com.healthware.models.Account;
import com.healthware.models.Employee;
import com.healthware.messages.EmployeeInfo;

import java.util.Map;

import static com.healthware.base.http.HTTPResponse.object;

public class EmployeeRoute extends JSONRoute {
    private Map<String, Session> sessions;
    private Table<Account> accounts;
    private Table<Employee> employees;


    public EmployeeRoute(Map<String, Session> sessions, Table<Account> accounts, Table<Employee> employees) {
        this.accounts = accounts;
        this.employees = employees;
    }

    @Override
    protected HTTPResponse getObject(HTTPRequest request) throws Exception {
        Session session = sessions.get(request.sessionToken);
        return object(new EmployeeInfo(accounts.selectFirstWhere("id", session.accountID),
            employees.selectFirstWhere("account_id", session.accountID),
            null, null, null)); //Claims, Plans, and Providers need filled
    }
}
