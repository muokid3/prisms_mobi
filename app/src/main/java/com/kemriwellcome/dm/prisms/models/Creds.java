package com.kemriwellcome.dm.prisms.models;

public class Creds {
    private String end_point;

    public Creds() {
    }

    public Creds(String end_point) {
        this.end_point = end_point;
    }

    public String getEnd_point() {
        return end_point;
    }

    public void setEnd_point(String end_point) {
        this.end_point = end_point;
    }
}
