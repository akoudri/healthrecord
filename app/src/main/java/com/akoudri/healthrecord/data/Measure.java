package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 30/06/14.
 */
public class Measure {

    private int id;
    private int personId;
    private String date;
    private double weight;
    private int size;
    private int cranialPerimeter;
    private double temperature;
    private double glucose;
    private int diastolic;
    private int systolic;
    private int heartbeat;

    public Measure() {}

    public Measure(int personId, String date, double weight, int size, int cranialPerimeter, double temperature, double glucose, int diastolic, int systolic, int heartbeat) {
        this.personId = personId;
        this.date = date;
        this.weight = weight;
        this.size = size;
        this.cranialPerimeter = cranialPerimeter;
        this.temperature = temperature;
        this.glucose = glucose;
        this.diastolic = diastolic;
        this.systolic = systolic;
        this.heartbeat = heartbeat;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCranialPerimeter() {
        return cranialPerimeter;
    }

    public void setCranialPerimeter(int cranialPerimeter) {
        this.cranialPerimeter = cranialPerimeter;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getGlucose() {
        return glucose;
    }

    public void setGlucose(double glucose) {
        this.glucose = glucose;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(int heartbeat) {
        this.heartbeat = heartbeat;
    }

    public boolean equalsTo(Measure other)
    {
        return personId == other.getPersonId() && date.equals(other.getDate()) &&
                weight == other.getWeight() && size == other.getSize() && cranialPerimeter == other.getCranialPerimeter() &&
                temperature == other.getTemperature() && glucose == other.getGlucose() && diastolic == other.getDiastolic() &&
                systolic == other.getSystolic() && heartbeat == other.getHeartbeat();
    }

    public boolean isNull(){
        return weight == 0.0 && size == 0 && cranialPerimeter == 0 && temperature == 0.0 &&
                glucose == 0.0 && diastolic == 0 && systolic == 0 && heartbeat == 0;
    }
}
