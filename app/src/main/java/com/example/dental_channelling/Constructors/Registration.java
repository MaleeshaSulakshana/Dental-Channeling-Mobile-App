package com.example.dental_channelling.Constructors;

public class Registration {

    String name, email, phone, address, accountType, profileImage;

    public Registration(String name, String email, String phone, String address, String accountType, String profileImage) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.accountType = accountType;
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getProfileImage() {
        return profileImage;
    }
}
