package com.akoudri.healthrecord.data;

/**
 * Created by Ali Koudri on 23/07/14.
 */
public class HeartMeasure extends Measure {

    private int diastolic;
    private int systolic;
    private int heartbeat;

    public HeartMeasure() {}

    @Override
    public String getValueString() {
        return diastolic + "/" + systolic + " - " + heartbeat + " p/m @ " + getHour();
    }

    public HeartMeasure(int personId, String date, String hour, int diastolic, int systolic, int heartbeat) {
        super(personId, date, hour);
        this.diastolic = diastolic;
        this.systolic = systolic;
        this.heartbeat = heartbeat;
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

    public boolean equalsTo(HeartMeasure other)
    {
        return super.equalsTo(other) && diastolic == other.getDiastolic() &&
                systolic == other.getSystolic() && heartbeat == other.getHeartbeat();
    }

    public boolean isNull(){
        return diastolic == 0 && systolic == 0 && heartbeat == 0;
    }
}
