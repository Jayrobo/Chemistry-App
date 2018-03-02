package com.androidplot.ui;

abstract class LayoutMetric<LayoutType extends Enum> {
    private LayoutType layoutType;
    private float value;

    public abstract float getPixelValue(float f);

    protected abstract void validatePair(float f, LayoutType layoutType);

    public LayoutMetric(float value, LayoutType layoutType) {
        validatePair(value, layoutType);
        set(value, layoutType);
    }

    public void set(float value, LayoutType layoutType) {
        validatePair(value, layoutType);
        this.value = value;
        this.layoutType = layoutType;
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        validatePair(value, this.layoutType);
        this.value = value;
    }

    public LayoutType getLayoutType() {
        return this.layoutType;
    }

    public void setLayoutType(LayoutType layoutType) {
        validatePair(this.value, layoutType);
        this.layoutType = layoutType;
    }
}
