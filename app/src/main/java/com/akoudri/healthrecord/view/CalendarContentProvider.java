package com.akoudri.healthrecord.view;

import java.util.Calendar;

/**
 * Created by Ali Koudri on 20/07/14.
 */
public interface CalendarContentProvider {

    int[] getMonthAppointmentsForPerson(int personId, Calendar cal);

    int[] getMonthAilmentsForPerson(int personId, Calendar cal);

    int[] getMonthMeasuresForPerson(int personId, Calendar cal);

    int[] getMonthMedicationsForPerson(int personId, Calendar cal);

    int[] getMonthObservationsForPerson(int personId, Calendar cal);

    int[] getMonthRemindersForPerson(int personId, Calendar cal);

}

