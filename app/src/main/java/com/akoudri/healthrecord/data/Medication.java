package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 18/06/14.
 */
public class Medication {

    private int id;
    private int personId;
    private int ailmentId;
    private int drugId;
    private int frequency;
    private DoseFrequencyKind kind;
    private String startDate;
    private int duration;

    public Medication() {}

    public Medication(Medication medic)
    {
        this.personId = medic.getPersonId();
        this.ailmentId = medic.getAilmentId();
        this.drugId = medic.getDrugId();
        this.frequency = medic.getFrequency();
        this.kind = medic.getKind();
        this.startDate = medic.getStartDate();
        this.duration = medic.getDuration();
    }

    public boolean equalsTo(Medication medic)
    {
        return this.personId == medic.getPersonId() && this.ailmentId == medic.getAilmentId() &&
                this.drugId == medic.getDrugId() && this.frequency == medic.getFrequency() &&
                this.kind == medic.getKind() && this.startDate.equals(medic.getStartDate()) &&
                this.duration == medic.getDuration();
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

    public int getAilmentId() {
        return ailmentId;
    }

    public void setAilmentId(int ailmentId) {
        this.ailmentId = ailmentId;
    }

    public int getDrugId() {
        return drugId;
    }

    public void setDrugId(int drugId) {
        this.drugId = drugId;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public DoseFrequencyKind getKind() {
        return kind;
    }

    public void setKind(DoseFrequencyKind kind) {
        this.kind = kind;
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
}
