package com.androidplot.xy;

import android.graphics.RectF;
import com.androidplot.util.ValPixConverter;

public class XYStepCalculator {
    public static XYStep getStep(XYPlot plot, XYAxisType axisType, RectF rect, Number minVal, Number maxVal) {
        switch (axisType) {
            case DOMAIN:
                return getStep(plot.getDomainStepMode(), rect.width(), plot.getDomainStepValue(), minVal, maxVal);
            case RANGE:
                return getStep(plot.getRangeStepMode(), rect.height(), plot.getRangeStepValue(), minVal, maxVal);
            default:
                return null;
        }
    }

    public static XYStep getStep(XYStepMode typeXY, float plotPixelSize, double stepValue, Number minVal, Number maxVal) {
        double stepVal = 0.0d;
        float stepPix = 0.0f;
        float stepCount = 0.0f;
        switch (typeXY) {
            case INCREMENT_BY_VAL:
                stepVal = stepValue;
                stepPix = (float) (stepValue / ValPixConverter.valPerPix(minVal.doubleValue(), maxVal.doubleValue(), plotPixelSize));
                stepCount = plotPixelSize / stepPix;
                break;
            case INCREMENT_BY_PIXELS:
                stepPix = new Double(stepValue).floatValue();
                stepCount = plotPixelSize / stepPix;
                stepVal = ValPixConverter.valPerPix(minVal.doubleValue(), maxVal.doubleValue(), plotPixelSize) * ((double) stepPix);
                break;
            case SUBDIVIDE:
                stepCount = new Double(stepValue).floatValue();
                stepPix = plotPixelSize / (stepCount - 1.0f);
                stepVal = ValPixConverter.valPerPix(minVal.doubleValue(), maxVal.doubleValue(), plotPixelSize) * ((double) stepPix);
                break;
        }
        return new XYStep(stepCount, stepPix, stepVal);
    }
}
