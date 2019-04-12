package com.healthware.models;

import java.math.BigDecimal;

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
}
