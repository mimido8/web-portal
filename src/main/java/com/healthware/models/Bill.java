package com.healthware.models;

import java.math.BigDecimal;
import java.sql.Date;

public class Bill {
    //create, getAllByID

    public long id;
    public long patientID;
    public BigDecimal amount;
    public String description;
    public Date dateGenerated;

    private Bill(long id, long patientID, BigDecimal amount, String description, Date dateGenerated) {
        this.id = id;
        this.patientID = patientID;
        this.amount = amount;
        this.description = description;
        this.dateGenerated = dateGenerated;
    }
}
