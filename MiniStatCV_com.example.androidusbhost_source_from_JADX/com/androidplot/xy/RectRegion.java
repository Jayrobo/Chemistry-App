package com.androidplot.xy;

import android.graphics.PointF;
import android.graphics.RectF;
import com.androidplot.LineRegion;
import com.androidplot.util.ValPixConverter;
import java.util.ArrayList;
import java.util.List;

public class RectRegion {
    private String label;
    LineRegion xLineRegion;
    LineRegion yLineRegion;

    public RectRegion(Number minX, Number maxX, Number minY, Number maxY, String label) {
        this.xLineRegion = new LineRegion(minX, maxX);
        this.yLineRegion = new LineRegion(minY, maxY);
        setLabel(label);
    }

    public RectRegion(Number minX, Number maxX, Number minY, Number maxY) {
        this(minX, maxX, minY, maxY, null);
    }

    public boolean containsPoint(PointF point) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean containsValue(Number x, Number y) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public boolean containsDomainValue(Number value) {
        return this.xLineRegion.contains(value);
    }

    public boolean containsRangeValue(Number value) {
        return this.yLineRegion.contains(value);
    }

    public boolean intersects(RectRegion region) {
        return intersects(region.getMinX(), region.getMaxX(), region.getMinY(), region.getMaxY());
    }

    public boolean intersects(Number minX, Number maxX, Number minY, Number maxY) {
        return this.xLineRegion.intersects(minX, maxX) && this.yLineRegion.intersects(minY, maxY);
    }

    public boolean intersects(RectF region, Number visMinX, Number visMaxX, Number visMinY, Number visMaxY) {
        return RectF.intersects(getRectF(region, Double.valueOf(visMinX.doubleValue()), Double.valueOf(visMaxX.doubleValue()), Double.valueOf(visMinY.doubleValue()), Double.valueOf(visMaxY.doubleValue())), region);
    }

    public RectF getRectF(RectF plotRect, Number visMinX, Number visMaxX, Number visMinY, Number visMaxY) {
        Number minVal;
        if (this.xLineRegion.getMinVal().doubleValue() != Double.NEGATIVE_INFINITY) {
            minVal = this.xLineRegion.getMinVal();
        } else {
            minVal = visMinX;
        }
        PointF topLeftPoint = ValPixConverter.valToPix(minVal, this.yLineRegion.getMaxVal().doubleValue() != Double.POSITIVE_INFINITY ? this.yLineRegion.getMaxVal() : visMaxY, plotRect, visMinX, visMaxX, visMinY, visMaxY);
        if (this.xLineRegion.getMaxVal().doubleValue() != Double.POSITIVE_INFINITY) {
            minVal = this.xLineRegion.getMaxVal();
        } else {
            minVal = visMaxX;
        }
        PointF bottomRightPoint = ValPixConverter.valToPix(minVal, this.yLineRegion.getMinVal().doubleValue() != Double.NEGATIVE_INFINITY ? this.yLineRegion.getMinVal() : visMinY, plotRect, visMinX, visMaxX, visMinY, visMaxY);
        return new RectF(topLeftPoint.x, topLeftPoint.y, bottomRightPoint.x, bottomRightPoint.y);
    }

    public static List<RectRegion> regionsWithin(List<RectRegion> regions, Number minX, Number maxX, Number minY, Number maxY) {
        ArrayList<RectRegion> intersectingRegions = new ArrayList();
        for (RectRegion r : regions) {
            if (r.intersects(minX, maxX, minY, maxY)) {
                intersectingRegions.add(r);
            }
        }
        return intersectingRegions;
    }

    public Number getMinX() {
        return this.xLineRegion.getMinVal();
    }

    public void setMinX(double minX) {
        this.xLineRegion.setMinVal(Double.valueOf(minX));
    }

    public Number getMaxX() {
        return this.xLineRegion.getMaxVal();
    }

    public void setMaxX(Number maxX) {
        this.xLineRegion.setMaxVal(maxX);
    }

    public Number getMinY() {
        return this.yLineRegion.getMinVal();
    }

    public void setMinY(Number minY) {
        this.yLineRegion.setMinVal(minY);
    }

    public Number getMaxY() {
        return this.yLineRegion.getMaxVal();
    }

    public void setMaxY(Number maxY) {
        this.yLineRegion.setMaxVal(maxY);
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
