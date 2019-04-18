package com.healthware.models;

import com.healthware.Utilities;
import com.healthware.base.sql.Column;
import com.healthware.base.sql.PrimaryKey;
import com.healthware.base.sql.Row;

public class Provider extends Row {
    public @PrimaryKey @Column("id") long id;
    public @Column("account_id") long accountID;
    public @Column("name") String name;
    public @Column("address") String address;
    public @Column("phone") String phone;

    public Provider() { }

    public Provider(long accountID, String name, String address, String phone) {
        this.id = Utilities.getNextUID();
        this.accountID = accountID;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }
}
