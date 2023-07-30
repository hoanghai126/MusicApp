package com.example.musicapp.model;

public class Feedback {
    private String name;
    private String email;
    private String phone;
    private String comment;

    public Feedback() {

    }

    public Feedback(String name, String email, String phone, String comment) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
