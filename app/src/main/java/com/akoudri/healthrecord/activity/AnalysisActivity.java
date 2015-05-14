package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.CholesterolMeasure;
import com.akoudri.healthrecord.data.CranialPerimeterMeasure;
import com.akoudri.healthrecord.data.GlucoseMeasure;
import com.akoudri.healthrecord.data.HeartMeasure;
import com.akoudri.healthrecord.data.Measure;
import com.akoudri.healthrecord.data.SizeMeasure;
import com.akoudri.healthrecord.data.TemperatureMeasure;
import com.akoudri.healthrecord.data.WeightMeasure;
import com.akoudri.healthrecord.utils.DatePickerFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//STATUS: checked
public class AnalysisActivity extends Activity {

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId;

    private Spinner measureSpinner;
    private EditText startET, endET;

    private int tsize, ttsize, psize, margin_left_right, margin_bottom_top;
    private int[] margins;
    //private int ratio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_analysis);
        personId = getIntent().getIntExtra("personId", 0);
        dataSource = HealthRecordDataSource.getInstance(this);
        measureSpinner = (Spinner) findViewById(R.id.measure_choice);
        String[] measureChoices = getResources().getStringArray(R.array.measures);
        ArrayAdapter<String> measureChoicesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, measureChoices);
        measureSpinner.setAdapter(measureChoicesAdapter);
        startET = (EditText) findViewById(R.id.start_measure);
        startET.setKeyListener(null);
        endET = (EditText) findViewById(R.id.end_measure);
        endET.setKeyListener(null);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        //ratio = size.x / 480;
        tsize = (int) HealthRecordUtils.convertPixelsToDp(16, this);
        ttsize = (int) HealthRecordUtils.convertPixelsToDp(12, this);
        psize = (int) HealthRecordUtils.convertPixelsToDp(2, this);
        margin_left_right = (int) HealthRecordUtils.convertPixelsToDp(5, this);
        margin_bottom_top = (int) HealthRecordUtils.convertPixelsToDp(20, this);
        margins = new int[] {margin_left_right, margin_bottom_top, margin_bottom_top, margin_left_right};
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (personId == 0) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
        } catch (SQLException e) {
            Toast.makeText(this, getResources().getString(R.string.database_access_impossible), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (personId == 0) return;
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
    }

    public void setAnalysisStartDate(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        Calendar c = Calendar.getInstance();
        dfrag.init(this, startET, c, null, c);
        dfrag.show(getFragmentManager(), "Pick Analysis Start Date");
    }

    public void setAnalysisEndDate(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        Calendar c = Calendar.getInstance();
        dfrag.init(this, endET, c, null, c);
        dfrag.show(getFragmentManager(), "Pick Analysis End Date");
    }

    public void showChart(View view)
    {
        if (personId == 0) return;
        if (!dataSourceLoaded) return;
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        String sDate = startET.getText().toString();
        String eDate = endET.getText().toString();
        Calendar start, end;
        if (sDate.equals("")) {
            start = Calendar.getInstance();
            start.setTimeInMillis(0L);
        } else {
            start = HealthRecordUtils.stringToCalendar(startET.getText().toString());
        }
        if (eDate.equals(""))
            end = Calendar.getInstance();
        else end = HealthRecordUtils.stringToCalendar(endET.getText().toString());
        List<XYSeries> series = null;
        XYMultipleSeriesRenderer renderer = null;
        switch (measureSpinner.getSelectedItemPosition())
        {
            case 1:
                series = getSeries(start, end, Measure.WEIGHT_MEASURE_TYPE);
                renderer = getSimpleRenderer();
                break;
            case 2:
                series = getSeries(start, end, Measure.SIZE_MEASURE_TYPE);
                renderer = getSimpleRenderer();
                break;
            case 3:
                series = getSeries(start, end, Measure.TEMPERATURE_MEASURE_TYPE);
                renderer = getSimpleRenderer();
                break;
            case 4:
                series = getSeries(start, end, Measure.CP_MEASURE_TYPE);
                renderer = getSimpleRenderer();
                break;
            case 5:
                series = getSeries(start, end, Measure.GLUCOSE_MEASURE_TYPE);
                renderer = getSimpleRenderer();
                break;
            case 6:
                series = getSeries(start, end, Measure.HEART_MEASURE_TYPE);
                renderer = getHeartRenderer();
                break;
            case 7:
                series = getSeries(start, end, Measure.CHOLESTEROL_MEASURE_TYPE);
                renderer = getCholesterolRenderer();
                break;
        }
        if (series == null)
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.choose_type), Toast.LENGTH_SHORT).show();
            return;
        }
        dataset.addAllSeries(series);
        Intent intent = ChartFactory.getTimeChartIntent(this, dataset, renderer, null);
        startActivity(intent);
    }

    private List<XYSeries> getSeries(Calendar start, Calendar end, int type)
    {
        switch (type)
        {
            case Measure.WEIGHT_MEASURE_TYPE:
                return getWeightDataSet(start, end);
            case Measure.SIZE_MEASURE_TYPE:
                return getSizeDataSet(start, end);
            case Measure.TEMPERATURE_MEASURE_TYPE:
                return getTempDataSet(start, end);
            case Measure.CP_MEASURE_TYPE:
                return getCpDataSet(start, end);
            case Measure.GLUCOSE_MEASURE_TYPE:
                return getGlucoseDataSet(start, end);
            case Measure.HEART_MEASURE_TYPE:
                return getHeartDataSet(start, end);
            case Measure.CHOLESTEROL_MEASURE_TYPE:
                return getCholesterolDataSet(start, end);
            default:
                return null;
        }
    }

    private List<XYSeries> getWeightDataSet(Calendar start, Calendar end)
    {
        List<WeightMeasure> measures = dataSource.getWeightMeasureTable().getMeasuresInInterval(start, end);
        if (measures.isEmpty()) return null;
        List<XYSeries> series = new ArrayList<XYSeries>();
        TimeSeries w_series = new TimeSeries(getResources().getString(R.string.weight));
       for (WeightMeasure measure : measures)
       {
           w_series.add(HealthRecordUtils.datehourToCalendar(measure.getDate(), measure.getHour()).getTime(), measure.getValue());
       }
        series.add(w_series);
        return series;
    }

    private List<XYSeries> getSizeDataSet(Calendar start, Calendar end)
    {
        List<SizeMeasure> measures = dataSource.getSizeMeasureTable().getMeasuresInInterval(start, end);
        if (measures.isEmpty()) return null;
        List<XYSeries> series = new ArrayList<XYSeries>();
        TimeSeries s_series = new TimeSeries(getResources().getString(R.string.weight));
        for (SizeMeasure measure : measures)
        {
            s_series.add(HealthRecordUtils.datehourToCalendar(measure.getDate(), measure.getHour()).getTime(), measure.getValue());
        }
        series.add(s_series);
        return series;
    }

    private List<XYSeries> getTempDataSet(Calendar start, Calendar end)
    {
        List<TemperatureMeasure> measures = dataSource.getTempMeasureTable().getMeasuresInInterval(start, end);
        if (measures.isEmpty()) return null;
        List<XYSeries> series = new ArrayList<XYSeries>();
        TimeSeries t_series = new TimeSeries(getResources().getString(R.string.weight));
        for (TemperatureMeasure measure : measures)
        {
            t_series.add(HealthRecordUtils.datehourToCalendar(measure.getDate(), measure.getHour()).getTime(), measure.getValue());
        }
        series.add(t_series);
        return series;
    }

    private List<XYSeries> getCpDataSet(Calendar start, Calendar end)
    {
        List<CranialPerimeterMeasure> measures = dataSource.getCpMeasureTable().getMeasuresInInterval(start, end);
        if (measures.isEmpty()) return null;
        List<XYSeries> series = new ArrayList<XYSeries>();
        TimeSeries cp_series = new TimeSeries(getResources().getString(R.string.weight));
        for (CranialPerimeterMeasure measure : measures)
        {
            cp_series.add(HealthRecordUtils.datehourToCalendar(measure.getDate(), measure.getHour()).getTime(), measure.getValue());
        }
        series.add(cp_series);
        return series;
    }

    private List<XYSeries> getGlucoseDataSet(Calendar start, Calendar end)
    {
        List<GlucoseMeasure> measures = dataSource.getGlucoseMeasureTable().getMeasuresInInterval(start, end);
        if (measures.isEmpty()) return null;
        List<XYSeries> series = new ArrayList<XYSeries>();
        TimeSeries g_series = new TimeSeries(getResources().getString(R.string.weight));
        for (GlucoseMeasure measure : measures)
        {
            g_series.add(HealthRecordUtils.datehourToCalendar(measure.getDate(), measure.getHour()).getTime(), measure.getValue());
        }
        series.add(g_series);
        return series;
    }

    private List<XYSeries>  getHeartDataSet(Calendar start, Calendar end)
    {
        List<HeartMeasure> measures = dataSource.getHeartMeasureTable().getMeasuresInInterval(start, end);
        if (measures.isEmpty()) return null;
        List<XYSeries> series = new ArrayList<XYSeries>();
        TimeSeries d_series = new TimeSeries(getResources().getString(R.string.diastolic));
        TimeSeries s_series = new TimeSeries(getResources().getString(R.string.systolic));
        TimeSeries p_series = new TimeSeries(getResources().getString(R.string.heartbeat));
        for (HeartMeasure measure : measures)
        {
            d_series.add(HealthRecordUtils.datehourToCalendar(measure.getDate(), measure.getHour()).getTime(), measure.getDiastolic());
            s_series.add(HealthRecordUtils.datehourToCalendar(measure.getDate(), measure.getHour()).getTime(), measure.getSystolic());
            p_series.add(HealthRecordUtils.datehourToCalendar(measure.getDate(), measure.getHour()).getTime(), measure.getHeartbeat());
        }
        series.add(d_series);
        series.add(s_series);
        series.add(p_series);
        return series;
    }

    private List<XYSeries> getCholesterolDataSet(Calendar start, Calendar end) {
        List<CholesterolMeasure> measures = dataSource.getCholesterolMeasureTable().getMeasuresInInterval(start, end);
        if (measures.isEmpty()) return null;
        List<XYSeries> series = new ArrayList<XYSeries>();
        TimeSeries t_series = new TimeSeries(getResources().getString(R.string.total));
        TimeSeries h_series = new TimeSeries(getResources().getString(R.string.hdl));
        TimeSeries l_series = new TimeSeries(getResources().getString(R.string.ldl));
        TimeSeries tg_series = new TimeSeries(getResources().getString(R.string.triglycerides));
        for (CholesterolMeasure measure : measures)
        {
            t_series.add(HealthRecordUtils.datehourToCalendar(measure.getDate(), measure.getHour()).getTime(), measure.getTotal());
            h_series.add(HealthRecordUtils.datehourToCalendar(measure.getDate(), measure.getHour()).getTime(), measure.getHDL());
            l_series.add(HealthRecordUtils.datehourToCalendar(measure.getDate(), measure.getHour()).getTime(), measure.getLDL());
            tg_series.add(HealthRecordUtils.datehourToCalendar(measure.getDate(), measure.getHour()).getTime(), measure.getTriglycerides());
        }
        series.add(t_series);
        series.add(h_series);
        series.add(l_series);
        series.add(tg_series);
        return series;
    }

    private XYMultipleSeriesRenderer getSimpleRenderer()
    {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(tsize);
        renderer.setChartTitleTextSize(tsize);
        renderer.setLabelsTextSize(ttsize);
        renderer.setLegendTextSize(ttsize);
        renderer.setPointSize(psize);
        renderer.setMargins(margins);
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.YELLOW);
        r.setPointStyle(PointStyle.CIRCLE);
        r.setFillPoints(true);
        renderer.addSeriesRenderer(r);
        return renderer;
    }

    private XYMultipleSeriesRenderer getHeartRenderer()
    {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(tsize);
        renderer.setChartTitleTextSize(tsize);
        renderer.setLabelsTextSize(ttsize);
        renderer.setLegendTextSize(ttsize);
        renderer.setPointSize(psize);
        renderer.setMargins(margins);
        XYSeriesRenderer r1 = new XYSeriesRenderer();
        r1.setColor(Color.YELLOW);
        r1.setPointStyle(PointStyle.CIRCLE);
        r1.setFillPoints(true);
        renderer.addSeriesRenderer(r1);
        XYSeriesRenderer r2 = new XYSeriesRenderer();
        r2.setColor(Color.RED);
        r2.setPointStyle(PointStyle.TRIANGLE);
        r2.setFillPoints(true);
        renderer.addSeriesRenderer(r2);
        XYSeriesRenderer r3 = new XYSeriesRenderer();
        r3.setColor(Color.BLUE);
        r3.setPointStyle(PointStyle.SQUARE);
        r3.setFillPoints(true);
        renderer.addSeriesRenderer(r3);
        return renderer;
    }

    private XYMultipleSeriesRenderer getCholesterolRenderer()
    {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(tsize);
        renderer.setChartTitleTextSize(tsize);
        renderer.setLabelsTextSize(ttsize);
        renderer.setLegendTextSize(ttsize);
        renderer.setPointSize(psize);
        renderer.setMargins(margins);
        XYSeriesRenderer r1 = new XYSeriesRenderer();
        r1.setColor(Color.YELLOW);
        r1.setPointStyle(PointStyle.CIRCLE);
        r1.setFillPoints(true);
        renderer.addSeriesRenderer(r1);
        XYSeriesRenderer r2 = new XYSeriesRenderer();
        r2.setColor(Color.RED);
        r2.setPointStyle(PointStyle.TRIANGLE);
        r2.setFillPoints(true);
        renderer.addSeriesRenderer(r2);
        XYSeriesRenderer r3 = new XYSeriesRenderer();
        r3.setColor(Color.BLUE);
        r3.setPointStyle(PointStyle.SQUARE);
        r3.setFillPoints(true);
        renderer.addSeriesRenderer(r3);
        XYSeriesRenderer r4 = new XYSeriesRenderer();
        r4.setColor(Color.GREEN);
        r4.setPointStyle(PointStyle.DIAMOND);
        r4.setFillPoints(true);
        renderer.addSeriesRenderer(r4);
        return renderer;
    }

}
