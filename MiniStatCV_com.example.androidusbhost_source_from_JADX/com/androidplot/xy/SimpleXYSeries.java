package com.androidplot.xy;

import android.graphics.Canvas;
import android.util.Pair;
import com.androidplot.Plot;
import com.androidplot.PlotListener;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SimpleXYSeries implements XYSeries, PlotListener {
    private static final String TAG = SimpleXYSeries.class.getName();
    private ReentrantReadWriteLock lock;
    private volatile String title;
    private volatile LinkedList<Number> xVals;
    private volatile LinkedList<Number> yVals;

    public enum ArrayFormat {
        Y_VALS_ONLY,
        XY_VALS_INTERLEAVED
    }

    public void onBeforeDraw(Plot source, Canvas canvas) {
        this.lock.readLock().lock();
    }

    public void onAfterDraw(Plot source, Canvas canvas) {
        this.lock.readLock().unlock();
    }

    public SimpleXYSeries(String title) {
        this.xVals = new LinkedList();
        this.yVals = new LinkedList();
        this.title = null;
        this.lock = new ReentrantReadWriteLock(true);
        this.title = title;
    }

    public SimpleXYSeries(List<? extends Number> model, ArrayFormat format, String title) {
        this(title);
        setModel(model, format);
    }

    public SimpleXYSeries(List<? extends Number> xVals, List<? extends Number> yVals, String title) {
        this(title);
        if (xVals == null || yVals == null) {
            throw new IllegalArgumentException("Neither the xVals nor the yVals parameters may be null.");
        } else if (xVals.size() != yVals.size()) {
            throw new IllegalArgumentException("xVals and yVals List parameters must be of the same size.");
        } else {
            this.xVals.addAll(xVals);
            this.yVals.addAll(yVals);
        }
    }

    public void useImplicitXVals() {
        this.lock.writeLock().lock();
        try {
            this.xVals = null;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void setModel(List<? extends Number> model, ArrayFormat format) {
        this.lock.writeLock().lock();
        try {
            this.xVals = null;
            this.yVals.clear();
            if (model == null || model.size() == 0) {
                this.lock.writeLock().unlock();
                return;
            }
            switch (format) {
                case Y_VALS_ONLY:
                    for (Number n : model) {
                        this.yVals.add(n);
                    }
                    break;
                case XY_VALS_INTERLEAVED:
                    if (this.xVals == null) {
                        this.xVals = new LinkedList();
                    }
                    if (model.size() % 2 == 0) {
                        int sz = model.size() / 2;
                        int i = 0;
                        int j = 0;
                        while (i < sz) {
                            this.xVals.add(model.get(j));
                            this.yVals.add(model.get(j + 1));
                            i++;
                            j += 2;
                        }
                        break;
                    }
                    throw new IndexOutOfBoundsException("Cannot auto-generate series from odd-sized xy List.");
                default:
                    throw new IllegalArgumentException("Unexpected enum value: " + format);
            }
            this.lock.writeLock().unlock();
        } catch (Throwable th) {
            this.lock.writeLock().unlock();
        }
    }

    public void setX(Number value, int index) {
        this.lock.writeLock().lock();
        try {
            this.xVals.set(index, value);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void setY(Number value, int index) {
        this.lock.writeLock().lock();
        try {
            this.yVals.set(index, value);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void setXY(Number xVal, Number yVal, int index) {
        this.lock.writeLock().lock();
        try {
            this.yVals.set(index, yVal);
            this.xVals.set(index, xVal);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void addFirst(Number x, Number y) {
        this.lock.writeLock().lock();
        try {
            if (this.xVals != null) {
                this.xVals.addFirst(x);
            }
            this.yVals.addFirst(y);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public Pair<Number, Number> removeFirst() {
        Pair<Number, Number> pair;
        this.lock.writeLock().lock();
        try {
            if (size() <= 0) {
                throw new NoSuchElementException();
            }
            pair = new Pair(this.xVals != null ? (Number) this.xVals.removeFirst() : Integer.valueOf(0), this.yVals.removeFirst());
            return pair;
        } finally {
            pair = this.lock.writeLock();
            pair.unlock();
        }
    }

    public void addLast(Number x, Number y) {
        this.lock.writeLock().lock();
        try {
            if (this.xVals != null) {
                this.xVals.addLast(x);
            }
            this.yVals.addLast(y);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public Pair<Number, Number> removeLast() {
        this.lock.writeLock().lock();
        Pair<Number, Number> pair;
        try {
            if (size() <= 0) {
                throw new NoSuchElementException();
            }
            pair = new Pair(this.xVals != null ? (Number) this.xVals.removeLast() : Integer.valueOf(this.yVals.size() - 1), this.yVals.removeLast());
            return pair;
        } finally {
            pair = this.lock.writeLock();
            pair.unlock();
        }
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.lock.writeLock().lock();
        try {
            this.title = title;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public int size() {
        return this.yVals != null ? this.yVals.size() : 0;
    }

    public Number getX(int index) {
        return this.xVals != null ? (Number) this.xVals.get(index) : Integer.valueOf(index);
    }

    public Number getY(int index) {
        return (Number) this.yVals.get(index);
    }
}
