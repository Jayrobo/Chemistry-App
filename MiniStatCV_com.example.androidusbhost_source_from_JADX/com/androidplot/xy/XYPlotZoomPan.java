package com.androidplot.xy;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.androidplot.Plot.RenderMode;

public class XYPlotZoomPan extends XYPlot implements OnTouchListener {
    private static final float MIN_DIST_2_FING = 5.0f;
    private PointF firstFingerPos;
    private float lastMaxX = Float.MAX_VALUE;
    private float lastMaxY = Float.MAX_VALUE;
    private float lastMinX = Float.MAX_VALUE;
    private float lastMinY = Float.MAX_VALUE;
    private boolean mCalledBySelf;
    private float mDistX;
    private boolean mZoomEnabled;
    private boolean mZoomEnabledInit;
    private boolean mZoomHorizontally;
    private boolean mZoomHorizontallyInit;
    private boolean mZoomVertically;
    private boolean mZoomVerticallyInit;
    private float maxXLimit = Float.MAX_VALUE;
    private float maxYLimit = Float.MAX_VALUE;
    private float minXLimit = Float.MAX_VALUE;
    private float minYLimit = Float.MAX_VALUE;
    private State mode = State.NONE;

    private enum State {
        NONE,
        ONE_FINGER_DRAG,
        TWO_FINGERS_DRAG
    }

    public XYPlotZoomPan(Context context, String title, RenderMode mode) {
        super(context, title, mode);
        setZoomEnabled(true);
    }

    public XYPlotZoomPan(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (this.mZoomEnabled || !this.mZoomEnabledInit) {
            setZoomEnabled(true);
        }
        if (!this.mZoomHorizontallyInit) {
            this.mZoomHorizontally = true;
        }
        if (!this.mZoomVerticallyInit) {
            this.mZoomVertically = true;
        }
    }

    public XYPlotZoomPan(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (this.mZoomEnabled || !this.mZoomEnabledInit) {
            setZoomEnabled(true);
        }
        if (!this.mZoomHorizontallyInit) {
            this.mZoomHorizontally = true;
        }
        if (!this.mZoomVerticallyInit) {
            this.mZoomVertically = true;
        }
    }

    public XYPlotZoomPan(Context context, String title) {
        super(context, title);
    }

    public void setOnTouchListener(OnTouchListener l) {
        if (l != this) {
            this.mZoomEnabled = false;
        }
        super.setOnTouchListener(l);
    }

    public boolean getZoomVertically() {
        return this.mZoomVertically;
    }

    public void setZoomVertically(boolean zoomVertically) {
        this.mZoomVertically = zoomVertically;
        this.mZoomVerticallyInit = true;
    }

    public boolean getZoomHorizontally() {
        return this.mZoomHorizontally;
    }

    public void setZoomHorizontally(boolean zoomHorizontally) {
        this.mZoomHorizontally = zoomHorizontally;
        this.mZoomHorizontallyInit = true;
    }

    public void setZoomEnabled(boolean enabled) {
        if (enabled) {
            setOnTouchListener(this);
        } else {
            setOnTouchListener(null);
        }
        this.mZoomEnabled = enabled;
        this.mZoomEnabledInit = true;
    }

    public boolean getZoomEnabled() {
        return this.mZoomEnabled;
    }

    private float getMinXLimit() {
        if (this.minXLimit == Float.MAX_VALUE) {
            this.minXLimit = getCalculatedMinX().floatValue();
            this.lastMinX = this.minXLimit;
        }
        return this.minXLimit;
    }

    private float getMaxXLimit() {
        if (this.maxXLimit == Float.MAX_VALUE) {
            this.maxXLimit = getCalculatedMaxX().floatValue();
            this.lastMaxX = this.maxXLimit;
        }
        return this.maxXLimit;
    }

    private float getMinYLimit() {
        if (this.minYLimit == Float.MAX_VALUE) {
            this.minYLimit = getCalculatedMinY().floatValue();
            this.lastMinY = this.minYLimit;
        }
        return this.minYLimit;
    }

    private float getMaxYLimit() {
        if (this.maxYLimit == Float.MAX_VALUE) {
            this.maxYLimit = getCalculatedMaxY().floatValue();
            this.lastMaxY = this.maxYLimit;
        }
        return this.maxYLimit;
    }

    private float getLastMinX() {
        if (this.lastMinX == Float.MAX_VALUE) {
            this.lastMinX = getCalculatedMinX().floatValue();
        }
        return this.lastMinX;
    }

    private float getLastMaxX() {
        if (this.lastMaxX == Float.MAX_VALUE) {
            this.lastMaxX = getCalculatedMaxX().floatValue();
        }
        return this.lastMaxX;
    }

    private float getLastMinY() {
        if (this.lastMinY == Float.MAX_VALUE) {
            this.lastMinY = getCalculatedMinY().floatValue();
        }
        return this.lastMinY;
    }

    private float getLastMaxY() {
        if (this.lastMaxY == Float.MAX_VALUE) {
            this.lastMaxY = getCalculatedMaxY().floatValue();
        }
        return this.lastMaxY;
    }

    public synchronized void setDomainBoundaries(Number lowerBoundary, BoundaryMode lowerBoundaryMode, Number upperBoundary, BoundaryMode upperBoundaryMode) {
        super.setDomainBoundaries(lowerBoundary, lowerBoundaryMode, upperBoundary, upperBoundaryMode);
        if (this.mCalledBySelf) {
            this.mCalledBySelf = false;
        } else {
            this.minXLimit = lowerBoundaryMode == BoundaryMode.FIXED ? lowerBoundary.floatValue() : getCalculatedMinX().floatValue();
            this.maxXLimit = upperBoundaryMode == BoundaryMode.FIXED ? upperBoundary.floatValue() : getCalculatedMaxX().floatValue();
            this.lastMinX = this.minXLimit;
            this.lastMaxX = this.maxXLimit;
        }
    }

    public synchronized void setRangeBoundaries(Number lowerBoundary, BoundaryMode lowerBoundaryMode, Number upperBoundary, BoundaryMode upperBoundaryMode) {
        super.setRangeBoundaries(lowerBoundary, lowerBoundaryMode, upperBoundary, upperBoundaryMode);
        if (this.mCalledBySelf) {
            this.mCalledBySelf = false;
        } else {
            this.minYLimit = lowerBoundaryMode == BoundaryMode.FIXED ? lowerBoundary.floatValue() : getCalculatedMinY().floatValue();
            this.maxYLimit = upperBoundaryMode == BoundaryMode.FIXED ? upperBoundary.floatValue() : getCalculatedMaxY().floatValue();
            this.lastMinY = this.minYLimit;
            this.lastMaxY = this.maxYLimit;
        }
    }

    public synchronized void setDomainBoundaries(Number lowerBoundary, Number upperBoundary, BoundaryMode mode) {
        super.setDomainBoundaries(lowerBoundary, upperBoundary, mode);
        if (this.mCalledBySelf) {
            this.mCalledBySelf = false;
        } else {
            this.minXLimit = mode == BoundaryMode.FIXED ? lowerBoundary.floatValue() : getCalculatedMinX().floatValue();
            this.maxXLimit = mode == BoundaryMode.FIXED ? upperBoundary.floatValue() : getCalculatedMaxX().floatValue();
            this.lastMinX = this.minXLimit;
            this.lastMaxX = this.maxXLimit;
        }
    }

    public synchronized void setRangeBoundaries(Number lowerBoundary, Number upperBoundary, BoundaryMode mode) {
        super.setRangeBoundaries(lowerBoundary, upperBoundary, mode);
        if (this.mCalledBySelf) {
            this.mCalledBySelf = false;
        } else {
            this.minYLimit = mode == BoundaryMode.FIXED ? lowerBoundary.floatValue() : getCalculatedMinY().floatValue();
            this.maxYLimit = mode == BoundaryMode.FIXED ? upperBoundary.floatValue() : getCalculatedMaxY().floatValue();
            this.lastMinY = this.minYLimit;
            this.lastMaxY = this.maxYLimit;
        }
    }

    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction() & MotionEventCompat.ACTION_MASK) {
            case 0:
                this.firstFingerPos = new PointF(event.getX(), event.getY());
                this.mode = State.ONE_FINGER_DRAG;
                break;
            case 2:
                if (this.mode != State.ONE_FINGER_DRAG) {
                    if (this.mode == State.TWO_FINGERS_DRAG) {
                        zoom(event);
                        break;
                    }
                }
                pan(event);
                break;
                break;
            case 5:
                this.mDistX = getXDistance(event);
                if (this.mDistX > MIN_DIST_2_FING || this.mDistX < -5.0f) {
                    this.mode = State.TWO_FINGERS_DRAG;
                    break;
                }
            case 6:
                this.mode = State.NONE;
                break;
        }
        return true;
    }

    private float getXDistance(MotionEvent event) {
        return event.getX(0) - event.getX(1);
    }

    private void pan(MotionEvent motionEvent) {
        PointF oldFirstFinger = this.firstFingerPos;
        this.firstFingerPos = new PointF(motionEvent.getX(), motionEvent.getY());
        PointF newX = new PointF();
        if (this.mZoomHorizontally) {
            calculatePan(oldFirstFinger, newX, true);
            this.mCalledBySelf = true;
            super.setDomainBoundaries(Float.valueOf(newX.x), Float.valueOf(newX.y), BoundaryMode.FIXED);
            this.lastMinX = newX.x;
            this.lastMaxX = newX.y;
        }
        if (this.mZoomVertically) {
            calculatePan(oldFirstFinger, newX, false);
            this.mCalledBySelf = true;
            super.setRangeBoundaries(Float.valueOf(newX.x), Float.valueOf(newX.y), BoundaryMode.FIXED);
            this.lastMinY = newX.x;
            this.lastMaxY = newX.y;
        }
        redraw();
    }

    private void calculatePan(PointF oldFirstFinger, PointF newX, boolean horizontal) {
        float offset;
        if (horizontal) {
            newX.x = getLastMinX();
            newX.y = getLastMaxX();
            offset = (oldFirstFinger.x - this.firstFingerPos.x) * ((newX.y - newX.x) / ((float) getWidth()));
        } else {
            newX.x = getLastMinY();
            newX.y = getLastMaxY();
            offset = (-(oldFirstFinger.y - this.firstFingerPos.y)) * ((newX.y - newX.x) / ((float) getHeight()));
        }
        newX.x += offset;
        newX.y += offset;
        float diff = newX.y - newX.x;
        if (horizontal) {
            if (newX.x < getMinXLimit()) {
                newX.x = getMinXLimit();
                newX.y = newX.x + diff;
            }
            if (newX.y > getMaxXLimit()) {
                newX.y = getMaxXLimit();
                newX.x = newX.y - diff;
                return;
            }
            return;
        }
        if (newX.x < getMinYLimit()) {
            newX.x = getMinYLimit();
            newX.y = newX.x + diff;
        }
        if (newX.y > getMaxYLimit()) {
            newX.y = getMaxYLimit();
            newX.x = newX.y - diff;
        }
    }

    private void zoom(MotionEvent motionEvent) {
        float oldDist = this.mDistX;
        float newDist = getXDistance(motionEvent);
        if (oldDist > 0.0f && newDist < 0.0f) {
            return;
        }
        if (oldDist >= 0.0f || newDist <= 0.0f) {
            this.mDistX = newDist;
            float scale = oldDist / this.mDistX;
            if (!Float.isInfinite(scale) && !Float.isNaN(scale)) {
                if (((double) scale) <= -0.001d || ((double) scale) >= 0.001d) {
                    PointF newX = new PointF();
                    if (this.mZoomHorizontally) {
                        calculateZoom(scale, newX, true);
                        this.mCalledBySelf = true;
                        super.setDomainBoundaries(Float.valueOf(newX.x), Float.valueOf(newX.y), BoundaryMode.FIXED);
                        this.lastMinX = newX.x;
                        this.lastMaxX = newX.y;
                    }
                    if (this.mZoomVertically) {
                        calculateZoom(scale, newX, false);
                        this.mCalledBySelf = true;
                        super.setRangeBoundaries(Float.valueOf(newX.x), Float.valueOf(newX.y), BoundaryMode.FIXED);
                        this.lastMinY = newX.x;
                        this.lastMaxY = newX.y;
                    }
                    redraw();
                }
            }
        }
    }

    private void calculateZoom(float scale, PointF newX, boolean horizontal) {
        float calcMax;
        float span;
        if (horizontal) {
            calcMax = getLastMaxX();
            span = calcMax - getLastMinX();
        } else {
            calcMax = getLastMaxY();
            span = calcMax - getLastMinY();
        }
        float midPoint = calcMax - (span / 2.0f);
        float offset = (span * scale) / 2.0f;
        newX.x = midPoint - offset;
        newX.y = midPoint + offset;
        if (horizontal) {
            if (newX.x < getMinXLimit()) {
                newX.x = getMinXLimit();
            }
            if (newX.y > getMaxXLimit()) {
                newX.y = getMaxXLimit();
                return;
            }
            return;
        }
        if (newX.x < getMinYLimit()) {
            newX.x = getMinYLimit();
        }
        if (newX.y > getMaxYLimit()) {
            newX.y = getMaxYLimit();
        }
    }
}
