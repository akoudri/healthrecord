package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 28/05/14.
 */
public class Ailment {

    private int id;
    private int personId;
    private int illnessId;
    private boolean isChronic;
    private String startDate;
    private String endDate;
    private String comment;

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

    public int getIllnessId() {
        return illnessId;
    }

    public void setIllnessId(int illnessId) {
        this.illnessId = illnessId;
    }

    public boolean isChronic() {
        return isChronic;
    }

    public void setChronic(boolean isChronic) {
        this.isChronic = isChronic;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
