
package com.example.hello.alarm;
import android.util.Log;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

class DateAxisValueFormatter implements IAxisValueFormatter {

    private BarLineChartBase<?> chart;

    public DateAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        String suffix = "th";
        switch ((int) value){
            case 1:
                suffix = "st";
                break;
            case 2:
                suffix = "nd";
                break;
            case 3:
                suffix = "rd";
                break;
            case 21:
                suffix = "st";
                break;
            case 22:
                suffix = "nd";
                break;
            case 23:
                suffix = "rd";
                break;
            case 31:
                suffix = "st";
                break;
        }
        return new java.text.DecimalFormat("#").format(value) + suffix;
    }
}

