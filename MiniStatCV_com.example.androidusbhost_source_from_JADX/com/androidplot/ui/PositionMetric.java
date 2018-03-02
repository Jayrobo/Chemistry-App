package com.androidplot.ui;

public abstract class PositionMetric<LayoutType extends Enum> extends LayoutMetric<LayoutType> {

    protected enum LayoutMode {
        ABSOLUTE,
        RELATIVE
    }

    protected enum Origin {
        FROM_BEGINING,
        FROM_CENTER,
        FROM_END
    }

    public /* bridge */ /* synthetic */ Enum getLayoutType() {
        return super.getLayoutType();
    }

    public /* bridge */ /* synthetic */ float getValue() {
        return super.getValue();
    }

    public /* bridge */ /* synthetic */ void set(float x0, Enum x1) {
        super.set(x0, x1);
    }

    public /* bridge */ /* synthetic */ void setLayoutType(Enum x0) {
        super.setLayoutType(x0);
    }

    public /* bridge */ /* synthetic */ void setValue(float x0) {
        super.setValue(x0);
    }

    public PositionMetric(float value, LayoutType layoutType) {
        super(value, layoutType);
    }

    protected static void validateValue(float value, LayoutMode layoutMode) throws IllegalArgumentException {
        switch (layoutMode) {
            case ABSOLUTE:
                return;
            case RELATIVE:
                if (value < -1.0f || value > 1.0f) {
                    throw new IllegalArgumentException("Relative layout values must be within the range of -1 to 1.");
                }
                return;
            default:
                throw new IllegalArgumentException("Unknown LayoutMode: " + layoutMode);
        }
    }

    protected float getAbsolutePosition(float size, Origin origin) {
        switch (origin) {
            case FROM_BEGINING:
                return getValue();
            case FROM_CENTER:
                return (size / 2.0f) + getValue();
            case FROM_END:
                return size - getValue();
            default:
                throw new IllegalArgumentException("Unsupported Origin: " + origin);
        }
    }

    protected float getRelativePosition(float size, Origin origin) {
        switch (origin) {
            case FROM_BEGINING:
                return getValue() * size;
            case FROM_CENTER:
                return (size / 2.0f) + ((size / 2.0f) * getValue());
            case FROM_END:
                return (getValue() * size) + size;
            default:
                throw new IllegalArgumentException("Unsupported Origin: " + origin);
        }
    }
}
