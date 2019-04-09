package com.healthware.models;

public class Employee {
    //create?

    public long id;
    public long accountID;
    public String name;
    public String phone;

    private Employee(long id, long accountID, String name, String phone) {
        this.id = id;
        this.accountID = accountID;
        this.name = name;
        this.phone = phone;
    }
}
