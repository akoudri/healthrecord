package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 23/07/14.
 */
public class WeightMeasure extends Measure {

    private double value;

    public WeightMeasure() {}

    @Override
    public String getValueString() {
        return value + " kg @ " + getHour();
    }

    public WeightMeasure(int personId, String date, String hour, double value) {
        super(personId, date, hour);
        this.value = value;
    }

    public WeightMeasure(WeightMeasure measure)
    {
        super(measure);
        this.value = measure.getValue();
    }

    public double getValue() {
        return value;
    }

    public void setValue(double weight) {
        this.value = weight;
    }

    public boolean equalsTo(WeightMeasure other)
    {
        return super.equalsTo(other) && value == other.getValue();
    }

    public boolean isNull(){
        return value == 0.0;
    }
}
