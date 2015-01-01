package com.akoudri.healthrecord.store;

import com.google.gson.annotations.Expose;

/**
 * Created by Ali Koudri on 31/12/14.
 */
public class ApptNotification {

    @Expose
    private Integer id;
    @Expose
    private Integer value;
    @Expose
    private Integer kind; //0 for hour and 1 for day

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getKind() {
        return kind;
    }

    public void setKind(Integer kind) {
        this.kind = kind;
    }
}
