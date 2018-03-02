package com.androidplot.xy;

import android.graphics.Paint;
import com.androidplot.ui.PositionMetric;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.ui.YPositionMetric;

public class XValueMarker extends ValueMarker<YPositionMetric> {
    public XValueMarker(Number value, String text) {
        super(value, text, new YPositionMetric(3.0f, YLayoutStyle.ABSOLUTE_FROM_TOP));
    }

    public XValueMarker(Number value, String text, YPositionMetric textPosition, Paint linePaint, Paint textPaint) {
        super(value, text, (PositionMetric) textPosition, linePaint, textPaint);
    }

    public XValueMarker(Number value, String text, YPositionMetric textPosition, int linePaint, int textPaint) {
        super(value, text, (PositionMetric) textPosition, linePaint, textPaint);
    }
}
