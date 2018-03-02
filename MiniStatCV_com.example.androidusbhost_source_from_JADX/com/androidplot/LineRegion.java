package com.androidplot;

public class LineRegion {
    private Number maxVal;
    private Number minVal;

    public LineRegion(Number val1, Number v2) {
        if (val1.doubleValue() < v2.doubleValue()) {
            setMinVal(val1);
            setMaxVal(v2);
            return;
        }
        setMinVal(v2);
        setMaxVal(val1);
    }

    public static Number measure(Number val1, Number val2) {
        return new LineRegion(val1, val2).length();
    }

    public Number length() {
        return Double.valueOf(this.maxVal.doubleValue() - this.minVal.doubleValue());
    }

    public boolean contains(Number value) {
        return value.doubleValue() >= this.minVal.doubleValue() && value.doubleValue() <= this.maxVal.doubleValue();
    }

    public boolean intersects(LineRegion lineRegion) {
        return intersects(lineRegion.getMinVal(), lineRegion.getMaxVal());
    }

    public boolean intersects(Number line2Min, Number line2Max) {
        if ((line2Min.doubleValue() <= this.minVal.doubleValue() && line2Max.doubleValue() >= this.maxVal.doubleValue()) || contains(line2Min) || contains(line2Max)) {
            return true;
        }
        return false;
    }

    public Number getMinVal() {
        return this.minVal;
    }

    public void setMinVal(Number minVal) {
        if (minVal == null) {
            throw new NullPointerException("Region values can never be null.");
        }
        this.minVal = minVal;
    }

    public Number getMaxVal() {
        return this.maxVal;
    }

    public void setMaxVal(Number maxVal) {
        if (maxVal == null) {
            throw new NullPointerException("Region values can never be null.");
        }
        this.maxVal = maxVal;
    }
}
