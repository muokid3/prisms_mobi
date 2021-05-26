package com.kemriwellcometrust.dm.prisms.models;

public class Site {
    private String site_name;
    private int id;

    public Site(String site_name, int id) {
        this.site_name = site_name;
        this.id = id;
    }

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
