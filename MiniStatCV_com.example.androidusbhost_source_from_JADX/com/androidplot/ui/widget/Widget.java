package com.androidplot.ui.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.BoxModel;
import com.androidplot.ui.BoxModelable;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.PositionMetrics;
import com.androidplot.ui.Resizable;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetric;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.util.DisplayDimensions;
import com.androidplot.util.PixelUtils;

public abstract class Widget implements BoxModelable, Resizable {
    private Paint backgroundPaint;
    private Paint borderPaint;
    private BoxModel boxModel;
    private boolean clippingEnabled;
    private boolean isVisible;
    private LayoutManager layoutManager;
    private DisplayDimensions plotDimensions;
    private PositionMetrics positionMetrics;
    private SizeMetrics sizeMetrics;
    private DisplayDimensions widgetDimensions;

    static /* synthetic */ class C00581 {
        static final /* synthetic */ int[] $SwitchMap$com$androidplot$ui$AnchorPosition = new int[AnchorPosition.values().length];

        static {
            try {
                $SwitchMap$com$androidplot$ui$AnchorPosition[AnchorPosition.LEFT_TOP.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$androidplot$ui$AnchorPosition[AnchorPosition.LEFT_MIDDLE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$androidplot$ui$AnchorPosition[AnchorPosition.LEFT_BOTTOM.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$androidplot$ui$AnchorPosition[AnchorPosition.RIGHT_TOP.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$androidplot$ui$AnchorPosition[AnchorPosition.RIGHT_BOTTOM.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$androidplot$ui$AnchorPosition[AnchorPosition.RIGHT_MIDDLE.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$androidplot$ui$AnchorPosition[AnchorPosition.TOP_MIDDLE.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$androidplot$ui$AnchorPosition[AnchorPosition.BOTTOM_MIDDLE.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$androidplot$ui$AnchorPosition[AnchorPosition.CENTER.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    protected abstract void doOnDraw(Canvas canvas, RectF rectF) throws PlotRenderException;

    public Widget(LayoutManager layoutManager, SizeMetric heightMetric, SizeMetric widthMetric) {
        this(layoutManager, new SizeMetrics(heightMetric, widthMetric));
    }

    public Widget(LayoutManager layoutManager, SizeMetrics sizeMetrics) {
        this.clippingEnabled = true;
        this.boxModel = new BoxModel();
        this.plotDimensions = new DisplayDimensions();
        this.widgetDimensions = new DisplayDimensions();
        this.isVisible = true;
        this.layoutManager = layoutManager;
        SizeMetrics oldSize = this.sizeMetrics;
        setSize(sizeMetrics);
        onMetricsChanged(oldSize, sizeMetrics);
    }

    public DisplayDimensions getWidgetDimensions() {
        return this.widgetDimensions;
    }

    public AnchorPosition getAnchor() {
        return getPositionMetrics().getAnchor();
    }

    public void setAnchor(AnchorPosition anchor) {
        getPositionMetrics().setAnchor(anchor);
    }

    public void position(float x, XLayoutStyle xLayoutStyle, float y, YLayoutStyle yLayoutStyle) {
        position(x, xLayoutStyle, y, yLayoutStyle, AnchorPosition.LEFT_TOP);
    }

    public void position(float x, XLayoutStyle xLayoutStyle, float y, YLayoutStyle yLayoutStyle, AnchorPosition anchor) {
        setPositionMetrics(new PositionMetrics(x, xLayoutStyle, y, yLayoutStyle, anchor));
        this.layoutManager.addToTop(this);
    }

    protected void onMetricsChanged(SizeMetrics oldSize, SizeMetrics newSize) {
    }

    public void onPostInit() {
    }

    public boolean containsPoint(PointF point) {
        return this.widgetDimensions.canvasRect.contains(point.x, point.y);
    }

    public void setSize(SizeMetrics sizeMetrics) {
        this.sizeMetrics = sizeMetrics;
    }

    public void setWidth(float width) {
        this.sizeMetrics.getWidthMetric().setValue(width);
    }

    public void setWidth(float width, SizeLayoutType layoutType) {
        this.sizeMetrics.getWidthMetric().set(width, layoutType);
    }

    public void setHeight(float height) {
        this.sizeMetrics.getHeightMetric().setValue(height);
    }

    public void setHeight(float height, SizeLayoutType layoutType) {
        this.sizeMetrics.getHeightMetric().set(height, layoutType);
    }

    public SizeMetric getWidthMetric() {
        return this.sizeMetrics.getWidthMetric();
    }

    public SizeMetric getHeightMetric() {
        return this.sizeMetrics.getHeightMetric();
    }

    public float getWidthPix(float size) {
        return this.sizeMetrics.getWidthMetric().getPixelValue(size);
    }

    public float getHeightPix(float size) {
        return this.sizeMetrics.getHeightMetric().getPixelValue(size);
    }

    public RectF getMarginatedRect(RectF widgetRect) {
        return this.boxModel.getMarginatedRect(widgetRect);
    }

    public RectF getPaddedRect(RectF widgetMarginRect) {
        return this.boxModel.getPaddedRect(widgetMarginRect);
    }

    public void setMarginRight(float marginRight) {
        this.boxModel.setMarginRight(marginRight);
    }

    public void setMargins(float left, float top, float right, float bottom) {
        this.boxModel.setMargins(left, top, right, bottom);
    }

    public void setPadding(float left, float top, float right, float bottom) {
        this.boxModel.setPadding(left, top, right, bottom);
    }

    public float getMarginTop() {
        return this.boxModel.getMarginTop();
    }

    public void setMarginTop(float marginTop) {
        this.boxModel.setMarginTop(marginTop);
    }

    public float getMarginBottom() {
        return this.boxModel.getMarginBottom();
    }

    public float getPaddingLeft() {
        return this.boxModel.getPaddingLeft();
    }

    public void setPaddingLeft(float paddingLeft) {
        this.boxModel.setPaddingLeft(paddingLeft);
    }

    public float getPaddingTop() {
        return this.boxModel.getPaddingTop();
    }

    public void setPaddingTop(float paddingTop) {
        this.boxModel.setPaddingTop(paddingTop);
    }

    public float getPaddingRight() {
        return this.boxModel.getPaddingRight();
    }

    public void setPaddingRight(float paddingRight) {
        this.boxModel.setPaddingRight(paddingRight);
    }

    public float getPaddingBottom() {
        return this.boxModel.getPaddingBottom();
    }

    public void setPaddingBottom(float paddingBottom) {
        this.boxModel.setPaddingBottom(paddingBottom);
    }

    public void setMarginBottom(float marginBottom) {
        this.boxModel.setMarginBottom(marginBottom);
    }

    public float getMarginLeft() {
        return this.boxModel.getMarginLeft();
    }

    public void setMarginLeft(float marginLeft) {
        this.boxModel.setMarginLeft(marginLeft);
    }

    public float getMarginRight() {
        return this.boxModel.getMarginRight();
    }

    public synchronized void refreshLayout() {
        if (this.positionMetrics != null) {
            float elementWidth = getWidthPix(this.plotDimensions.paddedRect.width());
            float elementHeight = getHeightPix(this.plotDimensions.paddedRect.height());
            PointF coords = getElementCoordinates(elementHeight, elementWidth, this.plotDimensions.paddedRect, this.positionMetrics);
            RectF widgetRect = new RectF(coords.x, coords.y, coords.x + elementWidth, coords.y + elementHeight);
            RectF marginatedWidgetRect = getMarginatedRect(widgetRect);
            this.widgetDimensions = new DisplayDimensions(widgetRect, marginatedWidgetRect, getPaddedRect(marginatedWidgetRect));
        }
    }

    public synchronized void layout(DisplayDimensions plotDimensions) {
        this.plotDimensions = plotDimensions;
        refreshLayout();
    }

    public PointF getElementCoordinates(float height, float width, RectF viewRect, PositionMetrics metrics) {
        return PixelUtils.sub(new PointF(metrics.getXPositionMetric().getPixelValue(viewRect.width()) + viewRect.left, metrics.getYPositionMetric().getPixelValue(viewRect.height()) + viewRect.top), getAnchorOffset(width, height, metrics.getAnchor()));
    }

    public static PointF getAnchorOffset(float width, float height, AnchorPosition anchorPosition) {
        PointF point = new PointF();
        switch (C00581.$SwitchMap$com$androidplot$ui$AnchorPosition[anchorPosition.ordinal()]) {
            case 1:
                break;
            case 2:
                point.set(0.0f, height / 2.0f);
                break;
            case 3:
                point.set(0.0f, height);
                break;
            case 4:
                point.set(width, 0.0f);
                break;
            case 5:
                point.set(width, height);
                break;
            case 6:
                point.set(width, height / 2.0f);
                break;
            case MotionEventCompat.ACTION_HOVER_MOVE /*7*/:
                point.set(width / 2.0f, 0.0f);
                break;
            case 8:
                point.set(width / 2.0f, height);
                break;
            case 9:
                point.set(width / 2.0f, height / 2.0f);
                break;
            default:
                throw new IllegalArgumentException("Unsupported anchor location: " + anchorPosition);
        }
        return point;
    }

    public static PointF getAnchorCoordinates(RectF widgetRect, AnchorPosition anchorPosition) {
        return PixelUtils.add(new PointF(widgetRect.left, widgetRect.top), getAnchorOffset(widgetRect.width(), widgetRect.height(), anchorPosition));
    }

    public static PointF getAnchorCoordinates(float x, float y, float width, float height, AnchorPosition anchorPosition) {
        return getAnchorCoordinates(new RectF(x, y, x + width, y + height), anchorPosition);
    }

    public void draw(Canvas canvas, RectF widgetRect) throws PlotRenderException {
        if (isVisible()) {
            if (this.backgroundPaint != null) {
                drawBackground(canvas, this.widgetDimensions.canvasRect);
            }
            doOnDraw(canvas, this.widgetDimensions.paddedRect);
            if (this.borderPaint != null) {
                drawBorder(canvas, this.widgetDimensions.paddedRect);
            }
        }
    }

    protected void drawBorder(Canvas canvas, RectF paddedRect) {
        canvas.drawRect(paddedRect, this.borderPaint);
    }

    protected void drawBackground(Canvas canvas, RectF widgetRect) {
        canvas.drawRect(widgetRect, this.backgroundPaint);
    }

    public Paint getBorderPaint() {
        return this.borderPaint;
    }

    public void setBorderPaint(Paint borderPaint) {
        this.borderPaint = borderPaint;
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    public void setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
    }

    public boolean isClippingEnabled() {
        return this.clippingEnabled;
    }

    public void setClippingEnabled(boolean clippingEnabled) {
        this.clippingEnabled = clippingEnabled;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public PositionMetrics getPositionMetrics() {
        return this.positionMetrics;
    }

    public void setPositionMetrics(PositionMetrics positionMetrics) {
        this.positionMetrics = positionMetrics;
    }
}
