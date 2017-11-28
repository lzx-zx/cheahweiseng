package com.example.user.cheahweiseng.Class;

/**
 * Created by USER on 2017/10/5.
 */

public class LodgeProvider {

    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String ic;
    private String phoneNumber;

    public LodgeProvider(String firstName, String lastName, String password, String email, String ic, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.ic = ic;
        this.phoneNumber = phoneNumber;
    }

    public LodgeProvider() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIc() {
        return ic;
    }

    public void setIc(String ic) {
        this.ic = ic;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
