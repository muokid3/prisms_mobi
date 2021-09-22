package com.kemriwellcometrust.dm.prisms.models;

import java.util.ArrayList;

public class SiteStudy {

    private int id;
    private int site_id;
    private int study_id;
    private int study_coordinator;
    private String date_initiated;
    private String status;
    private String study_name;
    private String study_detail;
    private String site_name;
    private ArrayList<String> strata;

    public SiteStudy(int id, int site_id, int study_id, int study_coordinator, String date_initiated, String status, String study_name,
                     String study_detail, String site_name, ArrayList<String> strata) {
        this.id = id;
        this.site_id = site_id;
        this.study_id = study_id;
        this.study_coordinator = study_coordinator;
        this.date_initiated = date_initiated;
        this.status = status;
        this.study_name = study_name;
        this.study_detail = study_detail;
        this.site_name = site_name;
        this.strata = strata;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public int getStudy_id() {
        return study_id;
    }

    public void setStudy_id(int study_id) {
        this.study_id = study_id;
    }

    public int getStudy_coordinator() {
        return study_coordinator;
    }

    public void setStudy_coordinator(int study_coordinator) {
        this.study_coordinator = study_coordinator;
    }

    public String getDate_initiated() {
        return date_initiated;
    }

    public void setDate_initiated(String date_initiated) {
        this.date_initiated = date_initiated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public ArrayList<String> getStrata() {
        return strata;
    }

    public void setStrata(ArrayList<String> strata) {
        this.strata = strata;
    }
}
