package com.androidplot.ui;

public class YPositionMetric extends PositionMetric<YLayoutStyle> {
    public YPositionMetric(float value, YLayoutStyle layoutStyle) {
        super(value, layoutStyle);
    }

    protected void validatePair(float value, YLayoutStyle layoutStyle) {
        switch (layoutStyle) {
            case ABSOLUTE_FROM_TOP:
            case ABSOLUTE_FROM_BOTTOM:
            case ABSOLUTE_FROM_CENTER:
                PositionMetric.validateValue(value, LayoutMode.ABSOLUTE);
                return;
            case RELATIVE_TO_TOP:
            case RELATIVE_TO_BOTTOM:
            case RELATIVE_TO_CENTER:
                PositionMetric.validateValue(value, LayoutMode.RELATIVE);
                return;
            default:
                return;
        }
    }

    public float getPixelValue(float size) {
        switch ((YLayoutStyle) getLayoutType()) {
            case ABSOLUTE_FROM_TOP:
                return getAbsolutePosition(size, Origin.FROM_BEGINING);
            case ABSOLUTE_FROM_BOTTOM:
                return getAbsolutePosition(size, Origin.FROM_END);
            case ABSOLUTE_FROM_CENTER:
                return getAbsolutePosition(size, Origin.FROM_CENTER);
            case RELATIVE_TO_TOP:
                return getRelativePosition(size, Origin.FROM_BEGINING);
            case RELATIVE_TO_BOTTOM:
                return getRelativePosition(size, Origin.FROM_END);
            case RELATIVE_TO_CENTER:
                return getRelativePosition(size, Origin.FROM_CENTER);
            default:
                throw new IllegalArgumentException("Unsupported LayoutType: " + getLayoutType());
        }
    }

    public void setLayoutType(YLayoutStyle layoutType) {
        super.setLayoutType(layoutType);
    }
}
