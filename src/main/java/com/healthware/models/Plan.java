package com.healthware.models;

import com.healthware.Utilities;
import com.healthware.WebPortal;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Plan {
    //create, update, getAll

    public long id;
    public String name;
    public BigDecimal premium;
    public BigDecimal deductible;
    public BigDecimal copay;

    private Plan(long id, String name, BigDecimal premium, BigDecimal deductible, BigDecimal copay) {
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
                results.getBigDecimal("premium"),
                results.getBigDecimal("deductible"),
                results.getBigDecimal("copay"));
    }
}
