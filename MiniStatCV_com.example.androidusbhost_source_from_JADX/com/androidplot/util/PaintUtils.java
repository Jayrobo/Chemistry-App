package com.androidplot.util;

import android.graphics.Paint;

public class PaintUtils {
    public static void setLineSizeDp(Paint paint, float lineSizeDp) {
        paint.setStrokeWidth(PixelUtils.dpToPix(lineSizeDp));
    }

    public static void setFontSizeDp(Paint paint, float fontSizeDp) {
        paint.setTextSize(PixelUtils.dpToPix(fontSizeDp));
    }

    public static void setShadowDp(Paint paint, float radiusDp, float dxDp, float dyDp, int color) {
        paint.setShadowLayer(PixelUtils.dpToPix(radiusDp), PixelUtils.dpToPix(dxDp), PixelUtils.dpToPix(dyDp), color);
    }
}
