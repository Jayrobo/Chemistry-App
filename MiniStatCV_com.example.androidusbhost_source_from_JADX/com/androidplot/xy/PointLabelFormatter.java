package com.androidplot.xy;

import android.graphics.Paint;
import android.graphics.Paint.Align;
import com.androidplot.util.PixelUtils;

public class PointLabelFormatter {
    private static final float DEFAULT_H_OFFSET_DP = 0.0f;
    private static final float DEFAULT_TEXT_SIZE_SP = 12.0f;
    private static final float DEFAULT_V_OFFSET_DP = -4.0f;
    public float hOffset;
    private Paint textPaint;
    public float vOffset;

    public PointLabelFormatter() {
        this(-1);
    }

    public PointLabelFormatter(int textColor) {
        this(textColor, PixelUtils.dpToPix(DEFAULT_H_OFFSET_DP), PixelUtils.dpToPix(DEFAULT_V_OFFSET_DP));
    }

    public PointLabelFormatter(int textColor, float hOffset, float vOffset) {
        initTextPaint(Integer.valueOf(textColor));
        this.hOffset = hOffset;
        this.vOffset = vOffset;
    }

    public Paint getTextPaint() {
        return this.textPaint;
    }

    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
    }

    protected void initTextPaint(Integer textColor) {
        if (textColor == null) {
            setTextPaint(null);
            return;
        }
        setTextPaint(new Paint());
        getTextPaint().setAntiAlias(true);
        getTextPaint().setColor(textColor.intValue());
        getTextPaint().setTextAlign(Align.CENTER);
        getTextPaint().setTextSize(PixelUtils.spToPix(DEFAULT_TEXT_SIZE_SP));
    }
}
