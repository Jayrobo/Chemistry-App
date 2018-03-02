package com.androidplot.ui;

import android.graphics.RectF;
import java.util.Iterator;

public class FixedTableModel extends TableModel {
    private float cellHeight;
    private float cellWidth;

    private class FixedTableModelIterator implements Iterator<RectF> {
        private int lastElement;
        private RectF lastRect;
        private FixedTableModel model;
        private int numElements;
        private RectF tableRect;

        protected FixedTableModelIterator(FixedTableModel model, RectF tableRect, int numElements) {
            this.model = model;
            this.tableRect = tableRect;
            this.numElements = numElements;
            this.lastRect = new RectF(tableRect.left, tableRect.top, tableRect.left + model.getCellWidth(), tableRect.top + model.getCellHeight());
        }

        public boolean hasNext() {
            return this.lastElement < this.numElements && !(isColumnFinished() && isRowFinished());
        }

        private boolean isColumnFinished() {
            return this.lastRect.bottom + this.model.getCellHeight() > this.tableRect.height();
        }

        private boolean isRowFinished() {
            return this.lastRect.right + this.model.getCellWidth() > this.tableRect.width();
        }

        public RectF next() {
            try {
                RectF rectF;
                if (this.lastElement == 0) {
                    rectF = this.lastRect;
                } else if (this.lastElement >= this.numElements) {
                    throw new IndexOutOfBoundsException();
                } else {
                    switch (this.model.getOrder()) {
                        case ROW_MAJOR:
                            if (!isColumnFinished()) {
                                moveDown();
                                break;
                            }
                            moveOverAndUp();
                            break;
                        case COLUMN_MAJOR:
                            if (!isRowFinished()) {
                                moveOver();
                                break;
                            }
                            moveDownAndBack();
                            break;
                        default:
                            throw new UnsupportedOperationException();
                    }
                    rectF = this.lastRect;
                    this.lastElement++;
                }
                return rectF;
            } finally {
                this.lastElement++;
            }
        }

        private void moveDownAndBack() {
            this.lastRect.offsetTo(this.tableRect.left, this.lastRect.bottom);
        }

        private void moveOverAndUp() {
            this.lastRect.offsetTo(this.lastRect.right, this.tableRect.top);
        }

        private void moveOver() {
            this.lastRect.offsetTo(this.lastRect.right, this.lastRect.top);
        }

        private void moveDown() {
            this.lastRect.offsetTo(this.lastRect.left, this.lastRect.bottom);
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    protected FixedTableModel(float cellWidth, float cellHeight, TableOrder order) {
        super(order);
        setCellWidth(cellWidth);
        setCellHeight(cellHeight);
    }

    public Iterator<RectF> getIterator(RectF tableRect, int totalElements) {
        return new FixedTableModelIterator(this, tableRect, totalElements);
    }

    public float getCellWidth() {
        return this.cellWidth;
    }

    public void setCellWidth(float cellWidth) {
        this.cellWidth = cellWidth;
    }

    public float getCellHeight() {
        return this.cellHeight;
    }

    public void setCellHeight(float cellHeight) {
        this.cellHeight = cellHeight;
    }
}
