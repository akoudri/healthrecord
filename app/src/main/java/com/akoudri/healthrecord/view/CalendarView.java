package com.akoudri.healthrecord.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.akoudri.healthrecord.app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ali Koudri on 27/04/14.
 */
public class CalendarView extends View {

    private Calendar _cal, today;
    private Paint paint;
    private int width, height;
    private String[] daysOfWeek;
    private List<Rect> rects;
    private static final int titleSize = 42;
    private static final int tableTitleSize = 20;
    private static final int corner = 5;
    private static final float nbRatio = 0.3f;
    private float delta;
    private float ratio;
    private float stepx, stepy;
    private float yCalendar;
    private float tsize, ttsize;

    public CalendarView(Context context) {
        super(context);
        paint = new Paint();
        _cal = Calendar.getInstance();
        today = Calendar.getInstance();
        rects = new ArrayList<Rect>();
        initDaysOfWeek();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        width = getWidth();
        height = getHeight();
        ratio = width/480.0f;
        stepx = width / 7;
        tsize = titleSize * ratio;
        ttsize = tableTitleSize * ratio;
        yCalendar = 3 * (tsize + ttsize) + 10;
        stepy = (height - yCalendar) / 6;
        delta = stepy * (nbRatio + 1) / 2;
        canvas.drawColor(getResources().getColor(R.color.app_bg_color));
        displayTitleDate(canvas);
        displayDays(canvas);
        displayNumbers(canvas);
        if (today.get(Calendar.MONTH) == _cal.get(Calendar.MONTH))
            displayCurrentDate(canvas);
        invalidate();
    }

    private void displayTitleDate(Canvas canvas)
    {
        //Calendar calendar = Calendar.getInstance();
        paint.setColor(getResources().getColor(R.color.regular_text_color));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(tsize);
        paint.setFakeBoldText(true);
        String month = today.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()).toUpperCase();
        int year = today.get(Calendar.YEAR);
        String currentDate = month + " " + year;
        canvas.drawText(currentDate, getWidth() / 2, 2 * tsize, paint);
    }

    private void displayDays(Canvas canvas)
    {
        paint.setColor(getResources().getColor(R.color.regular_text_color));
        paint.setStrokeWidth(1);
        canvas.drawLine(0, 3 * tsize, width, 3 * tsize, paint);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(ttsize);
        paint.setFakeBoldText(true);
        int i = 0;
        float y = 3 * tsize + 2 * ttsize;
        for (String day : daysOfWeek)
        {
            canvas.drawText(day, stepx * (i + .5f), y, paint);
            i++;
        }
        y = 3 * (tsize + ttsize);
        canvas.drawLine(0, y, width, y, paint);
    }

    private void initDaysOfWeek()
    {
        Calendar c = Calendar.getInstance();
        daysOfWeek = new String[7];
        for (int i = 0; i < 7; i++)
        {
            c.set(Calendar.DAY_OF_WEEK, (i + 2) % 7);
            daysOfWeek[i] = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        }
    }

    private Rect getRect(int x, int y)
    {
        int left = (int) (x * stepx + 1);
        int top = (int) (yCalendar + y * stepy + 1);
        int right = (int) (left + stepx - 1);
        int bottom = (int) (top + stepy - 1);
        return new Rect(left, top, right, bottom);
    }

    private void displayNumbers(Canvas canvas)
    {
        rects.clear();
        int currentDay = today.get(Calendar.DAY_OF_MONTH);
        int currentMonth = today.get(Calendar.MONTH);
        //Calendar calendar = Calendar.getInstance();
        paint.setColor(getResources().getColor(R.color.regular_text_color));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((int)(stepy * nbRatio));
        paint.setFakeBoldText(true);
        paint.setStrokeWidth(2.0f);
        //retrieve bounds for the current month
        int min = _cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        int max = _cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //set the calendar to the first day of the month and compute first x
        _cal.set(Calendar.DAY_OF_MONTH, min);
        int firstDay = _cal.get(Calendar.DAY_OF_WEEK);
        int firstx = (firstDay + 5) % 7;
        Rect rect;
        RectF rectf;
        //Draw the first day
        int x = firstx;
        int y = 0;
        rect = getRect(x, y);
        rects.add(rect);
        rectf = new RectF(rect);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(rectf, corner, corner, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("" + 1, rect.left + stepx / 2, rect.top + delta, paint);
        //Draw other days
        for (int i = min + 1; i <= max; i++)
        {
            x = (x + 1) % 7;
            if (x == 0) y++;
            rect = getRect(x, y);
            rects.add(rect);
            if (_cal.get(Calendar.MONTH) == currentMonth && i == currentDay) continue;
            rectf = new RectF(rect);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(rectf, corner, corner, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText("" + i, rect.left + stepx / 2, rect.top + delta, paint);
        }
        _cal.set(Calendar.DAY_OF_MONTH, currentDay);
    }

    private void displayCurrentDate(Canvas canvas)
    {
        int day = _cal.get(Calendar.DAY_OF_MONTH);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((int)(stepy * nbRatio));
        paint.setFakeBoldText(true);
        paint.setColor(getResources().getColor(R.color.title_button_text_color));
        paint.setStrokeWidth(3.0f);
        Rect rect = rects.get(day - 1);
        RectF rectf = new RectF(rect);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(rectf, corner, corner, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("" + day, rect.left + stepx / 2, rect.top + delta, paint);
    }

}
