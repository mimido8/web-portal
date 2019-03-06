package com.healthware.models;

import com.healthware.Utilities;
import com.healthware.WebPortal;

import java.sql.Date;

public class Patient {

    public static boolean create(long accountID, String name, Date birthDate, String phone, Date signupDate, long planID) throws Exception {
        int updated = WebPortal.executeUpdate(
                "INSERT INTO patients (id, account_id, name, birth_date, phone, signup_date, plan_id) VALUES (",
                Utilities.getNextUID(), ", ", accountID, ", '", name, "', date '", birthDate.toString(), "', '", phone, "', date '", signupDate.toString(), "', ", planID, ")");
        return updated > 0;
    }
}
