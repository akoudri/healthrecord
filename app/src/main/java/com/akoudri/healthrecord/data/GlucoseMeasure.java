package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 23/07/14.
 */
public class GlucoseMeasure extends Measure {

    private double value;
    public GlucoseMeasure() {}

    @Override
    public String getValueString() {
        return value + " g/l @ " + getHour();
    }

    public GlucoseMeasure(int personId, String date, String hour, double value) {
        super(personId, date, hour);
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean equalsTo(GlucoseMeasure other)
    {
        return super.equalsTo(other) && value == other.getValue();
    }

    public boolean isNull(){
        return value == 0.0;
    }
}
