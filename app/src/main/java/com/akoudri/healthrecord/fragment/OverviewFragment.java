package com.akoudri.healthrecord.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.PersonManager;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Appointment;
import com.akoudri.healthrecord.data.Drug;
import com.akoudri.healthrecord.data.DrugTable;
import com.akoudri.healthrecord.data.Measure;
import com.akoudri.healthrecord.data.Reminder;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapistTable;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.List;

/**
 * Recreated by Ali Koudri on 14/05/15.
 */
public class OverviewFragment extends EditDayFragment {

    private View view;
    private TextView ovSynthesis;

    private HealthRecordDataSource dataSource;

    private String date;

    public static OverviewFragment newInstance()
    {
        return new OverviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_overview, container, false);
        ovSynthesis = (TextView) view.findViewById(R.id.ov_synthesis);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dataSource == null) return;
        fillWidgets();
    }

    public void setCurrentDate(int day, int month, int year)
    {
        date = String.format("%02d/%02d/%4d", day, month + 1, year);
    }

    public void refresh()
    {
        fillWidgets();
    }

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    @Override
    public void resetObjectId() {
        //Nothing to do here
    }

    private void fillWidgets()
    {
        StringBuilder stringBuilder = new StringBuilder();
        int personId = PersonManager.getInstance().getPerson().getId();
        List<Appointment> allAppointments = dataSource.getAppointmentTable().getDayAppointmentsForPerson(personId, date);
        stringBuilder.append("<h2>" + getString(R.string.appointments) + "</h2>");
        if (allAppointments.size() > 0) {
            appendAppointments(stringBuilder, allAppointments);
        } else {
            stringBuilder.append("<b>" + getString(R.string.no_appt_today) + "</b></br>");
        }
        List<Reminder> allReminders = dataSource.getReminderTable().getDayRemindersForPerson(personId, date);
        stringBuilder.append("<h2>" + getString(R.string.reminders) + "</h2>");
        if (allReminders.size() > 0) {
            appendReminders(stringBuilder, allReminders);
        } else {
            stringBuilder.append("<b>" + getString(R.string.no_reminder_today) + "</b></br>");
        }
        ovSynthesis.setText(Html.fromHtml(stringBuilder.toString()));
    }

    private void appendAppointments(StringBuilder stringBuilder, List<Appointment> appointments)
    {
        TherapistTable table = dataSource.getTherapistTable();
        Therapist t = null;
        for (Appointment a : appointments)
        {
            t = table.getTherapistWithId(a.getTherapistId());
            stringBuilder.append("<b>" + t.getName() + " @ " + a.getHour() + "</b><br/>");
        }
    }

    private void appendReminders(StringBuilder stringBuilder, List<Reminder> reminders)
    {
        DrugTable table = dataSource.getDrugTable();
        Drug d = null;
        for (Reminder r : reminders)
        {
            d = table.getDrugWithId(r.getDrugId());
            stringBuilder.append("<b>" + d.getName() + "</b><br/>");
        }
    }

}
