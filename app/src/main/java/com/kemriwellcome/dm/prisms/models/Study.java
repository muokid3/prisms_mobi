package com.kemriwellcome.dm.prisms.models;

public class Study {
    private int id;
    private String study_name;
    private String study_detail;

    public Study(int id, String study_name, String study_detail) {
        this.id = id;
        this.study_name = study_name;
        this.study_detail = study_detail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudy_name() {
        return study_name;
    }

    public void setStudy_name(String study_name) {
        this.study_name = study_name;
    }

    public String getStudy_detail() {
        return study_detail;
    }

    public void setStudy_detail(String study_detail) {
        this.study_detail = study_detail;
    }
}
