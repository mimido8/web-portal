package com.healthware.models;

import com.healthware.WebPortal;
import com.healthware.base.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Account extends Table.Row {
    //create, getByUsername

    public enum Type {
        PATIENT, EMPLOYEE
    }

    public @Table.PrimaryKey("id") long id;
    public @Table.Column("username") String username;
    public @Table.Column("email") String email;
    public @Table.Column("password_hash") String passwordHash;
    public @Table.Column("type") Type type;

    private Account(long id, String username, String email, String passwordHash, Type type) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.type = type;
    }

    public static Account getByUsername(String username) throws SQLException {
        ResultSet results = WebPortal.buildQuery("SELECT * FROM accounts WHERE username = '%1'", username).andExecute();
        if (!results.next()) return null;
        else return new Account(
            results.getLong("id"),
            results.getString("username"),
            results.getString("email"),
            results.getString("password_hash"),
            Type.valueOf(results.getString("type")));
    }
}
