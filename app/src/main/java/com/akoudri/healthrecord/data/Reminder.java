package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 14/03/15.
 */
public class Reminder {

    private int id;
    private int personId;
    private int drugId;
    private String date;
    private String comment;

    public Reminder() {}

    public Reminder(int personId, int drugId, String date, String comment)
    {
        this.personId = personId;
        this.drugId = drugId;
        this.date = date;
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

    public int getDrugId() {
        return drugId;
    }

    public void setDrugId(int drugId) {
        this.drugId = drugId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean equalsTo(Reminder other)
    {
        return (personId == other.getPersonId()) && (drugId == other.getDrugId()) && (date.equals(other.getDate())) &&
                (comment.equals(other.getComment()));
    }
}
