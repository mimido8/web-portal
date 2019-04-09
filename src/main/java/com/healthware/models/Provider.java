package com.healthware.models;

public class Provider {
    //create

    public long id;
    public long accountID;
    public String name;
    public String address;
    public String phone;

    private Provider(long id, long accountID, String name, String address, String phone) {
        this.id = id;
        this.accountID = accountID;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }
}
