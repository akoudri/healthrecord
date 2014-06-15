package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 15/06/14.
 */
public class Treatment {

    private int id;
    private int personId;
    private int ailmentId;
    private int therapistId;
    private String startDate;
    private String endDate;
    private boolean isPermanent;
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

    public int getAilmentId() {
        return ailmentId;
    }

    public void setAilmentId(int ailmentId) {
        this.ailmentId = ailmentId;
    }

    public int getTherapistId() {
        return therapistId;
    }

    public void setTherapistId(int therapistId) {
        this.therapistId = therapistId;
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

    public boolean isPermanent() {
        return isPermanent;
    }

    public void setPermanent(boolean isPermanent) {
        this.isPermanent = isPermanent;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
