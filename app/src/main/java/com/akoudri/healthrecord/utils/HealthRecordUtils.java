package com.akoudri.healthrecord.utils;

import android.app.Activity;
import android.util.Patterns;
import android.widget.EditText;

import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.BloodType;
import com.akoudri.healthrecord.data.DoseFrequencyKind;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Ali Koudri on 01/07/14.
 */
public final class HealthRecordUtils {

    //Date shall be formatted this way: dd/mm/yyyy
    //This is guaranteed because user does not access this method
    public static Calendar stringToCalendar(String date)
    {
        if (date == null) return null;
        String pattern = "\\d{2}/\\d{2}/\\d{4}";
        if (!date.matches(pattern)) return null;
        String[] dateArray = date.split("/");
        int dd = Integer.parseInt(dateArray[0]);
        int mm = Integer.parseInt(dateArray[1]) - 1;
        int yyyy = Integer.parseInt(dateArray[2]);
        Calendar res = Calendar.getInstance();
        int fmm;
        switch (mm)
        {
            case 0:
                fmm = Calendar.JANUARY; break;
            case 1:
                fmm = Calendar.FEBRUARY; break;
            case 2:
                fmm = Calendar.MARCH; break;
            case 3:
                fmm = Calendar.APRIL; break;
            case 4:
                fmm = Calendar.MAY; break;
            case 5:
                fmm = Calendar.JUNE; break;
            case 6:
                fmm = Calendar.JULY; break;
            case 7:
                fmm = Calendar.AUGUST; break;
            case 8:
                fmm = Calendar.SEPTEMBER; break;
            case 9:
                fmm = Calendar.OCTOBER; break;
            case 10:
                fmm = Calendar.NOVEMBER; break;
            default:
                fmm = Calendar.DECEMBER; break;
        }
        res.set(yyyy, fmm, dd, 0, 0, 0);
        res.set(Calendar.MILLISECOND, 0);
        return res;
    }

    public static DoseFrequencyKind int2kind(int k)
    {
        switch (k)
        {
            case 0:
                return DoseFrequencyKind.HOUR;
            case 1:
                return DoseFrequencyKind.DAY;
            case 2:
                return DoseFrequencyKind.WEEK;
            case 3:
                return DoseFrequencyKind.MONTH;
            case 4:
                return DoseFrequencyKind.YEAR;
            default:
                return DoseFrequencyKind.LIFE;
        }
    }
    
    public static BloodType int2bloodType(int b)
    {
         switch (b)
        {
            case 0: return BloodType.OMINUS;
            case 1: return BloodType.OPLUS;
            case 2: return BloodType.AMINUS;
            case 3: return BloodType.APLUS;
            case 4: return BloodType.BMINUS;
            case 5: return BloodType.BPLUS;
            case 6: return BloodType.ABMINUS;
            case 7: return BloodType.ABPLUS;
            default: return BloodType.UNKNOWN;
        }
    }

    public static void highlightActivityFields(Activity activity, EditText... fields)
    {
        for (EditText field : fields)
        {
            field.setBackgroundColor(activity.getResources().getColor(R.color.notValidField));
            field.invalidate();
        }
    }

    public static void highlightActivityFields(Activity activity, List<EditText> fields, boolean highlight)
    {
        if (highlight) {
            for (EditText field : fields) {
                field.setBackgroundColor(activity.getResources().getColor(R.color.notValidField));
                field.invalidate();
            }
        }
        else
        {
            for (EditText field : fields) {
                field.setBackgroundColor(activity.getResources().getColor(android.R.color.white));
                field.invalidate();
            }
        }
    }

    public static boolean isValidName(String name)
    {
        if (name == null) return false;
        String pattern = "(\\w+(-|\\x20)?\\w+)+";
        return name.matches(pattern);
    }

    public static boolean isValidSsn(String ssn)
    {
        if (ssn == null) return false;
        String pattern = "\\s*";
        return  (! ssn.matches(pattern));
    }

    public static boolean isValidSpecialty(String specialty)
    {
        if (specialty == null) return false;
        String pattern = "(\\w+(-|\\x20)?\\w+)+";
        return specialty.matches(pattern);
    }

}
