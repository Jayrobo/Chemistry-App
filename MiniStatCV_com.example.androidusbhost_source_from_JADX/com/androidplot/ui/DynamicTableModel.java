package com.androidplot.ui;

import android.graphics.RectF;
import com.androidplot.ui.TableModel.Axis;
import com.androidplot.ui.TableModel.CellSizingMethod;
import java.util.Iterator;

public class DynamicTableModel extends TableModel {
    private Float cellHeight;
    private Float cellWidth;
    private CellSizingMethod columnSizingMethod;
    private int numColumns;
    private int numRows;
    private CellSizingMethod rowSizingMethod;

    private class TableModelIterator implements Iterator<RectF> {
        private int calculatedColumns;
        private int calculatedNumElements;
        private int calculatedRows;
        private DynamicTableModel dynamicTableModel;
        private boolean isOk = true;
        int lastColumn = 0;
        int lastElement = 0;
        private RectF lastElementRect;
        int lastRow = 0;
        private TableOrder order;
        private RectF tableRect;
        private int totalElements;

        public TableModelIterator(DynamicTableModel dynamicTableModel, RectF tableRect, int totalElements) {
            this.dynamicTableModel = dynamicTableModel;
            this.tableRect = tableRect;
            this.totalElements = totalElements;
            this.order = dynamicTableModel.getOrder();
            if (dynamicTableModel.getNumColumns() == 0 && dynamicTableModel.getNumRows() >= 1) {
                this.calculatedRows = dynamicTableModel.getNumRows();
                this.calculatedColumns = new Float(((double) (((float) totalElements) / ((float) this.calculatedRows))) + 0.5d).intValue();
            } else if (dynamicTableModel.getNumRows() == 0 && dynamicTableModel.getNumColumns() >= 1) {
                this.calculatedColumns = dynamicTableModel.getNumColumns();
                this.calculatedRows = new Float(((double) (((float) totalElements) / ((float) this.calculatedColumns))) + 0.5d).intValue();
            } else if (dynamicTableModel.getNumColumns() == 0 && dynamicTableModel.getNumRows() == 0) {
                this.calculatedRows = 1;
                this.calculatedColumns = totalElements;
            } else {
                this.calculatedRows = dynamicTableModel.getNumRows();
                this.calculatedColumns = dynamicTableModel.getNumColumns();
            }
            this.calculatedNumElements = this.calculatedRows * this.calculatedColumns;
            this.lastElementRect = dynamicTableModel.getCellRect(tableRect, totalElements);
        }

        public boolean hasNext() {
            return this.isOk && this.lastElement < this.calculatedNumElements;
        }

        public RectF next() {
            if (!hasNext()) {
                this.isOk = false;
                throw new IndexOutOfBoundsException();
            } else if (this.lastElement == 0) {
                this.lastElement++;
                return this.lastElementRect;
            } else {
                RectF nextElementRect = new RectF(this.lastElementRect);
                switch (this.order) {
                    case ROW_MAJOR:
                        if (this.dynamicTableModel.getNumColumns() > 0 && this.lastColumn >= this.dynamicTableModel.getNumColumns() - 1) {
                            nextElementRect.offsetTo(this.tableRect.left, this.lastElementRect.bottom);
                            this.lastColumn = 0;
                            this.lastRow++;
                            break;
                        }
                        nextElementRect.offsetTo(this.lastElementRect.right, this.lastElementRect.top);
                        this.lastColumn++;
                        break;
                    case COLUMN_MAJOR:
                        if (this.dynamicTableModel.getNumRows() > 0 && this.lastRow >= this.dynamicTableModel.getNumRows() - 1) {
                            nextElementRect.offsetTo(this.lastElementRect.right, this.tableRect.top);
                            this.lastRow = 0;
                            this.lastColumn++;
                            break;
                        }
                        nextElementRect.offsetTo(this.lastElementRect.left, this.lastElementRect.bottom);
                        this.lastRow++;
                        break;
                        break;
                    default:
                        this.isOk = false;
                        throw new IllegalArgumentException();
                }
                this.lastElement++;
                this.lastElementRect = nextElementRect;
                return nextElementRect;
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public DynamicTableModel(int numColumns, int numRows) {
        this(numColumns, numRows, TableOrder.ROW_MAJOR);
    }

    public DynamicTableModel(int numColumns, int numRows, TableOrder order) {
        super(order);
        this.numColumns = numColumns;
        this.numRows = numRows;
    }

    public TableModelIterator getIterator(RectF tableRect, int totalElements) {
        return new TableModelIterator(this, tableRect, totalElements);
    }

    public RectF getCellRect(RectF tableRect, int numElements) {
        RectF cellRect = new RectF();
        cellRect.left = tableRect.left;
        cellRect.top = tableRect.top;
        cellRect.bottom = tableRect.top + calculateCellSize(tableRect, Axis.ROW, numElements);
        cellRect.right = tableRect.left + calculateCellSize(tableRect, Axis.COLUMN, numElements);
        return cellRect;
    }

    private float calculateCellSize(RectF tableRect, Axis axis, int numElementsInTable) {
        int axisElements = 0;
        float axisSizePix = 0.0f;
        switch (axis) {
            case ROW:
                axisElements = this.numRows;
                axisSizePix = tableRect.height();
                break;
            case COLUMN:
                axisElements = this.numColumns;
                axisSizePix = tableRect.width();
                break;
        }
        if (axisElements != 0) {
            return axisSizePix / ((float) axisElements);
        }
        return axisSizePix / ((float) numElementsInTable);
    }

    public int getNumRows() {
        return this.numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getNumColumns() {
        return this.numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
    }
}
