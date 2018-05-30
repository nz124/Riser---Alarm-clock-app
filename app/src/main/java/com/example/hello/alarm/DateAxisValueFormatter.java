
package com.example.hello.alarm;
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
        return (int) value+"th";

    }
}

