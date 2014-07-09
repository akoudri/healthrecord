package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 18/05/14.
 */
public class Appointment {

    //TODO: add eventually a duration to perform checking
    private int id;
    private int personId;
    private int therapistId;
    private String date;
    private String hour;
    private String comment;

    public Appointment() {}

    public Appointment(int personId, int therapistId, String date, String hour, String comment) {
        this.personId = personId;
        this.therapistId = therapistId;
        this.date = date;
        this.hour = hour;
        this.comment = comment;
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

    public int getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(int therapistId) {
        this.therapistId = therapistId;
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

    public boolean equalsTo(Appointment other)
    {
        boolean eqPerson = (personId == other.getPersonId());
        boolean eqTherapist = (therapistId == other.getTherapistId());
        boolean eqDate = date.equals(other.getDate());
        boolean eqHour = hour.equals(other.getHour());
        boolean eqComment = (comment == null)?other.getComment()==null:comment.equals(other.getComment());
        return eqPerson && eqTherapist && eqDate && eqHour && eqComment;
    }
}
