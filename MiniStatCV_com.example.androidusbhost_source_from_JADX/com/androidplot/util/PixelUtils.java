package com.androidplot.util;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PixelUtils {
    private static final Pattern DIMENSION_PATTERN = Pattern.compile("^\\s*(\\d+(\\.\\d+)*)\\s*([a-zA-Z]+)\\s*$");
    private static final float FLOAT_INT_AVG_NUDGE = 0.5f;
    public static final Map<String, Integer> dimensionConstantLookup = initDimensionConstantLookup();
    private static DisplayMetrics metrics;

    private static class InternalDimension {
        int unit;
        float value;

        public InternalDimension(float value, int unit) {
            this.value = value;
            this.unit = unit;
        }
    }

    public static void init(Context ctx) {
        metrics = ctx.getResources().getDisplayMetrics();
    }

    public static PointF add(PointF lhs, PointF rhs) {
        return new PointF(lhs.x + rhs.x, lhs.y + rhs.y);
    }

    public static PointF sub(PointF lhs, PointF rhs) {
        return new PointF(lhs.x - rhs.x, lhs.y - rhs.y);
    }

    public static RectF sink(RectF in) {
        return nearestPixRect(in.left, in.top, in.right, in.bottom);
    }

    public static RectF nearestPixRect(float left, float top, float right, float bottom) {
        return new RectF((float) ((int) (left + FLOAT_INT_AVG_NUDGE)), (float) ((int) (top + FLOAT_INT_AVG_NUDGE)), (float) ((int) (right + FLOAT_INT_AVG_NUDGE)), (float) ((int) (FLOAT_INT_AVG_NUDGE + bottom)));
    }

    public static float dpToPix(float dp) {
        return TypedValue.applyDimension(1, dp, metrics);
    }

    public static float spToPix(float sp) {
        return TypedValue.applyDimension(2, sp, metrics);
    }

    public static float fractionToPixH(float fraction) {
        return ((float) metrics.heightPixels) * fraction;
    }

    public static float fractionToPixW(float fraction) {
        return ((float) metrics.widthPixels) * fraction;
    }

    private static Map<String, Integer> initDimensionConstantLookup() {
        Map<String, Integer> m = new HashMap();
        m.put("px", Integer.valueOf(0));
        m.put("dip", Integer.valueOf(1));
        m.put("dp", Integer.valueOf(1));
        m.put("sp", Integer.valueOf(2));
        m.put("pt", Integer.valueOf(3));
        m.put("in", Integer.valueOf(4));
        m.put("mm", Integer.valueOf(5));
        return Collections.unmodifiableMap(m);
    }

    public static float stringToDimension(String dimension) {
        InternalDimension internalDimension = stringToInternalDimension(dimension);
        return TypedValue.applyDimension(internalDimension.unit, internalDimension.value, metrics);
    }

    private static InternalDimension stringToInternalDimension(String dimension) {
        Matcher matcher = DIMENSION_PATTERN.matcher(dimension);
        if (matcher.matches()) {
            float value = Float.valueOf(matcher.group(1)).floatValue();
            Integer dimensionUnit = (Integer) dimensionConstantLookup.get(matcher.group(3).toLowerCase());
            if (dimensionUnit != null) {
                return new InternalDimension(value, dimensionUnit.intValue());
            }
            throw new NumberFormatException();
        }
        throw new NumberFormatException();
    }
}
