package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 18/05/14.
 */
public class Appointment {

    private int id;
    private int person;
    private int therapist;
    private String date;
    private String hour;
    private String comment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPerson() {
        return person;
    }

    public void setPerson(int person) {
        this.person = person;
    }

    public int getTherapist() {
        return therapist;
    }

    public void setTherapist(int therapist) {
        this.therapist = therapist;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
