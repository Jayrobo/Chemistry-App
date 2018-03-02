package com.androidplot.xy;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import com.androidplot.ui.SeriesRenderer;

public class BarFormatter extends LineAndPointFormatter {
    private Paint borderPaint;
    private Paint fillPaint = new Paint();

    public Paint getFillPaint() {
        return this.fillPaint;
    }

    public void setFillPaint(Paint fillPaint) {
        this.fillPaint = fillPaint;
    }

    public Paint getBorderPaint() {
        return this.borderPaint;
    }

    public void setBorderPaint(Paint borderPaint) {
        this.borderPaint = borderPaint;
    }

    public BarFormatter() {
        this.fillPaint.setStyle(Style.FILL);
        this.fillPaint.setAlpha(100);
        this.borderPaint = new Paint();
        this.borderPaint.setStyle(Style.STROKE);
        this.borderPaint.setAlpha(100);
    }

    public BarFormatter(int fillColor, int borderColor) {
        this.fillPaint.setStyle(Style.FILL);
        this.fillPaint.setAlpha(100);
        this.borderPaint = new Paint();
        this.borderPaint.setStyle(Style.STROKE);
        this.borderPaint.setAlpha(100);
        this.fillPaint.setColor(fillColor);
        this.borderPaint.setColor(borderColor);
    }

    public Class<? extends SeriesRenderer> getRendererClass() {
        return BarRenderer.class;
    }

    public SeriesRenderer getRendererInstance(XYPlot plot) {
        return new BarRenderer(plot);
    }
}
