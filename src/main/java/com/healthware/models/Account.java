package com.healthware.models;

import com.healthware.Utilities;
import com.healthware.WebPortal;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {
    public enum Type {
        PATIENT, EMPLOYEE, PROVIDER
    }

    public long id;
    public String username;
    public String email;
    public String passwordHash;
    public Type type;

    private Account(long id, String username, String email, String passwordHash, Type type) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.type = type;
    }

    public static boolean create(String username, String email, String passwordHash, Type type) throws Exception {
        int updated = WebPortal.executeUpdate(
            "INSERT INTO accounts (id, username, email, password_hash, type) VALUES (",
            Utilities.getNextUID(), ", '", username, "', '", email, "', '", passwordHash, "', '", type.toString(), "')");
        return updated > 0;
    }

    public static Account getByUsername(String username) throws SQLException {
        ResultSet results = WebPortal.executeQuery("SELECT * FROM accounts WHERE username = '", username, "'");
        if (!results.next()) return null;
        else return new Account(
            results.getLong("id"),
            results.getString("username"),
            results.getString("email"),
            results.getString("password_hash"),
            Type.valueOf(results.getString("type")));
    }
}
