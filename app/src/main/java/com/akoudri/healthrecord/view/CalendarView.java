package com.akoudri.healthrecord.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.akoudri.healthrecord.app.R;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Ali Koudri on 27/04/14.
 */
public class CalendarView extends View {

    private Calendar calendar;
    private Paint paint;
    private Rect bounds = new Rect();
    private int width, height;
    private String[] daysOfWeek;

    private static int titleSize = 42;
    private static int tableTitleSize = 20;

    public CalendarView(Context context) {
        super(context);
        calendar = Calendar.getInstance();
        paint = new Paint();
        initDaysOfWeek();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        width = getWidth();
        height = getHeight();
        canvas.drawColor(getResources().getColor(R.color.app_bg_color));
        displayTitleDate(canvas);
        displayDays(canvas);
        displayGrid(canvas);
        invalidate();
    }

    private void displayTitleDate(Canvas canvas)
    {
        paint.setColor(getResources().getColor(R.color.regular_text_color));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(titleSize);
        paint.setFakeBoldText(true);
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).toUpperCase();
        int year = calendar.get(Calendar.YEAR);
        String currentDate = month + " " + year;
        paint.getTextBounds(currentDate, 0, currentDate.length(), bounds);
        canvas.drawText(currentDate, getWidth() / 2, titleSize, paint);
    }

    private void displayDays(Canvas canvas)
    {
        paint.setColor(getResources().getColor(R.color.regular_text_color));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(tableTitleSize);
        paint.setFakeBoldText(true);
        int step = width / 7;
        int i = 0;
        int y = titleSize + 5 + tableTitleSize;
        for (String day : daysOfWeek)
        {
            canvas.drawText(day, step * (i + .5f), y, paint);
            i++;
        }
    }

    private void initDaysOfWeek()
    {
        daysOfWeek = new String[7];
        for (int i = 0; i < 7; i++)
        {
            calendar.set(Calendar.DAY_OF_WEEK, (i + 2) % 7);
            daysOfWeek[i] = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        }
    }

    private void displayGrid(Canvas canvas)
    {
        paint.setColor(getResources().getColor(R.color.regular_text_color));
        paint.setStrokeWidth(1);
        int y = titleSize + tableTitleSize + 12;
        int stepx = width / 7;
        int stepy = (height - y) / 6;
        //day line
        canvas.drawLine(0, titleSize + 4, width, titleSize + 4, paint);
        //horizontal lines
        for (int i = 0; i < 6; i++)
        {
            canvas.drawLine(0, y + (stepy * i), width, y + (stepy * i), paint);
        }
        //vertical lines
        for (int i = 1; i < 7; i++)
        {
            canvas.drawLine(stepx * i, y, stepx * i, height, paint);
        }
    }

}
