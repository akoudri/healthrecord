package com.akoudri.healthrecord.app;

import android.app.Application;

/**
 * Created by Ali Koudri on 31/03/14.
 */
public class HealthRecordApp extends Application{

    private static HealthRecordApp singleton;

    public static HealthRecordApp getInstance()
    {
        return singleton;
    }

    @Override
    public final void onCreate()
    {
        super.onCreate();
        singleton = this;
    }

}
