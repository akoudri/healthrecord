package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 23/07/14.
 */
public class CholesterolMeasure extends Measure {

    private float total;
    private float hdl;
    private float ldl;
    private float triglycerids;

    public CholesterolMeasure() {}

    @Override
    public String getValueString() {
        return total + "/" + hdl + "/" + ldl + "/" + triglycerids + " @ " + getHour();
    }

    public CholesterolMeasure(int personId, String date, String hour, float total, float hdl, float ldl, float triglycerids) {
        super(personId, date, hour);
        this.total = total;
        this.hdl = hdl;
        this.ldl = ldl;
        this.triglycerids = triglycerids;
    }

    public CholesterolMeasure(CholesterolMeasure measure)
    {
        super(measure);
        this.total = measure.getTotal();
        this.hdl = measure.getHDL();
        this.ldl = measure.getLDL();
        this.triglycerids = measure.getTriglycerids();
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getHDL() {
        return hdl;
    }

    public void setHDL(float hdl) {
        this.hdl = hdl;
    }

    public float getLDL() {
        return ldl;
    }

    public void setLDL(float ldl) {
        this.ldl = ldl;
    }

    public float getTriglycerids() {
        return triglycerids;
    }

    public void setTriglycerids(float triglycerids) {
        this.triglycerids = triglycerids;
    }

    public boolean equalsTo(CholesterolMeasure other)
    {
        return super.equalsTo(other) && total == other.getTotal() && hdl == other.getHDL() &&
                ldl == other.getLDL() && triglycerids == other.getTriglycerids();
    }

    public boolean isNull(){
        return total == 0.0 && hdl == 0.0 && ldl == 0.0 && triglycerids == 0.0;
    }
}
