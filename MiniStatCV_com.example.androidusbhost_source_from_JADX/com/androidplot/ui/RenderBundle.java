package com.androidplot.ui;

import com.androidplot.Series;
import com.androidplot.xy.XYSeriesFormatter;

public abstract class RenderBundle<RenderBundleType extends RenderBundle, SeriesType extends Series, SeriesFormatterType extends XYSeriesFormatter> {
    private SeriesFormatterType formatter;
    private Series series;

    public RenderBundle(SeriesType series, SeriesFormatterType formatter) {
        this.formatter = formatter;
        this.series = series;
    }

    public Series getSeries() {
        return this.series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public SeriesFormatterType getFormatter() {
        return this.formatter;
    }

    public void setFormatter(SeriesFormatterType formatter) {
        this.formatter = formatter;
    }
}
