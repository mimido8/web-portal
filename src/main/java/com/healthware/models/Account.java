package com.healthware.models;

import com.healthware.Utilities;
import com.healthware.base.sql.Column;
import com.healthware.base.sql.PrimaryKey;
import com.healthware.base.sql.Row;

public class Account extends Row {
    public enum Type {
        PATIENT, EMPLOYEE
    }

    public @PrimaryKey @Column("id") long id;
    public @Column("username") String username;
    public @Column("email") String email;
    public @Column("password_hash") String passwordHash;
    public @Column("type") Type type;

    public Account() { }

    public Account(String username, String email, String passwordHash, Type type) {
        this.id = Utilities.getNextUID();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.type = type;
    }
}
