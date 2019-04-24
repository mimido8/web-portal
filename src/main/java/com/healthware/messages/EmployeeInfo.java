package com.healthware.messages;

import com.healthware.models.Account;
import com.healthware.models.Employee;
import com.healthware.models.Claim;
import com.healthware.models.Plan;
import com.healthware.models.Provider;

public class EmployeeInfo {
    public Account account = null;
    public Employee employee = null;
    public Claim[] claims = null;
    public Plan[] plans = null;
    public Provider[] providers = null;

    public EmployeeInfo(Account account, Employee employee, Claim[] claims, Plan[] plans, Provider[] providers) {
        this.account = account;
        this.employee = employee;
        this.claims = claims;
        this.plans = plans;
        this.providers = providers;
    }
}
