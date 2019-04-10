package com.healthware;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utilities {

    private static Gson gson = new GsonBuilder().create();
    private static long lastUIDGenerationTime = 0;
    private static long nextUIDIncrement = 0;

    public static <T> T deserializeJSON(String body, Class<T> type) {
        return gson.fromJson(body, type);
    }

    public static String serializeJSON(Object o) {
        return gson.toJson(o);
    }

    public static long getNextUID() {
        long time = System.currentTimeMillis();
        if (time != lastUIDGenerationTime) {
            nextUIDIncrement = 0;
            return (lastUIDGenerationTime = time) * 1000;
        } else {
            return (lastUIDGenerationTime * 1000) + nextUIDIncrement++;
        }
    }

    public static String getSHA(String password) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        StringBuilder hexBuilder = new StringBuilder();
        for (byte b : sha.digest(password.getBytes())) hexBuilder.append(String.format("%02x", b));
        return hexBuilder.toString();
    }
}
