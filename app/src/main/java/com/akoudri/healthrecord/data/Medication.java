package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 18/06/14.
 */
public class Medication {

    private int id;
    private int treatmentId;
    private int drugId;
    private int frequency;
    private DoseFrequencyKind kind;
    private String startDate;
    private int duration;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatmentId = treatmentId;
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
