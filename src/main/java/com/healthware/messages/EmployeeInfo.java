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
    public Plans[] plans = null;
    public Providers[] providers = null;
}
