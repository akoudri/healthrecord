package com.akoudri.healthrecord.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.akoudri.healthrecord.app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ali Koudri on 27/04/14.
 * Implements a calendar view for health record
 */
public class CalendarView extends View implements View.OnTouchListener {

    private int personId = 0; //id of the person
    private Calendar _cal, today; //_cal is used to iterate over the days of the calendar, today represents the current day
    private Paint paint; //for drawing on the canvas
    private int width, height; //to store the dimension of the screen
    private String[] daysOfWeek; //to display on the screen
    private List<Rect> rects; //list of coordinates representing the days
    private float ratio; //used to adapt sizes between various screen configurations
    private static final int titleSize = 42; //absolute size of the month.year
    private static final int tableTitleSize = 20; //absolute size of the days
    private float tsize, ttsize; //relative size of the month.year and days
    private static final int corner = 5; //radius of the rectangles
    private static final float nbRatio = 0.3f; //used to calculate delta
    private float delta; //internal vertical space in the cells
    private float stepx, stepy; //used to display the cells of the calendar -> 7x6 cells
    private float yCalendar; //y coordinate for the first row of the calendar
    private Bitmap next, previous; //Images for navigation
    //private Rect[] nav = new Rect[2]; //Locations to display navigation images
    private Rect selectedRect = null; //references the cell that has been touched by the user

    private CalendarContentProvider calendarContentProvider;

    //used to manage navigation between months
    private int tx = 0;
    private int ty = 0;

    //TODO: add icons in the cells


    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();//set paint object
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        _cal = Calendar.getInstance();//set _cal and today to current day
        _cal.set(Calendar.HOUR_OF_DAY, 0);
        _cal.set(Calendar.MINUTE, 0);
        _cal.set(Calendar.SECOND, 0);
        _cal.set(Calendar.MILLISECOND, 0);
        today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        rects = new ArrayList<Rect>();
        initDaysOfWeek(); //retrieve the localized days of the week
        //listen touch events
        setOnTouchListener(this);
    }

    public void setPersonId(int personId)
    {
        this.personId = personId;
    }

    public void setCalendarContentProvider(CalendarContentProvider provider)
    {
        this.calendarContentProvider = provider;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (personId == 0) return; //nothing to display
        width = getWidth();
        height = getHeight();
        ratio = width/480.0f;
        stepx = width / 7;
        tsize = titleSize * ratio;
        ttsize = tableTitleSize * ratio;
        yCalendar = 3 * (tsize + ttsize) + 10;
        stepy = (height - yCalendar) / 6;
        delta = stepy * (nbRatio + 1) / 2;
        canvas.drawColor(getResources().getColor(R.color.app_bg_color)); //set bg to app bg
        displayTitleDate(canvas);
        //displayNavigation(canvas);
        displayDaysOfTheWeek(canvas);
        displayDaysOfTheMonth(canvas);
        highlightRect(canvas);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;
        Point size = new Point();
        WindowManager w = ((Activity)getContext()).getWindowManager();
        w.getDefaultDisplay().getSize(size);
        width = size.x;
        //set the ratio between the width and the height of the view
        height = (int) (size.x * 1.5);
        setMeasuredDimension(width, height);
    }

    //returns whether the current day (_cal) is the actual day (today)
    private boolean isToday()
    {
        return _cal.equals(today);
    }

    //displays the date title
    private void displayTitleDate(Canvas canvas)
    {
        paint.setColor(getResources().getColor(R.color.regular_text_color));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(tsize);
        paint.setFakeBoldText(true);
        String month = _cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()).toUpperCase();
        int year = _cal.get(Calendar.YEAR);
        String currentDate = month + " " + year;
        canvas.drawText(currentDate, getWidth() / 2, 2 * tsize, paint);
    }

    //display the days of the weeks
    private void displayDaysOfTheWeek(Canvas canvas)
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

    //initialize the array containing the days of the week - With localization
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

    //computes the coordinates of any cell of the calendar
    private Rect getRect(int x, int y)
    {
        int left = (int) (x * stepx + 1);
        int top = (int) (yCalendar + y * stepy + 1);
        int right = (int) (left + stepx - 1);
        int bottom = (int) (top + stepy - 1);
        return new Rect(left, top, right, bottom);
    }

    //display any day of the month except actual day
    private void displayDaysOfTheMonth(Canvas canvas)
    {
        rects.clear(); //clear the list of rects
        //prepare the painting for days different of actual day
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
        //used to draw rects
        Rect rect;
        RectF rectf;
        int c = corner / 2; //for the oblique line
        //Draw the first day
        int x = firstx;
        int y = 0;
        rect = getRect(x, y);
        rects.add(rect);
        if (isToday())
        {
            displayCurrentDate(canvas);
        }
        else
        {
            rectf = new RectF(rect);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(rectf, corner, corner, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText("" + 1, rect.left + stepx / 2, rect.top + delta, paint);
            if (_cal.before(today))
            {
                canvas.drawLine(rect.left + c, rect.bottom - c, rect.right - c, rect.top + c, paint);
            }
        }
        //Draw other days
        for (int i = min + 1; i <= max; i++)
        {
            _cal.add(Calendar.DAY_OF_MONTH, 1);
            x = (x + 1) % 7;
            if (x == 0) y++;
            rect = getRect(x, y);
            rects.add(rect);
            if (isToday())
            {
                displayCurrentDate(canvas);
                continue;
            }
            rectf = new RectF(rect);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(rectf, corner, corner, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText("" + i, rect.left + stepx / 2, rect.top + delta, paint);
            if (_cal.before(today))
            {
                canvas.drawLine(rect.left + c, rect.bottom - c, rect.right - c, rect.top + c, paint);
            }
            paint.setStyle(Paint.Style.FILL);

            if (calendarContentProvider.countAppointmentsForDay(personId, _cal.getTimeInMillis()) > 0)
            {
                int xa = (int) (rect.left + stepx / 4);
                int yi = (int) (rect.top + 5);
                paint.setColor(getResources().getColor(R.color.rvColor));
                canvas.drawCircle(xa, yi, 3, paint);
            }

            if (calendarContentProvider.countAilmentsForDay(personId, _cal.getTimeInMillis()) > 0) {
                int xa = (int) (rect.right - stepx / 4);
                int yi = (int) (rect.top + 5);
                paint.setColor(getResources().getColor(R.color.illnessColor));
                canvas.drawCircle(xa, yi, 3, paint);
            }

            if (calendarContentProvider.countMedicsForDay(personId, _cal.getTimeInMillis()) > 0) {
                int xa = (int) (rect.right - stepx / 4);
                int yi = (int) (rect.bottom - 5);
                paint.setColor(getResources().getColor(R.color.medicsColor));
                canvas.drawCircle(xa, yi, 3, paint);
            }

            if (calendarContentProvider.countMeasuresForDay(personId, _cal.getTimeInMillis()) > 0) {
                int xa = (int) (rect.right - stepx / 4);
                int yi = (int) (rect.bottom - 5);
                paint.setColor(getResources().getColor(R.color.measuresColor));
                canvas.drawCircle(xa, yi, 3, paint);
            }

            paint.setColor(getResources().getColor(R.color.regular_text_color));
        }
    }

    private void displayCurrentDate(Canvas canvas)
    {
        int day = _cal.get(Calendar.DAY_OF_MONTH);
        Rect rect = rects.get(day - 1);
        RectF rectf = new RectF(rect);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rectf, corner, corner, paint);
        paint.setColor(getResources().getColor(R.color.app_bg_color));
        canvas.drawText("" + day, rect.left + stepx / 2, rect.top + delta, paint);
        paint.setStyle(Paint.Style.FILL);
        if (calendarContentProvider.countAppointmentsForDay(personId, _cal.getTimeInMillis()) > 0)
        {
            int xa = (int) (rect.left + stepx / 4);
            int yi = (int) (rect.top + 5);
            paint.setColor(getResources().getColor(R.color.rvColor));
            canvas.drawCircle(xa, yi, 3, paint);
        }
        if (calendarContentProvider.countAilmentsForDay(personId, _cal.getTimeInMillis()) > 0) {
            int xa = (int) (rect.right - stepx / 4);
            int yi = (int) (rect.top + 5);
            paint.setColor(getResources().getColor(R.color.illnessColor));
            canvas.drawCircle(xa, yi, 3, paint);
        }
        if (calendarContentProvider.countMedicsForDay(personId, _cal.getTimeInMillis()) > 0) {
            int xa = (int) (rect.right - stepx / 4);
            int yi = (int) (rect.bottom - 5);
            paint.setColor(getResources().getColor(R.color.medicsColor));
            canvas.drawCircle(xa, yi, 3, paint);
        }
        if (calendarContentProvider.countMeasuresForDay(personId, _cal.getTimeInMillis()) > 0) {
            int xa = (int) (rect.right - stepx / 4);
            int yi = (int) (rect.bottom - 5);
            paint.setColor(getResources().getColor(R.color.measuresColor));
            canvas.drawCircle(xa, yi, 3, paint);
        }
        paint.setColor(getResources().getColor(R.color.regular_text_color));
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int xm;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                tx = (int) event.getX();
                ty = (int) event.getY();
                selectedRect = getSelectedRect(tx, ty);
                break;
            case MotionEvent.ACTION_MOVE:
                selectedRect = null;
                break;
            case MotionEvent.ACTION_UP:
                if (ty < yCalendar) {
                    xm = (int) event.getX();
                    int xdiff = xm - tx;
                    if (xdiff > 50)
                        _cal.add(Calendar.MONTH, -1);
                    else if (xdiff < -50)
                        _cal.add(Calendar.MONTH, 1);
                    tx = 0;
                    ty = 0;
                }
                else manageClick(tx, ty);
                selectedRect = null;
                break;
            default:
                selectedRect = null;
                break;
        }
        return true;
    }

    private Rect getSelectedRect(int x, int y)
    {
        for (Rect r : rects)
        {
            if (r.contains(x, y))
                return r;
        }
        return null;
    }

    private void highlightRect(Canvas canvas)
    {
        if (selectedRect == null) return;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.regular_text_color));
        RectF rectf = new RectF(selectedRect);
        canvas.drawRoundRect(rectf, corner, corner, paint);
    }

    private void manageClick(int x, int y)
    {
        if (selectedRect != null)
        {
            Intent intent = new Intent("com.akoudri.healthrecord.app.EditDay");
            int day_idx = rects.indexOf(selectedRect);
            intent.putExtra("personId", personId);
            int day = day_idx + 1;
            intent.putExtra("day", day);
            int month = _cal.get(Calendar.MONTH);
            intent.putExtra("month", month);
            int year = _cal.get(Calendar.YEAR);
            intent.putExtra("year", year);
            getContext().startActivity(intent);
        }
    }
}
