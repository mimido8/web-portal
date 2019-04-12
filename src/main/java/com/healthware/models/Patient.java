package com.healthware.models;

import java.sql.Date;

public class Patient {
    //create, getByAccountID

    public long id;
    public long accountID;
    public String name;
    public Date birthDate;
    public String phone;
    public Date signupDate;
    public long planID;

    private Patient(long id, long accountID, String name, Date birthDate, String phone, Date signupDate, long planID) {
        this.id = id;
        this.accountID = accountID;
        this.name = name;
        this.birthDate = birthDate;
        this.phone = phone;
        this.signupDate = signupDate;
        this.planID = planID;
    }
}
