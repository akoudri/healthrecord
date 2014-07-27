package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 23/07/14.
 */
public class TemperatureMeasure extends Measure {

    private double value;

    public TemperatureMeasure() {}

    @Override
    public String getValueString() {
        return value + " °C @ " + getHour();
    }

    public TemperatureMeasure(int personId, String date, String hour, double value) {
        super(personId, date, hour);
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public boolean equalsTo(TemperatureMeasure other)
    {
        return super.equalsTo(other) && value == other.getValue();
    }

    public boolean isNull(){
        return value == 0.0;
    }
}
