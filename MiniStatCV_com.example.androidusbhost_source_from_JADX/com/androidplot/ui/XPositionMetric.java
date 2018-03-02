package com.androidplot.ui;

public class XPositionMetric extends PositionMetric<XLayoutStyle> {
    public XPositionMetric(float value, XLayoutStyle layoutStyle) {
        super(value, layoutStyle);
        validatePair(value, layoutStyle);
    }

    protected void validatePair(float value, XLayoutStyle layoutStyle) {
        switch (layoutStyle) {
            case ABSOLUTE_FROM_LEFT:
            case ABSOLUTE_FROM_RIGHT:
            case ABSOLUTE_FROM_CENTER:
                PositionMetric.validateValue(value, LayoutMode.ABSOLUTE);
                return;
            case RELATIVE_TO_LEFT:
            case RELATIVE_TO_RIGHT:
            case RELATIVE_TO_CENTER:
                PositionMetric.validateValue(value, LayoutMode.RELATIVE);
                return;
            default:
                return;
        }
    }

    public float getPixelValue(float size) {
        switch ((XLayoutStyle) getLayoutType()) {
            case ABSOLUTE_FROM_LEFT:
                return getAbsolutePosition(size, Origin.FROM_BEGINING);
            case ABSOLUTE_FROM_RIGHT:
                return getAbsolutePosition(size, Origin.FROM_END);
            case ABSOLUTE_FROM_CENTER:
                return getAbsolutePosition(size, Origin.FROM_CENTER);
            case RELATIVE_TO_LEFT:
                return getRelativePosition(size, Origin.FROM_BEGINING);
            case RELATIVE_TO_RIGHT:
                return getRelativePosition(size, Origin.FROM_END);
            case RELATIVE_TO_CENTER:
                return getRelativePosition(size, Origin.FROM_CENTER);
            default:
                throw new IllegalArgumentException("Unsupported LayoutType: " + getLayoutType());
        }
    }

    public void setLayoutType(XLayoutStyle layoutType) {
        super.setLayoutType(layoutType);
    }
}
