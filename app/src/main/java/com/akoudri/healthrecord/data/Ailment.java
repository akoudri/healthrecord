package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 15/06/14.
 */
public class Ailment {

    private int id;
    private int personId;
    private int illnessId;
    private int therapistId;
    private String startDate;
    private int duration;
    private String comment;

    public Ailment() {}

    public Ailment(int personId, int illnessId, int therapistId, String startDate, int duration, String comment) {
        this.personId = personId;
        this.illnessId = illnessId;
        this.therapistId = therapistId;
        this.startDate = startDate;
        this.duration = duration;
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

    public int getIllnessId() {
        return illnessId;
    }

    public void setIllnessId(int illnessId) {
        this.illnessId = illnessId;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean equalsTo(Ailment other)
    {
        boolean checkComment = (comment == null)?other.comment == null:comment.equals(other.getComment());
        return personId == other.getPersonId() && illnessId == other.getIllnessId() &&
                therapistId == other.getTherapistId() && startDate.equals(other.getStartDate()) &&
                duration == other.getDuration() && checkComment;
    }
}
