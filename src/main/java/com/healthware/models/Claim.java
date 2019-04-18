package com.healthware.models;

import com.healthware.Utilities;
import com.healthware.base.sql.Column;
import com.healthware.base.sql.PrimaryKey;
import com.healthware.base.sql.Row;

import java.math.BigDecimal;
import java.sql.Date;

public class Claim extends Row {
    public @PrimaryKey @Column("id") long id;
    public @Column("provider_id") long providerID;
    public @Column("patient_id") long patientID;
    public @Column("service_code") String serviceCode;
    public @Column("description") String description;
    public @Column("date_submitted") Date dateSubmitted;
    public @Column("amount") BigDecimal amount;

    public Claim() { }

    private Claim(long providerID, long patientID, String serviceCode, String description, Date dateSubmitted, BigDecimal amount) {
        this.id = Utilities.getNextUID();
        this.providerID = providerID;
        this.patientID = patientID;
        this.serviceCode = serviceCode;
        this.description = description;
        this.dateSubmitted = dateSubmitted;
        this.amount = amount;
    }
}
