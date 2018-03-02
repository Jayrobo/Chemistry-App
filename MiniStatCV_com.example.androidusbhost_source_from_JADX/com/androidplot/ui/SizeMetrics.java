package com.androidplot.ui;

import android.graphics.RectF;
import com.androidplot.util.PixelUtils;

public class SizeMetrics {
    private SizeMetric heightMetric;
    private SizeMetric widthMetric;

    public SizeMetrics(float height, SizeLayoutType heightLayoutType, float width, SizeLayoutType widthLayoutType) {
        this.heightMetric = new SizeMetric(height, heightLayoutType);
        this.widthMetric = new SizeMetric(width, widthLayoutType);
    }

    public SizeMetrics(SizeMetric heightMetric, SizeMetric widthMetric) {
        this.heightMetric = heightMetric;
        this.widthMetric = widthMetric;
    }

    public SizeMetric getHeightMetric() {
        return this.heightMetric;
    }

    public void setHeightMetric(SizeMetric heightMetric) {
        this.heightMetric = heightMetric;
    }

    public SizeMetric getWidthMetric() {
        return this.widthMetric;
    }

    public RectF getRectF(RectF canvasRect) {
        return new RectF(0.0f, 0.0f, this.widthMetric.getPixelValue(canvasRect.width()), this.heightMetric.getPixelValue(canvasRect.height()));
    }

    public RectF getRoundedRect(RectF canvasRect) {
        return PixelUtils.nearestPixRect(0.0f, 0.0f, this.widthMetric.getPixelValue(canvasRect.width()), this.heightMetric.getPixelValue(canvasRect.height()));
    }

    public void setWidthMetric(SizeMetric widthMetric) {
        this.widthMetric = widthMetric;
    }
}
