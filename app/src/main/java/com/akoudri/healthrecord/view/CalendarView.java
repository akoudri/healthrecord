package com.akoudri.healthrecord.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.akoudri.healthrecord.activity.CalendarActivity;
import com.akoudri.healthrecord.activity.EditDayActivity;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ali Koudri on 27/04/14.
 * Implements a calendar view for health record
 * STATUS: to check more accurately with schema
 */
public class CalendarView extends View implements View.OnTouchListener {

    private int personId = 0; //id of the person
    private Calendar _cal, today; //_cal is used to iterate over the days of the calendar, today represents the current day
    private Paint paint; //for drawing on the canvas
    private int width, height; //to store the dimension of the screen
    private String[] daysOfWeek; //to display on the screen
    private List<Rect> rects; //list of coordinates representing the days
    private float ratio; //used to adapt sizes between various screen configurations
    //private int titleSize; //absolute size of the month.year
    //private int tableTitleSize; //absolute size of the days
    private float tsize, ttsize; //relative size of the month.year and days
    private int corner; //radius of the rectangles
    private float nbRatio; //used to calculate delta
    //private float delta; //internal vertical space in the cells
    private float stepx, stepy; //used to display the cells of the calendar -> 7x6 cells
    private float yCalendar; //y coordinate for the first row of the calendar
    private Rect selectedRect = null; //references the cell that has been touched by the user

    private CalendarContentProvider calendarContentProvider;

    //used to manage navigation between months
    private int tx = 0;
    private int ty = 0;

    //min and max days of month for current month
    private int min_day, max_day;
    //store whether the days of the month are before the actual date
    private boolean[] isBeforeActualDate;

    //Information
    private int[] appointments;
    private int[] ailments;
    private int[] measures;
    private int[] medics;
    private int[] observations;

    //Icons
    private Bitmap illnessIco, measureIco, medicsIco, rvIco;
    //private Bitmap obsIco;
    private boolean imagesFound = true;
    private int imgSize;

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //Icons
        AssetManager assetManager = context.getAssets();
        InputStream inputStream;
        try {
            inputStream = assetManager.open("images/measure_ico.png");
            measureIco = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            inputStream = assetManager.open("images/rv_ico.png");
            rvIco = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            inputStream = assetManager.open("images/illness_ico.png");
            illnessIco = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            inputStream = assetManager.open("images/medics_ico.png");
            medicsIco = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            //inputStream = assetManager.open("images/eye_ico.png");
            //obsIco = BitmapFactory.decodeStream(inputStream);
            //inputStream.close();
        } catch (IOException e)
        {
            imagesFound = false;
        }
        //Metrics
        //titleSize = 24;
        //tableTitleSize = 12;
        corner = (int) HealthRecordUtils.convertPixelsToDp(5, context);
        nbRatio = HealthRecordUtils.convertPixelsToDp(0.3f, context);
        //Paint
        paint = new Paint();//set paint object
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(16.0f * ratio);
        //Actual and reference dates
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
        loadInfo();
    }

    public void setWidth(int width)
    {
        this.width = width;
        this.height = (int) (width * 1.618033989);
        //ratio = width/480.0f;
        ratio = HealthRecordUtils.getDisplayRatio((CalendarActivity)calendarContentProvider);
        stepx = width / 7;
        tsize = 24 * ratio;
        ttsize = 16 * ratio;
        yCalendar = 3 * (tsize + ttsize) + 10;
        stepy = (height - yCalendar) / 6;
        //delta = stepy * (nbRatio + 1) / 2;
        imgSize = (int) (16 * ratio);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        computeRects();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (personId == 0) return; //nothing to display
        canvas.drawColor(getResources().getColor(R.color.app_bg_color)); //set bg to app bg
        //The order of operation calls is very important!!!
        displayTitleDate(canvas);
        displayDaysOfTheWeek(canvas);
        displayDaysOfMonth(canvas);
        displayInformation(canvas);
        highlightRect(canvas);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(width, height);
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
        //prepare the painting for days different of actual day
        paint.setColor(getResources().getColor(R.color.regular_text_color));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((int) (stepy * nbRatio));
        paint.setFakeBoldText(true);
        paint.setColor(getResources().getColor(R.color.regular_text_color));
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
        int left = (int) (x * stepx);
        int top = (int) (yCalendar + y * stepy);
        int right = (int) (left + stepx);
        int bottom = (int) (top + stepy);
        return new Rect(left, top, right, bottom);
    }

    private void computeRects()
    {
        rects.clear();
        min_day = _cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        max_day = _cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        isBeforeActualDate = new boolean[max_day];
        for (boolean b : isBeforeActualDate)
            b = false;
        //set the calendar to the first day of the month and compute first x
        _cal.set(Calendar.DAY_OF_MONTH, min_day);
        if (_cal.before(today))
            isBeforeActualDate[0] = true;
        int firstDay = _cal.get(Calendar.DAY_OF_WEEK);
        int firstx = (firstDay + 5) % 7;
        //used to draw rects
        Rect rect;
        int x = firstx;
        int y = 0;
        rect = getRect(x, y);
        rects.add(rect);
        for (int i = min_day + 1; i <= max_day; i++) {
            _cal.add(Calendar.DAY_OF_MONTH, 1);
            if (_cal.before(today))
                isBeforeActualDate[i-1] = true;
            x = (x + 1) % 7;
            if (x == 0) y++;
            rect = getRect(x, y);
            rects.add(rect);
        }
    }

    private void displayDaysOfMonth(Canvas canvas)
    {
        Rect rect, b_rect;
        RectF rectf;
        int day = 1;
        int c = corner; //for the oblique line
        b_rect = new Rect();
        for (int i = 0; i < max_day; i++)
        {
            rect = rects.get(i);
            b_rect.left = rect.left + 2;
            b_rect.top = rect.top + 2;
            b_rect.right = rect.right - 2;
            b_rect.bottom = rect.bottom - 2;
            rectf = new RectF(b_rect);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(rectf, corner, corner, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText("" + day, (rect.right + rect.left) / 2, (rect.bottom + rect.top) / 2 + ttsize / 3, paint);
            if (isBeforeActualDate[i])
                canvas.drawLine(rect.left + c, rect.bottom - c, rect.right - c, rect.top + c, paint);
            paint.setStyle(Paint.Style.FILL);
            day++;
        }
    }

    private void displayInformation(Canvas canvas)
    {
        Rect rect;
        int sx = 3;
        int sy = 3;
        int rcirc = (int) (16 * ratio);
        Rect b_rect = new Rect();
        if (imagesFound) {
            for (int i = min_day; i <= max_day; i++) {
                rect = rects.get(i - 1);
                if (measures[i - 1] > 0) {
                    b_rect.left = rect.left + sx;
                    b_rect.right = rect.left + sx + imgSize;
                    b_rect.top = rect.top + sy;
                    b_rect.bottom = rect.top + sy + imgSize;
                    canvas.drawBitmap(measureIco, null, b_rect, null);
                }
                if (appointments[i - 1] > 0) {
                    b_rect.left = rect.right - sx - imgSize;
                    b_rect.right = rect.right - sx;
                    b_rect.top = rect.top + sy;
                    b_rect.bottom = rect.top + sy + imgSize;
                    canvas.drawBitmap(rvIco, null, b_rect, null);
                }
                if (ailments[i - 1] > 0) {
                    b_rect.left = rect.left + sx;
                    b_rect.right = rect.left + sx + imgSize;
                    b_rect.top = rect.bottom - sy - imgSize;
                    b_rect.bottom = rect.bottom - sy;
                    canvas.drawBitmap(illnessIco, null, b_rect, null);
                }
                if (medics[i - 1] > 0) {
                    b_rect.left = rect.right - sx - imgSize;
                    b_rect.right = rect.right - sx;
                    b_rect.top = rect.bottom - sy - imgSize;
                    b_rect.bottom = rect.bottom - sy;
                    canvas.drawBitmap(medicsIco, null, b_rect, null);
                }
                if (observations[i - 1] > 0) {
                    int xa = (rect.right + rect.left) / 2;
                    int yi = (rect.bottom + rect.top) / 2;
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(getResources().getColor(R.color.measuresColor));
                    canvas.drawCircle(xa, yi, rcirc, paint);
                    paint.setStyle(Paint.Style.FILL);
                }
            }
        }
        else
        {
            for (int i = min_day; i <= max_day; i++) {
                rect = rects.get(i - 1);
                if (measures[i - 1] > 0) {
                    int xa = rect.left + sx + rcirc;
                    int yi = rect.top + sy + rcirc;
                    paint.setColor(getResources().getColor(R.color.measuresColor));
                    canvas.drawCircle(xa, yi, rcirc, paint);
                }
                if (appointments[i - 1] > 0) {
                    int xa = rect.right - sx  - rcirc;
                    int yi = rect.top + sy  + rcirc;
                    paint.setColor(getResources().getColor(R.color.rvColor));
                    canvas.drawCircle(xa, yi, rcirc, paint);
                }
                if (ailments[i - 1] > 0) {
                    int xa = rect.left + sx  + rcirc;
                    int yi = rect.bottom - sy  - rcirc;
                    paint.setColor(getResources().getColor(R.color.illnessColor));
                    canvas.drawCircle(xa, yi, rcirc, paint);
                }
                if (medics[i - 1] > 0) {
                    int xa = rect.right - sx - rcirc;
                    int yi = rect.bottom - sy  - rcirc;
                    paint.setColor(getResources().getColor(R.color.medicsColor));
                    canvas.drawCircle(xa, yi, rcirc, paint);
                }
                if (observations[i - 1] > 0) {
                    int xa = (rect.right + rect.left) / 2;
                    int yi = (rect.bottom + rect.top) / 2;
                    paint.setColor(getResources().getColor(R.color.obsColor));
                    canvas.drawCircle(xa, yi, rcirc, paint);
                }
            }
        }
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
            case MotionEvent.ACTION_UP:
                xm = (int) event.getX();
                int xdiff = xm - tx;
                if (xdiff > 50) {
                    _cal.add(Calendar.MONTH, -1);
                    computeRects();
                    loadInfo();
                }
                else if (xdiff < -50) {
                    _cal.add(Calendar.MONTH, 1);
                    computeRects();
                    loadInfo();
                }
                else {
                    manageClick();
                }
                tx = 0;
                ty = 0;
                selectedRect = null;
        }
        return true;
    }

    private void loadInfo()
    {
        appointments = calendarContentProvider.getMonthAppointmentsForPerson(personId, _cal);
        ailments = calendarContentProvider.getMonthAilmentsForPerson(personId, _cal);
        measures = calendarContentProvider.getMonthMeasuresForPerson(personId, _cal);
        medics = calendarContentProvider.getMonthMedicationsForPerson(personId, _cal);
        observations = calendarContentProvider.getMonthObservationsForPerson(personId, _cal);
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
        paint.setColor(getResources().getColor(R.color.button_pressed_bg_color));
        Rect b_rect = new Rect(selectedRect.left + 2, selectedRect.top + 2, selectedRect.right - 2, selectedRect.bottom - 2);
        RectF rectf = new RectF(b_rect);
        canvas.drawRoundRect(rectf, corner, corner, paint);
    }

    private void manageClick()
    {
        if (selectedRect != null)
        {
            Intent intent = new Intent((CalendarActivity)calendarContentProvider, EditDayActivity.class);
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
