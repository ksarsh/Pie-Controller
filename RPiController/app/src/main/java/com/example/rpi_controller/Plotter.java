package com.example.rpi_controller;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PanZoom;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class Plotter extends AppCompatActivity {

    XYPlot tempPlot;
    Map<Instant, Float> TimeTempRecvMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plotter);

        tempPlot = findViewById(R.id.tempPlot);

        // Test getIntent()

        TimeTempRecvMap = (Map<Instant, Float>) getIntent().getSerializableExtra("HashMap");

//        for(Instant inst: TimeTempRecvMap.keySet())
//            Log.d("Arjun Intent HM Test", "Time: " + inst + " Temp: " + TimeTempRecvMap.get(inst));

        final Instant[] instantLabel = sortInstant();
        final Number[] timeLabel = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21};
        //double[] tempLabel = {44.2, 45.6, 39.3, 40.4, 46.8, 41.9, 45.7, 48.5, 48.4, 46.7};
        Number[] seriesNumber = {2,4,6,8,10,14,16,18,22,24};
        final Float[] tempNumber = sortTemp(instantLabel);

        // XYSeries tempSeries = new SimpleXYSeries(Arrays.asList(seriesNumber), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series");
        XYSeries tempSeries = new SimpleXYSeries(Arrays.asList(tempNumber), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series");

        LineAndPointFormatter seriesFormat = new LineAndPointFormatter(Color.RED, Color.GREEN, null,null);

        seriesFormat.setInterpolationParams(new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        tempPlot.addSeries(tempSeries, seriesFormat);

        tempPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round( ((Number) obj).floatValue());
                Log.d("Arjun i Test", "i: " + i);
                //return toAppendTo.append(timeLabel[i]);
                return toAppendTo.append(instantLabel[i]);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        PanZoom.attach(tempPlot);

    }

    public Instant[] sortInstant()
    {
        int size = TimeTempRecvMap.size();
        Instant[] sortedInstant = TimeTempRecvMap.keySet().toArray(new Instant[size]);
        Arrays.sort(sortedInstant);

        for(Instant inst: sortedInstant)
            Log.d("Arjun SInstant Test" , " " + inst);

        return sortedInstant;
    }

    public Float[] sortTemp(Instant[] sortedInstant)
    {
        int size = TimeTempRecvMap.size();
        int i = 0;
        Float[] sortedTemp = new Float[size];
        for(Instant inst: sortedInstant) {
            sortedTemp[i] = TimeTempRecvMap.get(inst);
            i++;
        }
        return sortedTemp;
    }
}