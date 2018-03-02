package com.androidplot.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.DisplayDimensions;
import com.androidplot.util.ZLinkedList;

public class LayoutManager extends ZLinkedList<Widget> implements OnTouchListener, Resizable {
    private Paint anchorPaint = new Paint();
    private DisplayDimensions displayDims = new DisplayDimensions();
    private boolean drawAnchorsEnabled = false;
    private boolean drawMarginsEnabled = false;
    private boolean drawOutlineShadowsEnabled = false;
    private boolean drawOutlinesEnabled = false;
    private boolean drawPaddingEnabled = false;
    private Paint marginPaint;
    private Paint outlinePaint;
    private Paint outlineShadowPaint;
    private Paint paddingPaint;

    public synchronized void onPostInit() {
        for (Widget w : elements()) {
            w.onPostInit();
        }
    }

    public LayoutManager() {
        this.anchorPaint.setStyle(Style.FILL);
        this.anchorPaint.setColor(-16711936);
        this.outlinePaint = new Paint();
        this.outlinePaint.setColor(-16711936);
        this.outlinePaint.setStyle(Style.STROKE);
        this.marginPaint = new Paint();
        this.marginPaint.setColor(-256);
        this.marginPaint.setStyle(Style.FILL);
        this.marginPaint.setAlpha(200);
        this.paddingPaint = new Paint();
        this.paddingPaint.setColor(-16776961);
        this.paddingPaint.setStyle(Style.FILL);
        this.paddingPaint.setAlpha(200);
    }

    public void setMarkupEnabled(boolean enabled) {
        setDrawOutlinesEnabled(enabled);
        setDrawAnchorsEnabled(enabled);
        setDrawMarginsEnabled(enabled);
        setDrawPaddingEnabled(enabled);
        setDrawOutlineShadowsEnabled(enabled);
    }

    public void draw(Canvas canvas) throws PlotRenderException {
        if (isDrawMarginsEnabled()) {
            drawSpacing(canvas, this.displayDims.canvasRect, this.displayDims.marginatedRect, this.marginPaint);
        }
        if (isDrawPaddingEnabled()) {
            drawSpacing(canvas, this.displayDims.marginatedRect, this.displayDims.paddedRect, this.paddingPaint);
        }
        for (Widget widget : elements()) {
            try {
                canvas.save(31);
                PositionMetrics metrics = widget.getPositionMetrics();
                float elementWidth = widget.getWidthPix(this.displayDims.paddedRect.width());
                float elementHeight = widget.getHeightPix(this.displayDims.paddedRect.height());
                PointF coords = widget.getElementCoordinates(elementHeight, elementWidth, this.displayDims.paddedRect, metrics);
                DisplayDimensions dims = widget.getWidgetDimensions();
                if (this.drawOutlineShadowsEnabled) {
                    canvas.drawRect(dims.canvasRect, this.outlineShadowPaint);
                }
                if (widget.isClippingEnabled()) {
                    canvas.clipRect(dims.canvasRect, Op.INTERSECT);
                }
                widget.draw(canvas, dims.canvasRect);
                if (this.drawMarginsEnabled) {
                    drawSpacing(canvas, dims.canvasRect, dims.marginatedRect, getMarginPaint());
                }
                if (this.drawPaddingEnabled) {
                    drawSpacing(canvas, dims.marginatedRect, dims.paddedRect, getPaddingPaint());
                }
                if (this.drawAnchorsEnabled) {
                    drawAnchor(canvas, Widget.getAnchorCoordinates(coords.x, coords.y, elementWidth, elementHeight, metrics.getAnchor()));
                }
                if (this.drawOutlinesEnabled) {
                    this.outlinePaint.setAntiAlias(true);
                    canvas.drawRect(dims.canvasRect, this.outlinePaint);
                }
                canvas.restore();
            } catch (Throwable th) {
                canvas.restore();
            }
        }
    }

    private void drawSpacing(Canvas canvas, RectF outer, RectF inner, Paint paint) {
        try {
            canvas.save(31);
            canvas.clipRect(inner, Op.DIFFERENCE);
            canvas.drawRect(outer, paint);
        } finally {
            canvas.restore();
        }
    }

    protected void drawAnchor(Canvas canvas, PointF coords) {
        canvas.drawRect(coords.x - 4.0f, coords.y - 4.0f, coords.x + 4.0f, coords.y + 4.0f, this.anchorPaint);
    }

    public boolean isDrawOutlinesEnabled() {
        return this.drawOutlinesEnabled;
    }

    public void setDrawOutlinesEnabled(boolean drawOutlinesEnabled) {
        this.drawOutlinesEnabled = drawOutlinesEnabled;
    }

    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    public void setOutlinePaint(Paint outlinePaint) {
        this.outlinePaint = outlinePaint;
    }

    public boolean isDrawAnchorsEnabled() {
        return this.drawAnchorsEnabled;
    }

    public void setDrawAnchorsEnabled(boolean drawAnchorsEnabled) {
        this.drawAnchorsEnabled = drawAnchorsEnabled;
    }

    public boolean isDrawMarginsEnabled() {
        return this.drawMarginsEnabled;
    }

    public void setDrawMarginsEnabled(boolean drawMarginsEnabled) {
        this.drawMarginsEnabled = drawMarginsEnabled;
    }

    public Paint getMarginPaint() {
        return this.marginPaint;
    }

    public void setMarginPaint(Paint marginPaint) {
        this.marginPaint = marginPaint;
    }

    public boolean isDrawPaddingEnabled() {
        return this.drawPaddingEnabled;
    }

    public void setDrawPaddingEnabled(boolean drawPaddingEnabled) {
        this.drawPaddingEnabled = drawPaddingEnabled;
    }

    public Paint getPaddingPaint() {
        return this.paddingPaint;
    }

    public void setPaddingPaint(Paint paddingPaint) {
        this.paddingPaint = paddingPaint;
    }

    public boolean isDrawOutlineShadowsEnabled() {
        return this.drawOutlineShadowsEnabled;
    }

    public void setDrawOutlineShadowsEnabled(boolean drawOutlineShadowsEnabled) {
        this.drawOutlineShadowsEnabled = drawOutlineShadowsEnabled;
        if (drawOutlineShadowsEnabled && this.outlineShadowPaint == null) {
            this.outlineShadowPaint = new Paint();
            this.outlineShadowPaint.setColor(-12303292);
            this.outlineShadowPaint.setStyle(Style.FILL);
            this.outlineShadowPaint.setShadowLayer(3.0f, 5.0f, 5.0f, -16777216);
        }
    }

    public Paint getOutlineShadowPaint() {
        return this.outlineShadowPaint;
    }

    public void setOutlineShadowPaint(Paint outlineShadowPaint) {
        this.outlineShadowPaint = outlineShadowPaint;
    }

    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public void refreshLayout() {
        for (Widget widget : elements()) {
            widget.layout(this.displayDims);
        }
    }

    public void layout(DisplayDimensions dims) {
        this.displayDims = dims;
        refreshLayout();
    }
}
