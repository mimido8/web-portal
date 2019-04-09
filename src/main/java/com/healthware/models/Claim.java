package com.healthware.models;

import java.math.BigDecimal;
import java.sql.Date;

public class Claim {
    //create, getAll

    public long id;
    public long providerID;
    public long patientID;
    public String serviceCode;
    public String description;
    public Date dateSubmitted;
    public BigDecimal amount;

    private Claim(long id, long providerID, long patientID, String serviceCode, String description, Date dateSubmitted, BigDecimal amount) {
        this.id = id;
        this.providerID = providerID;
        this.patientID = patientID;
        this.serviceCode = serviceCode;
        this.description = description;
        this.dateSubmitted = dateSubmitted;
        this.amount = amount;
    }
}
