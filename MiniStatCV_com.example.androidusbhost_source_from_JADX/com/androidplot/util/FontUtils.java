package com.androidplot.util;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;

public class FontUtils {
    public static float getFontHeight(Paint paint) {
        FontMetrics metrics = paint.getFontMetrics();
        return (-metrics.ascent) + metrics.descent;
    }

    public static Rect getPackedStringDimensions(String text, Paint paint) {
        Rect size = new Rect();
        paint.getTextBounds(text, 0, text.length(), size);
        return size;
    }

    public static Rect getStringDimensions(String text, Paint paint) {
        Rect size = new Rect();
        if (text == null || text.length() == 0) {
            return null;
        }
        paint.getTextBounds(text, 0, text.length(), size);
        size.bottom = size.top + ((int) getFontHeight(paint));
        return size;
    }
}
