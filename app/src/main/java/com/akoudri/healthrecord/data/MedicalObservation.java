package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 23/07/14.
 * TODO: store image in V2
 */
public class MedicalObservation {

    private int id;
    private int personId;
    private String date;
    private String hour;
    private String description;

    public MedicalObservation() {}

    public MedicalObservation(int personId, String date, String hour) {
        this.personId = personId;
        this.date = date;
        this.hour = hour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
