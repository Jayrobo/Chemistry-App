package com.androidplot.pie;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.androidplot.Plot;
import com.androidplot.Plot.RenderMode;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.util.PixelUtils;

public class PieChart extends Plot<Segment, SegmentFormatter, PieRenderer> {
    private static final int DEFAULT_PIE_WIDGET_H_DP = 18;
    private static final int DEFAULT_PIE_WIDGET_W_DP = 10;
    private static final int DEFAULT_PIE_WIDGET_X_OFFSET_DP = 0;
    private static final int DEFAULT_PIE_WIDGET_Y_OFFSET_DP = 0;
    private PieWidget pieWidget;

    public void setPieWidget(PieWidget pieWidget) {
        this.pieWidget = pieWidget;
    }

    public PieChart(Context context, String title) {
        super(context, title);
    }

    public PieChart(Context context, String title, RenderMode mode) {
        super(context, title, mode);
    }

    public PieChart(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    protected void onPreInit() {
        this.pieWidget = new PieWidget(getLayoutManager(), this, new SizeMetrics(PixelUtils.dpToPix(18.0f), SizeLayoutType.FILL, PixelUtils.dpToPix(10.0f), SizeLayoutType.FILL));
        this.pieWidget.position(PixelUtils.dpToPix(0.0f), XLayoutStyle.ABSOLUTE_FROM_CENTER, PixelUtils.dpToPix(0.0f), YLayoutStyle.ABSOLUTE_FROM_CENTER, AnchorPosition.CENTER);
        this.pieWidget.setPadding(10.0f, 10.0f, 10.0f, 10.0f);
    }

    protected void processAttrs(TypedArray attrs) {
    }

    public PieWidget getPieWidget() {
        return this.pieWidget;
    }

    public void addSegment(Segment segment, SegmentFormatter formatter) {
        addSeries(segment, formatter);
    }

    public void removeSegment(Segment segment) {
        removeSeries(segment);
    }
}
