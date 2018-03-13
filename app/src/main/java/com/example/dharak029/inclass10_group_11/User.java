/**
 * Assignment - InClass11
 * File Name - User.java
 * Dharak Shah,Viranchi Deshpande
 */
package com.example.dharak029.inclass10_group_11;

import java.io.Serializable;

/**
 * Created by dharak029 on 11/13/2017.
 */

public class User implements Serializable {

    String fname, lname, email;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }





    public User(String fname, String lname, String email) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
    }

    public User() {
    }
}