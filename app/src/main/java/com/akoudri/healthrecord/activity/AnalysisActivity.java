package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Measure;
import com.akoudri.healthrecord.utils.DatePickerFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.model.XYValueSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class AnalysisActivity extends Activity {

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId;

    private Spinner measureSpinner;
    private EditText startET, endET;
    private EditText nbPointsET;

    //TODO: Use to activate / deactivate display of charts
    int nbWeightMeasures, nbSizeMeasures, nbTemperatureMeasures, nbCpMeasures, nbGlucoseMeasures, nbHeartMeasures;

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
        nbPointsET = (EditText) findViewById(R.id.nb_points);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (personId == 0) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            nbWeightMeasures = dataSource.getWeightMeasureTable().getTotalMeasureCountForPerson(personId);
            nbSizeMeasures = dataSource.getSizeMeasureTable().getTotalMeasureCountForPerson(personId);
            nbTemperatureMeasures = dataSource.getTempMeasureTable().getTotalMeasureCountForPerson(personId);
            nbCpMeasures = dataSource.getCpMeasureTable().getTotalMeasureCountForPerson(personId);
            nbGlucoseMeasures = dataSource.getGlucoseMeasureTable().getTotalMeasureCountForPerson(personId);
            nbHeartMeasures = dataSource.getHeartMeasureTable().getTotalMeasureCountForPerson(personId);
        } catch (SQLException e) {
            e.printStackTrace();
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
        dfrag.init(this, startET);
        dfrag.show(getFragmentManager(),"Pick Analysis Start Date");
    }

    public void setAnalysisEndDate(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(this, endET);
        dfrag.show(getFragmentManager(),"Pick Analysis End Date");
    }

    public void showChart(View view)
    {
        if (personId == 0) return;
        if (!dataSourceLoaded) return;
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        Calendar start = HealthRecordUtils.stringToCalendar(startET.getText().toString());
        Calendar end = HealthRecordUtils.stringToCalendar(endET.getText().toString());
        //TODO: retrieve data according to the chosen type
        List<XYSeries> series = getSeries(start, end, Measure.WEIGHT_MEASURE_TYPE); //Arbitrary code
        if (series == null) return;
        dataset.addAllSeries(series);
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        initRenderer(renderer);
        Intent intent = ChartFactory.getLineChartIntent(this, dataset, renderer);
        startActivity(intent);
    }

    //FIXME: renderer shall be set for each type of measure, and not once only
    private void initRenderer(XYMultipleSeriesRenderer renderer) {
        renderer.setAxisTitleTextSize(12);
        renderer.setChartTitleTextSize(16);
        renderer.setLabelsTextSize(8);
        renderer.setLegendTextSize(8);
        renderer.setPointSize(3f);
        renderer.setMargins(new int[] {20, 20, 20, 20});
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.YELLOW);
        r.setPointStyle(PointStyle.CIRCLE);
        //XYSeriesRenderer.FillOutsideLine outsideLine = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BELOW);
        //outsideLine.setColor(Color.LTGRAY);
        //r.addFillOutsideLine(outsideLine);
        r.setFillPoints(true);
        renderer.addSeriesRenderer(r);
        renderer.setChartTitle("Weight Chart");
        renderer.setXTitle("x values");
        renderer.setYTitle("y values");
        renderer.setXAxisMin(0);
        renderer.setXAxisMax(5);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(5);
    }

    //FIXME: add time series instead for all types
    private List<XYSeries> getSeries(Calendar start, Calendar end, int type)
    {
        switch (type)
        {
            case Measure.WEIGHT_MEASURE_TYPE:
                return getWeightDataSet(start, end);
            case Measure.SIZE_MEASURE_TYPE:
                return getWeightDataSet(start, end);
            case Measure.TEMPERATURE_MEASURE_TYPE:
                return getWeightDataSet(start, end);
            case Measure.CP_MEASURE_TYPE:
                return getWeightDataSet(start, end);
            case Measure.GLUCOSE_MEASURE_TYPE:
                return getWeightDataSet(start, end);
            case Measure.HEART_MEASURE_TYPE:
                return getWeightDataSet(start, end);
            default:
                return null;
        }
    }

    private List<XYSeries> getWeightDataSet(Calendar start, Calendar end)
    {
        List<XYSeries> series = new ArrayList<XYSeries>();
        XYValueSeries w_series = new XYValueSeries("");
        //Arbitrary code
        w_series.add(0, 1);
        w_series.add(1, 3);
        w_series.add(2, 2);
        w_series.add(3, 4);
        series.add(w_series);
        return series;
    }

    private List<XYSeries>  getSizeDataSet(Calendar start, Calendar end)
    {
        return null;
    }

    private List<XYSeries>  getTempDataSet(Calendar start, Calendar end)
    {
        return null;
    }

    private List<XYSeries>  getCpDataSet(Calendar start, Calendar end)
    {
        return null;
    }

    private List<XYSeries>  getGlucoseDataSet(Calendar start, Calendar end)
    {
        return null;
    }

    private List<XYSeries>  getHeartDataSet(Calendar start, Calendar end)
    {
        //TODO 3 series to return
        return null;
    }

}
