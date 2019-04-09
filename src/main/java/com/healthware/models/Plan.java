package com.healthware.models;

import com.healthware.Utilities;
import com.healthware.WebPortal;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Plan {
    public long id;
    public String name;
    public double premium;
    public double deductible;
    public double copay;

    public Plan(long id, String name, double premium, double deductible, double copay) {
        this.id = id;
        this.name = name;
        this.premium = premium;
        this.deductible = deductible;
        this.copay = copay;
    }

    public static boolean create(String name, double premium, double deductible, double copay) throws Exception {
        return WebPortal.buildQuery("INSERT INTO patients (id, name, premium, deductible, copay)")
                .values(Utilities.getNextUID(), name, premium, deductible, copay)
                .andTryUpdate();
    }

    public static Plan getByID(long id) throws SQLException {
        ResultSet results = WebPortal.buildQuery("SELECT * FROM patients WHERE account_id = %1", id).andExecute();
        if (!results.next()) return null;
        else return new Plan(
                results.getLong("id"),
                results.getString("name"),
                results.getDouble("premium"),
                results.getDouble("deductible"),
                results.getDouble("copay"));
    }
}
