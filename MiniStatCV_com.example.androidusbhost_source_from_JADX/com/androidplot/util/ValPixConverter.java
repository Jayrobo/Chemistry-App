package com.androidplot.util;

import android.graphics.PointF;
import android.graphics.RectF;

public class ValPixConverter {
    private static final int ZERO = 0;

    public static float valToPix(double val, double min, double max, float lengthPix, boolean flip) {
        if (lengthPix <= 0.0f) {
            throw new IllegalArgumentException("Length in pixels must be greater than 0.");
        }
        double raw = val - min;
        float pix = (float) (raw * (((double) lengthPix) / range(min, max)));
        if (flip) {
            return lengthPix - pix;
        }
        return pix;
    }

    public static double range(double min, double max) {
        return max - min;
    }

    public static double valPerPix(double min, double max, float lengthPix) {
        return range(min, max) / ((double) lengthPix);
    }

    public static double pixToVal(float pix, double min, double max, float lengthPix, boolean flip) {
        if (pix < 0.0f) {
            throw new IllegalArgumentException("pixel values cannot be negative.");
        } else if (lengthPix <= 0.0f) {
            throw new IllegalArgumentException("Length in pixels must be greater than 0.");
        } else {
            float pMult = pix;
            if (flip) {
                pMult = lengthPix - pix;
            }
            return ((range(min, max) / ((double) lengthPix)) * ((double) pMult)) + min;
        }
    }

    public static PointF valToPix(Number x, Number y, RectF plotArea, Number minX, Number maxX, Number minY, Number maxY) {
        return new PointF(valToPix(x.doubleValue(), minX.doubleValue(), maxX.doubleValue(), plotArea.width(), false) + plotArea.left, valToPix(y.doubleValue(), minY.doubleValue(), maxY.doubleValue(), plotArea.height(), true) + plotArea.top);
    }
}
