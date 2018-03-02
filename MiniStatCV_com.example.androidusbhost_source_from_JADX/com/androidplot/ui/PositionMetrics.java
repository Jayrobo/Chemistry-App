package com.androidplot.ui;

public class PositionMetrics implements Comparable<PositionMetrics> {
    private AnchorPosition anchor;
    private float layerDepth;
    private XPositionMetric xPositionMetric;
    private YPositionMetric yPositionMetric;

    public PositionMetrics(float x, XLayoutStyle xLayoutStyle, float y, YLayoutStyle yLayoutStyle, AnchorPosition anchor) {
        setXPositionMetric(new XPositionMetric(x, xLayoutStyle));
        setYPositionMetric(new YPositionMetric(y, yLayoutStyle));
        setAnchor(anchor);
    }

    public YPositionMetric getYPositionMetric() {
        return this.yPositionMetric;
    }

    public void setYPositionMetric(YPositionMetric yPositionMetric) {
        this.yPositionMetric = yPositionMetric;
    }

    public AnchorPosition getAnchor() {
        return this.anchor;
    }

    public void setAnchor(AnchorPosition anchor) {
        this.anchor = anchor;
    }

    public int compareTo(PositionMetrics o) {
        if (this.layerDepth < o.layerDepth) {
            return -1;
        }
        if (this.layerDepth == o.layerDepth) {
            return 0;
        }
        return 1;
    }

    public XPositionMetric getXPositionMetric() {
        return this.xPositionMetric;
    }

    public void setXPositionMetric(XPositionMetric xPositionMetric) {
        this.xPositionMetric = xPositionMetric;
    }
}
