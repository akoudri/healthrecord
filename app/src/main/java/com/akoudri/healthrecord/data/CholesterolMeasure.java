package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 23/07/14.
 */
public class CholesterolMeasure extends Measure {

    private double total;
    private double hdl;
    private double ldl;
    private double triglycerides;

    public CholesterolMeasure() {}

    @Override
    public String getValueString() {
        return total + "/" + hdl + "/" + ldl + "/" + triglycerides + " @ " + getHour();
    }

    public CholesterolMeasure(int personId, String date, String hour, double total, double hdl, double ldl, double triglycerides) {
        super(personId, date, hour);
        this.total = total;
        this.hdl = hdl;
        this.ldl = ldl;
        this.triglycerides = triglycerides;
    }

    public CholesterolMeasure(CholesterolMeasure measure)
    {
        super(measure);
        this.total = measure.getTotal();
        this.hdl = measure.getHDL();
        this.ldl = measure.getLDL();
        this.triglycerides = measure.getTriglycerides();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getHDL() {
        return hdl;
    }

    public void setHDL(double hdl) {
        this.hdl = hdl;
    }

    public double getLDL() {
        return ldl;
    }

    public void setLDL(double ldl) {
        this.ldl = ldl;
    }

    public double getTriglycerides() {
        return triglycerides;
    }

    public void setTriglycerides(double triglycerides) {
        this.triglycerides = triglycerides;
    }

    public boolean equalsTo(CholesterolMeasure other)
    {
        return super.equalsTo(other) && total == other.getTotal() && hdl == other.getHDL() &&
                ldl == other.getLDL() && triglycerides == other.getTriglycerides();
    }

    public boolean isNull(){
        return total == 0.0 && hdl == 0.0 && ldl == 0.0 && triglycerides == 0.0;
    }
}
