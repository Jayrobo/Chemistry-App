package com.androidplot.xy;

import com.androidplot.ui.SeriesRenderer;

public class BezierLineAndPointFormatter extends LineAndPointFormatter {
    public BezierLineAndPointFormatter(Integer lineColor, Integer vertexColor, Integer fillColor) {
        super(lineColor, vertexColor, fillColor, null);
    }

    public BezierLineAndPointFormatter(Integer lineColor, Integer vertexColor, Integer fillColor, FillDirection fillDir) {
        super(lineColor, vertexColor, fillColor, null, fillDir);
    }

    public Class<? extends SeriesRenderer> getRendererClass() {
        return BezierLineAndPointRenderer.class;
    }

    public SeriesRenderer getRendererInstance(XYPlot plot) {
        return new BezierLineAndPointRenderer(plot);
    }
}
