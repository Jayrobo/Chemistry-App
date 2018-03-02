package com.androidplot.ui;

public class SizeMetric extends LayoutMetric<SizeLayoutType> {
    public /* bridge */ /* synthetic */ float getValue() {
        return super.getValue();
    }

    public /* bridge */ /* synthetic */ void setValue(float x0) {
        super.setValue(x0);
    }

    public SizeMetric(float value, SizeLayoutType layoutType) {
        super(value, layoutType);
    }

    protected void validatePair(float value, SizeLayoutType layoutType) {
        switch (layoutType) {
            case RELATIVE:
                if (value < 0.0f || value > 1.0f) {
                    throw new IllegalArgumentException("SizeMetric Relative and Hybrid layout values must be within the range of 0 to 1.");
                }
                return;
            default:
                return;
        }
    }

    public float getPixelValue(float size) {
        switch ((SizeLayoutType) getLayoutType()) {
            case RELATIVE:
                return getValue() * size;
            case ABSOLUTE:
                return getValue();
            case FILL:
                return size - getValue();
            default:
                throw new IllegalArgumentException("Unsupported LayoutType: " + getLayoutType());
        }
    }

    public void setLayoutType(SizeLayoutType layoutType) {
        super.setLayoutType(layoutType);
    }
}
