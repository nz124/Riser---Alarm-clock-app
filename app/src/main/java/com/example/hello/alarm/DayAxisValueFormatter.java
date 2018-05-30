package com.example.hello.alarm;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

class DayAxisValueFormatter implements IAxisValueFormatter {
    protected String[] mDayOfWeek = new String[]{
            "Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun"
    };

    private BarLineChartBase<?> chart;

    public DayAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mDayOfWeek[(int) value + 1];
    }
}
