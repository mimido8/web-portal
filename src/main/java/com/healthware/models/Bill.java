package com.healthware.models;

import com.healthware.base.sql.Column;
import com.healthware.base.sql.PrimaryKey;
import com.healthware.base.sql.Row;

import java.math.BigDecimal;
import java.sql.Date;

public class Bill extends Row {
    public @PrimaryKey @Column("id") long id;
    public @Column("patient_id") long patientID;
    public @Column("amount") BigDecimal amount;
    public @Column("description") String description;
    public @Column("date_generated") Date dateGenerated;

    public Bill() { }

    public Bill(long id, long patientID, BigDecimal amount, String description, Date dateGenerated) {
        this.id = id;
        this.patientID = patientID;
        this.amount = amount;
        this.description = description;
        this.dateGenerated = dateGenerated;
    }
}
