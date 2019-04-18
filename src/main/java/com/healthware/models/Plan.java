package com.healthware.models;

import com.healthware.Utilities;
import com.healthware.base.sql.Column;
import com.healthware.base.sql.PrimaryKey;
import com.healthware.base.sql.Row;

import java.math.BigDecimal;

public class Plan extends Row {
    public @PrimaryKey @Column("id") long id;
    public @Column("name") String name;
    public @Column("premium") BigDecimal premium;
    public @Column("deductible") BigDecimal deductible;
    public @Column("copay") BigDecimal copay;

    public Plan() { }

    public Plan(String name, BigDecimal premium, BigDecimal deductible, BigDecimal copay) {
        this.id = Utilities.getNextUID();
        this.name = name;
        this.premium = premium;
        this.deductible = deductible;
        this.copay = copay;
    }
}
