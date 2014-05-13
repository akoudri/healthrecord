package com.akoudri.healthrecord.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.akoudri.healthrecord.app.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ali Koudri on 27/04/14.
 */
public class CalendarView extends View implements View.OnTouchListener {

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
    private Bitmap next, previous;
    private Rect[] nav = new Rect[2];
    private Rect selectedRect = null;

    //TODO: add icons in the cells
    //TODO: manage clicks graphically

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        _cal = Calendar.getInstance();
        today = Calendar.getInstance();
        rects = new ArrayList<Rect>();
        initDaysOfWeek();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream;
        try {
            inputStream = assetManager.open("images/next.png");
            next = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            inputStream = assetManager.open("images/previous.png");
            previous = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setOnTouchListener(this);
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
        displayNavigation(canvas);
        displayDays(canvas);
        displayNumbers(canvas);
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
        //FIXME: compute the right height
        height = (int) (size.x * 1.5);
        setMeasuredDimension(width, height);
    }

    private boolean isToday()
    {
        return  (today.get(Calendar.YEAR) == _cal.get(Calendar.YEAR)) &&
            (today.get(Calendar.MONTH) == _cal.get(Calendar.MONTH)) &&
            (today.get(Calendar.DATE) == _cal.get(Calendar.DATE));
    }

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
        }
    }

    private void displayCurrentDate(Canvas canvas)
    {
        int day = _cal.get(Calendar.DAY_OF_MONTH);
        paint.setColor(getResources().getColor(R.color.title_button_text_color));
        paint.setStrokeWidth(3.0f);
        Rect rect = rects.get(day - 1);
        RectF rectf = new RectF(rect);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRoundRect(rectf, corner, corner, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("" + day, rect.left + stepx / 2, rect.top + delta, paint);
        paint.setColor(getResources().getColor(R.color.regular_text_color));
        paint.setStrokeWidth(2.0f);
    }

    private void displayNavigation(Canvas canvas)
    {
        if (next == null && previous == null)
            return;
        int left = 20;
        int top = (int) tsize;
        int right = (int) (left + tsize);
        int bottom = (int) (top + tsize);
        nav[0] = new Rect(left, top, right, bottom);
        left = width - 20 - (int) tsize;
        right = (int) (left + tsize);
        nav[1] = new Rect(left, top, right, bottom);
        canvas.drawBitmap(previous, null, nav[0], null);
        canvas.drawBitmap(next, null, nav[1], null);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                selectedRect = getSelectedRect(x, y);
                break;
            case MotionEvent.ACTION_UP:
                manageClick(x, y);
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
        if (nav[0].contains(x, y))
            _cal.add(Calendar.MONTH, -1);
        if (nav[1].contains(x, y))
            _cal.add(Calendar.MONTH, 1);
        if (selectedRect != null)
        {
            Intent intent = new Intent("com.akoudri.healthrecord.app.EditDay");
            //TODO: retrieve current date to pass to the activity
            getContext().startActivity(intent);
        }
    }
}
