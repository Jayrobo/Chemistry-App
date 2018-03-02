package com.androidplot.util;

import android.graphics.RectF;

public class DisplayDimensions {
    private static final RectF initRect = new RectF(1.0f, 1.0f, 1.0f, 1.0f);
    public final RectF canvasRect;
    public final RectF marginatedRect;
    public final RectF paddedRect;

    public DisplayDimensions() {
        this(initRect, initRect, initRect);
    }

    public DisplayDimensions(RectF canvasRect, RectF marginatedRect, RectF paddedRect) {
        this.canvasRect = canvasRect;
        this.marginatedRect = marginatedRect;
        this.paddedRect = paddedRect;
    }
}
