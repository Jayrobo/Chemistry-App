package com.androidplot.xy;

import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.v4.internal.view.SupportMenu;
import com.androidplot.ui.PositionMetric;

public abstract class ValueMarker<PositionMetricType extends PositionMetric> {
    private Paint linePaint;
    private String text;
    private int textMargin;
    private TextOrientation textOrientation;
    private Paint textPaint;
    private PositionMetricType textPosition;
    private Number value;

    public enum TextOrientation {
        HORIZONTAL,
        VERTICAL
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ValueMarker(Number value, String text, PositionMetricType textPosition) {
        this.textMargin = 2;
        this.linePaint = new Paint();
        this.linePaint.setColor(SupportMenu.CATEGORY_MASK);
        this.linePaint.setAntiAlias(true);
        this.linePaint.setStyle(Style.STROKE);
        this.textPaint = new Paint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setColor(SupportMenu.CATEGORY_MASK);
        this.value = value;
        this.textPosition = textPosition;
        this.text = text;
    }

    public ValueMarker(Number value, String text, PositionMetricType textPosition, Paint linePaint, Paint textPaint) {
        this(value, text, textPosition);
        this.linePaint = linePaint;
        this.textPaint = textPaint;
    }

    public ValueMarker(Number value, String text, PositionMetricType textPosition, int linePaint, int textPaint) {
        this(value, text, textPosition);
        this.linePaint.setColor(linePaint);
        this.textPaint.setColor(textPaint);
    }

    public Number getValue() {
        return this.value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public Paint getLinePaint() {
        return this.linePaint;
    }

    public void setLinePaint(Paint linePaint) {
        this.linePaint = linePaint;
    }

    public Paint getTextPaint() {
        return this.textPaint;
    }

    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
    }

    public TextOrientation getTextOrientation() {
        return this.textOrientation;
    }

    public void setTextOrientation(TextOrientation textOrientation) {
        this.textOrientation = textOrientation;
    }

    public int getTextMargin() {
        return this.textMargin;
    }

    public void setTextMargin(int textMargin) {
        this.textMargin = textMargin;
    }

    public PositionMetricType getTextPosition() {
        return this.textPosition;
    }

    public void setTextPosition(PositionMetricType textPosition) {
        this.textPosition = textPosition;
    }
}
