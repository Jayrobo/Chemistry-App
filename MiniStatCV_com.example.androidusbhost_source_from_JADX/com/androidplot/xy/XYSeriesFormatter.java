package com.androidplot.xy;

import com.androidplot.ui.Formatter;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.util.ZHash;
import com.androidplot.util.ZIndexable;

public abstract class XYSeriesFormatter<XYRegionFormatterType extends XYRegionFormatter> extends Formatter<XYPlot> {
    ZHash<RectRegion, XYRegionFormatterType> regions = new ZHash();

    public abstract SeriesRenderer getRendererInstance(XYPlot xYPlot);

    public void addRegion(RectRegion region, XYRegionFormatterType regionFormatter) {
        this.regions.addToBottom(region, regionFormatter);
    }

    public void removeRegion(RectRegion region) {
        this.regions.remove(region);
    }

    public ZIndexable<RectRegion> getRegions() {
        return this.regions;
    }

    public XYRegionFormatterType getRegionFormatter(RectRegion region) {
        return (XYRegionFormatter) this.regions.get(region);
    }
}
