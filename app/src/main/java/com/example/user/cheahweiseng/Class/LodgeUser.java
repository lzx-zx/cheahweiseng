package com.example.user.cheahweiseng.Class;

/**
 * Created by Lee Zi Xiang on 28/11/2017.
 */

public class LodgeUser {

    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String ic;
    private String phoneNumber;
    private String department;
    private String programme;

    public LodgeUser() {
    }

    public LodgeUser(String firstName, String lastName, String password, String email, String ic, String phoneNumber, String department) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.ic = ic;
        this.phoneNumber = phoneNumber;
        this.department = department;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }
}
