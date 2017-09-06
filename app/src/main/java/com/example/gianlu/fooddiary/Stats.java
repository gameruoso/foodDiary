package com.example.gianlu.fooddiary;

import android.database.Cursor;
import android.graphics.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.*;


public class Stats extends AppCompatActivity {

    private XYPlot plot;
    DatabaseHelper app_db;
    Utils app_utils;

    CheckBox checkBoxOverall;
    CheckBox checkBoxBreakfast;
    CheckBox checkBoxLunch;
    CheckBox checkBoxDinner;
    CheckBox checkBoxOther;
    TextView textViewTop5WorstFood;
    Spinner spinnerStatsTimerange;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        app_db = new DatabaseHelper(this);
        app_utils = new Utils();

        checkBoxOverall = (CheckBox)findViewById(R.id.checkBoxOverall);
        checkBoxBreakfast = (CheckBox)findViewById(R.id.checkBoxBreakfast);
        checkBoxLunch = (CheckBox)findViewById(R.id.checkBoxLunch);
        checkBoxDinner = (CheckBox)findViewById(R.id.checkBoxDinner);
        checkBoxOther = (CheckBox)findViewById(R.id.checkBoxOther);
        textViewTop5WorstFood = (TextView)findViewById(R.id.textViewTop5WorstFood);
        plot = (XYPlot) findViewById(R.id.plot);
        spinnerStatsTimerange = (Spinner) findViewById(R.id.spinnerStatsTimerange);

        // setup spinner
//      // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinnerStatsTimerangeArray, android.R.layout.simple_spinner_item);
//      Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//      Apply the adapter to the spinner
        spinnerStatsTimerange.setAdapter(adapter);

        spinnerChangeTimeWindow();

        drawPlot();

    }


    private void drawPlot(){

        Integer timeseriesDayRollingWindow = getSpinnerUserSelection();


        // create dates and days arrays for X axes
        final Number[] xLabelsDays = app_utils.getLastNDays(timeseriesDayRollingWindow, "day");
        final Number[] xLabelsDates = app_utils.getLastNDays(timeseriesDayRollingWindow, "date");

        // create overall rating array
        Number[] overallRating = builOverallRatingArray(xLabelsDates);
        // create breakfast rating array
        Number[] breakfastRating = buildBreakfastRatingArray(xLabelsDates);
        // create breakfast rating array
        Number[] lunchRating = buildLunchRatingArray(xLabelsDates);
        // create breakfast rating array
        Number[] dinnerRating = buildDinnerRatingArray(xLabelsDates);
        // create breakfast rating array
        Number[] otherRating = buildOtherRatingArray(xLabelsDates);

        Object[] allRatings = {overallRating,breakfastRating,lunchRating,dinnerRating,otherRating};

        // initialize our XYPlot reference:
        setRangePlotAxes(allRatings);
        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(xLabelsDays[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });


        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        XYSeries overallRatingSeries = new SimpleXYSeries(
                Arrays.asList(overallRating), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "overall");
        XYSeries breakfastRatingSeries = new SimpleXYSeries(
                Arrays.asList(breakfastRating), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "breakfast");
        XYSeries lunchRatingSeries = new SimpleXYSeries(
                Arrays.asList(lunchRating), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "lunch");
        XYSeries dinnerRatingSeries = new SimpleXYSeries(
                Arrays.asList(dinnerRating), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "dinner");
        XYSeries otherRatingSeries = new SimpleXYSeries(
                Arrays.asList(otherRating), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "other");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter overallRatingFormat =
                new LineAndPointFormatter(this, R.xml.line_point_formatter_overall);

        LineAndPointFormatter breakfastRatingFormat =
                new LineAndPointFormatter(this, R.xml.line_point_formatter_foodtypes);
        LineAndPointFormatter lunchRatingFormat =
                new LineAndPointFormatter(this, R.xml.line_point_formatter_foodtypes);
        LineAndPointFormatter dinnerRatingFormat =
                new LineAndPointFormatter(this, R.xml.line_point_formatter_foodtypes);
        LineAndPointFormatter otherRatingFormat =
                new LineAndPointFormatter(this, R.xml.line_point_formatter_foodtypes);

        // setup series colours:
        breakfastRatingFormat.getLinePaint().setColor(Color.parseColor("#011f4b"));
        lunchRatingFormat.getLinePaint().setColor(Color.parseColor("#00aedb"));
        dinnerRatingFormat.getLinePaint().setColor(Color.parseColor("#f37735"));
        otherRatingFormat.getLinePaint().setColor(Color.parseColor("#ffc425"));

        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        overallRatingFormat.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        breakfastRatingFormat.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        lunchRatingFormat.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        dinnerRatingFormat.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        otherRatingFormat.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


//         add overall series' to the xyplot:
        plot.addSeries(overallRatingSeries, overallRatingFormat);


        addListenerOnCheckBoxOverall(overallRatingSeries, overallRatingFormat);
        addListenerOnCheckBoxBreakfast(breakfastRatingSeries, breakfastRatingFormat);
        addListenerOnCheckBoxLunch(lunchRatingSeries, lunchRatingFormat);
        addListenerOnCheckBoxDinner(dinnerRatingSeries, dinnerRatingFormat);
        addListenerOnCheckBoxOther(otherRatingSeries, otherRatingFormat);

    }

    private void addListenerOnCheckBoxOverall(final XYSeries series, final XYSeriesFormatter format) {

        checkBoxOverall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is checkBoxOverall checked?
                if (checkBoxOverall.isChecked()) {
                    plot.addSeries(series, format);
                    plot.redraw();
                }
                if (!checkBoxOverall.isChecked()) {
                    plot.removeSeries(series);
                    plot.redraw();
                }

            }
        });

    }
    private void addListenerOnCheckBoxBreakfast(final XYSeries series, final XYSeriesFormatter format) {

        checkBoxBreakfast.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is checkBoxBreakfast checked?
                if (checkBoxBreakfast.isChecked()) {
                    plot.addSeries(series, format);
                    plot.redraw();
                }
                if (!checkBoxBreakfast.isChecked()) {
                    plot.removeSeries(series);
                    plot.redraw();
                }

            }
        });

    }
    private void addListenerOnCheckBoxLunch(final XYSeries series, final XYSeriesFormatter format) {

        checkBoxLunch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is checkBoxLunch checked?
                if (checkBoxLunch.isChecked()) {
                    plot.addSeries(series, format);
                    plot.redraw();
                }
                if (!checkBoxLunch.isChecked()) {
                    plot.removeSeries(series);
                    plot.redraw();
                }

            }
        });

    }
    private void addListenerOnCheckBoxDinner(final XYSeries series, final XYSeriesFormatter format) {

        checkBoxDinner.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is checkBoxDinner checked?
                if (checkBoxDinner.isChecked()) {
                    plot.addSeries(series, format);
                    plot.redraw();
                }
                if (!checkBoxDinner.isChecked()) {
                    plot.removeSeries(series);
                    plot.redraw();
                }

            }
        });

    }
    private void addListenerOnCheckBoxOther(final XYSeries series, final XYSeriesFormatter format) {

        checkBoxOther.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //is checkBoxOther checked?
                if (checkBoxOther.isChecked()) {
                    plot.addSeries(series, format);
                    plot.redraw();
                }
                if (!checkBoxOther.isChecked()) {
                    plot.removeSeries(series);
                    plot.redraw();
                }

            }
        });

    }



    private Number[] fillMissingDatesWithPeriodAverage(Number[] plotDatesArray, Number[] dbDatesArray, Number[] dbMetricArray){

        Number[] res = new Number[plotDatesArray.length];

        Float[] dbMetricArrayFloat = new Float[dbMetricArray.length];
        dbMetricArrayFloat = (Float[]) dbMetricArray;

        float sum = 0;

        for(int i=0; i < dbMetricArray.length ; i++)
            sum = sum + dbMetricArrayFloat[i];

        float average = sum / dbMetricArray.length;
        float periodAvgRating = app_utils.roundFloatToDecimals(average, 1);
        
        // iterate over the dates to display: put rating=periodAvgRating if the date is missing in the db
        Number currDate;
        int dbEntryIndex = 0;
        for (int x = 0; x < plotDatesArray.length; x++) {

            currDate = plotDatesArray[x];


            if (Arrays.asList(dbDatesArray).contains(currDate)) {
                res[x] = dbMetricArray[dbEntryIndex];
                dbEntryIndex++;
            }
            else
                res[x] = periodAvgRating;
        }

        return res;
    }
    private void setRangePlotAxes(Object[] ratingArrays){

        float minvalue = 5;
        float maxvalue = 0;

        for (int i=0; i<ratingArrays.length; i++){
            Number[] currRatingArray = (Number[])ratingArrays[i];

            for (int x=0; x<currRatingArray.length; x++){
                if ((float)currRatingArray[x] < minvalue)
                    minvalue = (float)currRatingArray[x];
                if ((float)currRatingArray[x] > maxvalue)
                    maxvalue = (float)currRatingArray[x];

            }
        }

        int roundDownMinvalue = (int)minvalue;
        int roundUpMaxvalue = (int) Math. ceil(maxvalue);

        plot.setRangeBoundaries(roundDownMinvalue, roundUpMaxvalue + 0.5, BoundaryMode.FIXED);
        plot.setRangeStepValue(2 *(roundUpMaxvalue - roundDownMinvalue) + 2);

    }

    private Number[] builOverallRatingArray(Number[] xLabelsDates) {

        Number[] overallRating = new Number[xLabelsDates.length];

        String overallRatingsQuery = "SELECT DATE, ROUND(AVG(RATING), 1) AS RATING" + " " +
                "FROM 'fact_food' " +
                "WHERE DATE BETWEEN " + xLabelsDates[0].toString() + " AND " + xLabelsDates[xLabelsDates.length - 1] + " " +
                "GROUP BY DATE" + " " +
                "ORDER BY DATE";

        Cursor overallRatingsCursor = app_db.runSqlQuery(overallRatingsQuery);

        // put all ordered dates and ratings from the cursor into two arrays
        int i = 0;
        Number[] dbDates = new Integer[overallRatingsCursor.getCount()];
        Number[] dbRatings = new Float[overallRatingsCursor.getCount()];

        while (overallRatingsCursor.moveToNext()) {
            dbDates[i] = Integer.parseInt(overallRatingsCursor.getString(0));
            dbRatings[i] = Float.parseFloat(overallRatingsCursor.getString(1));
            i++;
        }
        overallRating = fillMissingDatesWithPeriodAverage(xLabelsDates, dbDates, dbRatings);

        return  overallRating;
    }
    private Number[] buildBreakfastRatingArray(Number[] xLabelsDates) {

        Number[] breakfastRating = new Number[xLabelsDates.length];

        String breakfastRatingsQuery = "SELECT DATE, ROUND(AVG(RATING), 1) AS RATING" + " " +
                "FROM 'fact_food' " +
                "WHERE DATE BETWEEN " + xLabelsDates[0].toString() + " AND " + xLabelsDates[xLabelsDates.length - 1] + " " +
                "AND FOOD_TYPE = 'Breakfast' " +
                "GROUP BY DATE" + " " +
                "ORDER BY DATE";

        Cursor overallRatingsCursor = app_db.runSqlQuery(breakfastRatingsQuery);

        // put all ordered dates and ratings from the cursor into two arrays
        int i = 0;
        Number[] dbDates = new Integer[overallRatingsCursor.getCount()];
        Number[] dbRatings = new Float[overallRatingsCursor.getCount()];

        while (overallRatingsCursor.moveToNext()) {
            dbDates[i] = Integer.parseInt(overallRatingsCursor.getString(0));
            dbRatings[i] = Float.parseFloat(overallRatingsCursor.getString(1));
            i++;
        }
        breakfastRating = fillMissingDatesWithPeriodAverage(xLabelsDates, dbDates, dbRatings);

        return  breakfastRating;
    }
    private Number[] buildLunchRatingArray(Number[] xLabelsDates) {

        Number[] lunchRating = new Number[xLabelsDates.length];

        String lunchRatingsQuery = "SELECT DATE, ROUND(AVG(RATING), 1) AS RATING" + " " +
                "FROM 'fact_food' " +
                "WHERE DATE BETWEEN " + xLabelsDates[0].toString() + " AND " + xLabelsDates[xLabelsDates.length - 1] + " " +
                "AND FOOD_TYPE = 'Lunch' " +
                "GROUP BY DATE" + " " +
                "ORDER BY DATE";

        Cursor overallRatingsCursor = app_db.runSqlQuery(lunchRatingsQuery);

        // put all ordered dates and ratings from the cursor into two arrays
        int i = 0;
        Number[] dbDates = new Integer[overallRatingsCursor.getCount()];
        Number[] dbRatings = new Float[overallRatingsCursor.getCount()];

        while (overallRatingsCursor.moveToNext()) {
            dbDates[i] = Integer.parseInt(overallRatingsCursor.getString(0));
            dbRatings[i] = Float.parseFloat(overallRatingsCursor.getString(1));
            i++;
        }
        lunchRating = fillMissingDatesWithPeriodAverage(xLabelsDates, dbDates, dbRatings);

        return  lunchRating;
    }
    private Number[] buildOtherRatingArray(Number[] xLabelsDates) {

        Number[] otherRating = new Number[xLabelsDates.length];

        String otherRatingsQuery = "SELECT DATE, ROUND(AVG(RATING), 1) AS RATING" + " " +
                "FROM 'fact_food' " +
                "WHERE DATE BETWEEN " + xLabelsDates[0].toString() + " AND " + xLabelsDates[xLabelsDates.length - 1] + " " +
                "AND FOOD_TYPE = 'Other' " +
                "GROUP BY DATE" + " " +
                "ORDER BY DATE";

        Cursor overallRatingsCursor = app_db.runSqlQuery(otherRatingsQuery);

        // put all ordered dates and ratings from the cursor into two arrays
        int i = 0;
        Number[] dbDates = new Integer[overallRatingsCursor.getCount()];
        Number[] dbRatings = new Float[overallRatingsCursor.getCount()];

        while (overallRatingsCursor.moveToNext()) {
            dbDates[i] = Integer.parseInt(overallRatingsCursor.getString(0));
            dbRatings[i] = Float.parseFloat(overallRatingsCursor.getString(1));
            i++;
        }
        otherRating = fillMissingDatesWithPeriodAverage(xLabelsDates, dbDates, dbRatings);

        return  otherRating;
    }
    private Number[] buildDinnerRatingArray(Number[] xLabelsDates) {

        Number[] dinnerRating = new Number[xLabelsDates.length];

        String dinnerRatingsQuery = "SELECT DATE, ROUND(AVG(RATING), 1) AS RATING" + " " +
                "FROM 'fact_food' " +
                "WHERE DATE BETWEEN " + xLabelsDates[0].toString() + " AND " + xLabelsDates[xLabelsDates.length - 1] + " " +
                "AND FOOD_TYPE = 'Dinner' " +
                "GROUP BY DATE" + " " +
                "ORDER BY DATE";

        Cursor overallRatingsCursor = app_db.runSqlQuery(dinnerRatingsQuery);

        // put all ordered dates and ratings from the cursor into two arrays
        int i = 0;
        Number[] dbDates = new Integer[overallRatingsCursor.getCount()];
        Number[] dbRatings = new Float[overallRatingsCursor.getCount()];

        while (overallRatingsCursor.moveToNext()) {
            dbDates[i] = Integer.parseInt(overallRatingsCursor.getString(0));
            dbRatings[i] = Float.parseFloat(overallRatingsCursor.getString(1));
            i++;
        }
        dinnerRating = fillMissingDatesWithPeriodAverage(xLabelsDates, dbDates, dbRatings);

        return  dinnerRating;
    }

    private Integer getSpinnerUserSelection(){

        Integer res = 7; // last week data default
        if(spinnerStatsTimerange.getSelectedItem().equals("Last week"))
            res = 7;
        if(spinnerStatsTimerange.getSelectedItem().equals("Last 2 weeks"))
            res = 14;
        if(spinnerStatsTimerange.getSelectedItem().equals("Last 3 weeks"))
            res = 21;
        if(spinnerStatsTimerange.getSelectedItem().equals("Last month"))
            res = 30;
        if(spinnerStatsTimerange.getSelectedItem().equals("Last 2 months"))
            res = 60;
        if(spinnerStatsTimerange.getSelectedItem().equals("Last 3 months"))
            res = 90;
        if(spinnerStatsTimerange.getSelectedItem().equals("Last 6 months"))
            res = 180;
        if(spinnerStatsTimerange.getSelectedItem().equals("Last year"))
            res = 365;

        return res;

    }
    private void resetCheckboxList(){

        checkBoxOverall.setChecked(true);
        checkBoxBreakfast.setChecked(false);
        checkBoxLunch.setChecked(false);
        checkBoxDinner.setChecked(false);
        checkBoxOther.setChecked(false);
    }

    private void spinnerChangeTimeWindow(){
        spinnerStatsTimerange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                resetCheckboxList();
                
                // empty plot
                plot.clear();

                drawPlot();

                // refresh plot
                plot.redraw();

                resetCheckboxList();
            }

            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

    }

}
