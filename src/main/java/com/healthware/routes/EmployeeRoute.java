package com.healthware.routes;

import com.healthware.base.http.HTTPRequest;
import com.healthware.base.http.HTTPResponse;
import com.healthware.base.http.JSONRoute;
import com.healthware.base.sql.Table;
import com.healthware.models.Account;
import com.healthware.models.Employee;
import com.healthware.messages.EmployeeInfo;

import static com.healthware.base.http.HTTPResponse.object;

public class EmployeeRoute extends JSONRoute {
    private Table<Account> account;
    private Table<Employee> employee;


    public EmployeeRoute(Table<Employee> employee) {
        super(EmployeeInfo.class);
        this.employee = employee;   //necessary?
        this.account = account;
    }

    @Override
    protected HTTPResponse getObject(HTTPRequest request) throws Exception {
        return object();
    }
}
