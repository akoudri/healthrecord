package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 23/07/14.
 */
public class CranialPerimeterMeasure extends Measure {

    private int value;

    public CranialPerimeterMeasure() {}

    @Override
    public String getValueString() {
        return value + " cm @ " + getHour();
    }

    public CranialPerimeterMeasure(int personId, String date, String hour, int value) {
        super(personId, date, hour);
        this.value = value;
    }

    public CranialPerimeterMeasure(CranialPerimeterMeasure measure)
    {
        super(measure);
        this.value = measure.getValue();
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean equalsTo(CranialPerimeterMeasure other)
    {
        return super.equalsTo(other) && value == other.getValue();
    }

    public boolean isNull(){
        return value == 0;
    }
}
