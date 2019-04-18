package com.healthware.models;

import com.healthware.Utilities;
import com.healthware.base.sql.Column;
import com.healthware.base.sql.PrimaryKey;
import com.healthware.base.sql.Row;

public class Employee extends Row {
    public @PrimaryKey @Column("id") long id;
    public @Column("account_id") long accountID;
    public @Column("name") String name;
    public @Column("phone") String phone;

    public Employee() { }

    private Employee(long accountID, String name, String phone) {
        this.id = Utilities.getNextUID();
        this.accountID = accountID;
        this.name = name;
        this.phone = phone;
    }
}
