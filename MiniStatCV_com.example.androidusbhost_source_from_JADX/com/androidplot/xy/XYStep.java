package com.androidplot.xy;

public class XYStep {
    private final float stepCount;
    private final float stepPix;
    private final double stepVal;

    public XYStep(float stepCount, float stepPix, double stepVal) {
        this.stepCount = stepCount;
        this.stepPix = stepPix;
        this.stepVal = stepVal;
    }

    public double getStepCount() {
        return (double) this.stepCount;
    }

    public float getStepPix() {
        return this.stepPix;
    }

    public double getStepVal() {
        return this.stepVal;
    }
}
