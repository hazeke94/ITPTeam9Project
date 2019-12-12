package com.example.portermanagementsystem.Model;

public class Location {
    private String level;
    private String locationName;
    private String stn;
    private boolean admission;
    private boolean adhoc;

    public Location() { }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getStn() {
        return stn;
    }

    public void setStn(String stn) {
        this.stn = stn;
    }

    public boolean isAdmission() { return admission; }

    public void setAdmission(boolean admission) {
        this.admission = admission;
    }

    public boolean isAdhoc() {
        return adhoc;
    }

    public void setAdhoc(boolean adhoc) {
        this.adhoc = adhoc;
    }
}
