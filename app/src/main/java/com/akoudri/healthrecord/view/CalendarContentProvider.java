package com.akoudri.healthrecord.view;

/**
 * Created by Ali Koudri on 20/07/14.
 */
public interface CalendarContentProvider {

    int countAppointmentsForDay(int personId, long date);

    int countAilmentsForDay(int personId, long date);

    int countMeasuresForDay(int personId, long date);

    int countMedicsForDay(int personId, long date);

}

