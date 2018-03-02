package com.androidplot.xy;

import com.androidplot.ui.SeriesAndFormatterList;
import com.androidplot.ui.SeriesRenderer;
import java.util.Hashtable;

public abstract class XYSeriesRenderer<XYFormatterType extends XYSeriesFormatter> extends SeriesRenderer<XYPlot, XYSeries, XYFormatterType> {
    public XYSeriesRenderer(XYPlot plot) {
        super(plot);
    }

    public Hashtable<XYRegionFormatter, String> getUniqueRegionFormatters() {
        Hashtable<XYRegionFormatter, String> found = new Hashtable();
        SeriesAndFormatterList<XYSeries, XYFormatterType> sfl = getSeriesAndFormatterList();
        if (sfl != null) {
            for (XYSeriesFormatter xyf : sfl.getFormatterList()) {
                for (RectRegion region : xyf.getRegions().elements()) {
                    found.put(xyf.getRegionFormatter(region), region.getLabel());
                }
            }
        }
        return found;
    }
}
