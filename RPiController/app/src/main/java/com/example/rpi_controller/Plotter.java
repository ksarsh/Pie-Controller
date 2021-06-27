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
    Map<Long, Float> TimeTempRecvMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plotter);

        tempPlot = findViewById(R.id.tempPlot);

        TimeTempRecvMap = (Map<Long, Float>) getIntent().getSerializableExtra("HashMap");

        final Long[] instantLabel = sortInstant();
        final Float[] tempNumber = sortTemp(instantLabel);

        XYSeries tempSeries = new SimpleXYSeries(Arrays.asList(tempNumber),
                                                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                                                "Series");

        LineAndPointFormatter seriesFormat = new LineAndPointFormatter( Color.RED,
                                                                        Color.GREEN,
                                                                null,
                                                                    null);

        seriesFormat.setInterpolationParams(new CatmullRomInterpolator.Params(10,
                                                CatmullRomInterpolator.Type.Centripetal));

        tempPlot.addSeries(tempSeries, seriesFormat);

        tempPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round( ((Number) obj).floatValue());
                String timeStr = milliSecToTime(instantLabel[i]);
                return toAppendTo.append(timeStr);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        PanZoom.attach(tempPlot);
    }

    public String milliSecToTime(Long timeInSec){
        long second = (timeInSec / 1000) % 60;
        long minute = (timeInSec / (1000 * 60)) % 60;
        long hour = (timeInSec / (1000 * 60 * 60)) % 24;

        String timeStr = String.format("%02d:%02d:%02d", hour, minute, second);

        return timeStr;
    }

    public Long[] sortInstant()
    {
        int size = TimeTempRecvMap.size();
        Long[] sortedInstant = TimeTempRecvMap.keySet().toArray(new Long[size]);
        Arrays.sort(sortedInstant);

//        for(Long inst: sortedInstant)
//            Log.d("Arjun SInstant Test" , " " + inst);

        return sortedInstant;
    }

    public Float[] sortTemp(Long[] sortedInstant)
    {
        int size = TimeTempRecvMap.size();
        int i = 0;
        Float[] sortedTemp = new Float[size];
        for(Long inst: sortedInstant) {
            sortedTemp[i] = TimeTempRecvMap.get(inst);
            i++;
        }
        return sortedTemp;
    }
}