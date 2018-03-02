package com.androidplot.ui;

import android.graphics.RectF;
import java.util.Iterator;

public abstract class TableModel {
    private TableOrder order;

    public enum Axis {
        ROW,
        COLUMN
    }

    public enum CellSizingMethod {
        FIXED,
        FILL
    }

    public abstract Iterator<RectF> getIterator(RectF rectF, int i);

    protected TableModel(TableOrder order) {
        setOrder(order);
    }

    public TableOrder getOrder() {
        return this.order;
    }

    public void setOrder(TableOrder order) {
        this.order = order;
    }
}
