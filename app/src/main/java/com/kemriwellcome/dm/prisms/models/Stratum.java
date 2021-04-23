package com.kemriwellcome.dm.prisms.models;

public class Stratum {
    private int id;
    private String stratum;

    public Stratum(int id, String stratum) {
        this.id = id;
        this.stratum = stratum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStratum() {
        return stratum;
    }

    public void setStratum(String stratum) {
        this.stratum = stratum;
    }
}
