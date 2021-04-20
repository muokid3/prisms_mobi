package com.kemriwellcome.dm.prisms.models;

public class User {

    private  int id;
    private  int user_group;
    private  String phone_no;
    private  String title;
    private  String first_name;
    private  String last_name;
    private  int site_id;
    private  int active;
    private  String email;
    private  String access_token;
    private  String token_type;

    public User(int id, int user_group, String phone_no, String title, String first_name, String last_name, int site_id, int active,
                String email, String access_token, String token_type) {
        this.id = id;
        this.user_group = user_group;
        this.phone_no = phone_no;
        this.title = title;
        this.first_name = first_name;
        this.last_name = last_name;
        this.site_id = site_id;
        this.active = active;
        this.email = email;
        this.access_token = access_token;
        this.token_type = token_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_group() {
        return user_group;
    }

    public void setUser_group(int user_group) {
        this.user_group = user_group;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }
}
