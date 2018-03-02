package com.androidplot.pie;

import android.graphics.Canvas;
import android.graphics.RectF;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.widget.Widget;

public class PieWidget extends Widget {
    private PieChart pieChart;

    public PieWidget(LayoutManager layoutManager, PieChart pieChart, SizeMetrics metrics) {
        super(layoutManager, metrics);
        this.pieChart = pieChart;
    }

    protected void doOnDraw(Canvas canvas, RectF widgetRect) throws PlotRenderException {
        for (PieRenderer renderer : this.pieChart.getRendererList()) {
            renderer.render(canvas, widgetRect);
        }
    }
}
