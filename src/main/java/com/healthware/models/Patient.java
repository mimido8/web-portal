package com.healthware.models;

import com.healthware.Utilities;
import com.healthware.base.sql.Column;
import com.healthware.base.sql.PrimaryKey;
import com.healthware.base.sql.Row;

import java.sql.Date;

public class Patient extends Row {
    public @PrimaryKey @Column("id") long id;
    public @Column("account_id") long accountID;
    public @Column("name") String name;
    public @Column("birth_date") Date birthDate;
    public @Column("phone") String phone;
    public @Column("signup_date") Date signupDate;
    public @Column("plan_id") long planID;

    public Patient() { }

    private Patient(long accountID, String name, Date birthDate, String phone, Date signupDate, long planID) {
        this.id = Utilities.getNextUID();
        this.accountID = accountID;
        this.name = name;
        this.birthDate = birthDate;
        this.phone = phone;
        this.signupDate = signupDate;
        this.planID = planID;
    }
}
