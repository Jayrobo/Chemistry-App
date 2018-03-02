package com.androidplot.ui;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Region.Op;
import com.androidplot.Plot;
import com.androidplot.Series;
import com.androidplot.exception.PlotRenderException;

public abstract class SeriesRenderer<PlotType extends Plot, SeriesType extends Series, SeriesFormatterType extends Formatter> {
    private PlotType plot;

    protected abstract void doDrawLegendIcon(Canvas canvas, RectF rectF, SeriesFormatterType seriesFormatterType);

    public abstract void onRender(Canvas canvas, RectF rectF) throws PlotRenderException;

    public SeriesRenderer(PlotType plot) {
        this.plot = plot;
    }

    public PlotType getPlot() {
        return this.plot;
    }

    public void setPlot(PlotType plot) {
        this.plot = plot;
    }

    public SeriesAndFormatterList<SeriesType, SeriesFormatterType> getSeriesAndFormatterList() {
        return this.plot.getSeriesAndFormatterListForRenderer(getClass());
    }

    public SeriesFormatterType getFormatter(SeriesType series) {
        return this.plot.getFormatter(series, getClass());
    }

    public void render(Canvas canvas, RectF plotArea) throws PlotRenderException {
        onRender(canvas, plotArea);
    }

    public void drawSeriesLegendIcon(Canvas canvas, RectF rect, SeriesFormatterType formatter) {
        try {
            canvas.save(31);
            canvas.clipRect(rect, Op.INTERSECT);
            doDrawLegendIcon(canvas, rect, formatter);
        } finally {
            canvas.restore();
        }
    }
}
