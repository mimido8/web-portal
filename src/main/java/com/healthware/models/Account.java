package com.healthware.models;

import com.healthware.WebPortal;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Account {
    public enum Type {
        PATIENT, EMPLOYEE, PROVIDER
    }

    public long id;
    public String username;
    public String email;
    public String passwordHash;
    public Type type;

    private static String getSHA(String password) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        StringBuilder hexBuilder = new StringBuilder();
        for (byte b : sha.digest(password.getBytes())) hexBuilder.append(String.format("%02x", b));
        return hexBuilder.toString();
    }

    public static boolean create(String username, String email, String password, Type type) throws Exception {
        Connection connection = WebPortal.getDatabaseConnectionPool().getConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO \"Accounts\" (id, username, email, password_hash, type) VALUES (" + WebPortal.getNextID() + ", '" + username + "', '" + email + "', '" + getSHA(password) + "', '" + type.toString() + "');");
        return statement.execute();
    }
}
