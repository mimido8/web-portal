package com.healthware.models;

import com.healthware.Utilities;
import com.healthware.WebPortal;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public static boolean create(long accountID, String name, Date birthDate, String phone, Date signupDate, long planID) throws Exception {
        return WebPortal.buildQuery("INSERT INTO patients (id, account_id, name, birth_date, phone, signup_date, plan_id)")
                .values(Utilities.getNextUID(), accountID, name, birthDate, phone, signupDate, planID)
                .andTryUpdate();
    }

    public static Patient getByAccountID(long accountID) throws SQLException {
        ResultSet results = WebPortal.buildQuery("SELECT * FROM patients WHERE account_id = %1", accountID).andExecute();
        if (!results.next()) return null;
        else return new Patient(
                results.getLong("id"),
                results.getLong("account_id"),
                results.getString("name"),
                results.getDate("birth_date"),
                results.getString("phone"),
                results.getDate("signup_date"),
                results.getLong("plan_id"));
    }
}
