package com.akoudri.healthrecord.data;

import com.akoudri.healthrecord.app.R;

/**
 * Created by Ali Koudri on 23/07/14.
 */
public class SizeMeasure extends AbstractMeasure {

    private int value;

    public SizeMeasure() {}

    @Override
    public String getValueString() {
        return value + " cm @ " + getHour();
    }

    public SizeMeasure(int personId, String date, String hour, int value) {
        super(personId, date, hour);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean equalsTo(SizeMeasure other)
    {
        return super.equalsTo(other) && value == other.getValue();
    }

    public boolean isNull(){
        return value == 0;
    }
}
