package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 23/07/14.
 */
public class AbstractMeasure {

    public static final int WEIGHT_MEASURE_TYPE = 1;
    public static final int SIZE_MEASURE_TYPE = 2;
    public static final int TEMPERATURE_MEASURE_TYPE = 3;
    public static final int CP_MEASURE_TYPE = 4;
    public static final int GLUCOSE_MEASURE_TYPE = 5;
    public static final int HEART_MEASURE_TYPE = 6;

    private int id;
    private int personId;
    private String date;
    private String hour;
    private String valueString;
    private int type;

    public AbstractMeasure() {}

    public AbstractMeasure(int personId, String date, String hour) {
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

    public boolean equalsTo(AbstractMeasure other)
    {
        return personId == other.getPersonId() && date.equals(other.getDate()) && hour.equals(other.getHour());
    }

    public String getValueString()
    {
        return valueString;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setValueString(String valueString)
    {
        this.valueString = valueString;
    }
}
