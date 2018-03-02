package com.androidplot.xy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region.Op;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.XPositionMetric;
import com.androidplot.ui.YPositionMetric;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.FontUtils;
import com.androidplot.util.Mapping;
import com.androidplot.util.ValPixConverter;
import com.androidplot.util.ZHash;
import com.androidplot.util.ZIndexable;
import java.text.DecimalFormat;
import java.text.Format;

public class XYGraphWidget extends Widget {
    private static final int CURSOR_LABEL_SPACING = 2;
    private static final int MARKER_LABEL_SPACING = 2;
    private static final String TAG = "AndroidPlot";
    private ZHash<RectRegion, AxisValueLabelFormatter> axisValueLabelRegions;
    private Paint cursorLabelBackgroundPaint;
    private Paint cursorLabelPaint;
    private boolean domainAxisBottom = true;
    private Paint domainCursorPaint;
    private float domainCursorPosition;
    private Paint domainGridLinePaint;
    private float domainLabelHorizontalOffset = 0.0f;
    private float domainLabelOrientation;
    private Paint domainLabelPaint;
    private Mapping<Paint, Number> domainLabelPaintMap;
    private int domainLabelSubTickExtension = 0;
    private int domainLabelTickExtension = 5;
    private float domainLabelVerticalOffset = -5.0f;
    private float domainLabelWidth = 15.0f;
    private Paint domainOriginLabelPaint;
    private Paint domainOriginLinePaint;
    private Paint domainSubGridLinePaint;
    private boolean domainSubTick = true;
    private boolean domainTick = true;
    private Format domainValueFormat;
    private boolean drawCursorLabelEnabled = true;
    private boolean drawMarkersEnabled = true;
    private Paint gridBackgroundPaint = new Paint();
    private float gridPaddingBottom = 0.0f;
    private float gridPaddingLeft = 0.0f;
    private float gridPaddingRight = 0.0f;
    private float gridPaddingTop = 0.0f;
    private RectF gridRect;
    private RectF paddedGridRect;
    private XYPlot plot;
    private boolean rangeAxisLeft = true;
    private Paint rangeCursorPaint;
    private float rangeCursorPosition;
    private Paint rangeGridLinePaint;
    private float rangeLabelHorizontalOffset = 1.0f;
    private float rangeLabelOrientation;
    private Paint rangeLabelPaint;
    private Mapping<Paint, Number> rangeLabelPaintMap;
    private int rangeLabelSubTickExtension = 0;
    private int rangeLabelTickExtension = 5;
    private float rangeLabelVerticalOffset = 0.0f;
    private float rangeLabelWidth = 41.0f;
    private Paint rangeOriginLabelPaint;
    private Paint rangeOriginLinePaint;
    private Paint rangeSubGridLinePaint;
    private boolean rangeSubTick = true;
    private boolean rangeTick = true;
    private Format rangeValueFormat;
    private int ticksPerDomainLabel = 1;
    private int ticksPerRangeLabel = 1;

    public enum XYPlotOrientation {
        HORIZONTAL,
        VERTICAL
    }

    public float getRangeLabelOrientation() {
        return this.rangeLabelOrientation;
    }

    public void setRangeLabelOrientation(float rangeLabelOrientation) {
        this.rangeLabelOrientation = rangeLabelOrientation;
    }

    public float getDomainLabelOrientation() {
        return this.domainLabelOrientation;
    }

    public void setDomainLabelOrientation(float domainLabelOrientation) {
        this.domainLabelOrientation = domainLabelOrientation;
    }

    public Mapping<Paint, Number> getDomainLabelPaintMap() {
        return this.domainLabelPaintMap;
    }

    public void setDomainLabelPaintMap(Mapping<Paint, Number> domainLabelPaintMap) {
        this.domainLabelPaintMap = domainLabelPaintMap;
    }

    public Mapping<Paint, Number> getRangeLabelPaintMap() {
        return this.rangeLabelPaintMap;
    }

    public void setRangeLabelPaintMap(Mapping<Paint, Number> rangeLabelPaintMap) {
        this.rangeLabelPaintMap = rangeLabelPaintMap;
    }

    public XYGraphWidget(LayoutManager layoutManager, XYPlot plot, SizeMetrics sizeMetrics) {
        super(layoutManager, sizeMetrics);
        this.gridBackgroundPaint.setColor(Color.rgb(140, 140, 140));
        this.gridBackgroundPaint.setStyle(Style.FILL);
        this.rangeGridLinePaint = new Paint();
        this.rangeGridLinePaint.setColor(Color.rgb(180, 180, 180));
        this.rangeGridLinePaint.setAntiAlias(true);
        this.rangeGridLinePaint.setStyle(Style.STROKE);
        this.domainGridLinePaint = new Paint(this.rangeGridLinePaint);
        this.domainSubGridLinePaint = new Paint(this.domainGridLinePaint);
        this.rangeSubGridLinePaint = new Paint(this.rangeGridLinePaint);
        this.domainOriginLinePaint = new Paint();
        this.domainOriginLinePaint.setColor(-1);
        this.domainOriginLinePaint.setAntiAlias(true);
        this.rangeOriginLinePaint = new Paint();
        this.rangeOriginLinePaint.setColor(-1);
        this.rangeOriginLinePaint.setAntiAlias(true);
        this.domainOriginLabelPaint = new Paint();
        this.domainOriginLabelPaint.setColor(-1);
        this.domainOriginLabelPaint.setAntiAlias(true);
        this.domainOriginLabelPaint.setTextAlign(Align.CENTER);
        this.rangeOriginLabelPaint = new Paint();
        this.rangeOriginLabelPaint.setColor(-1);
        this.rangeOriginLabelPaint.setAntiAlias(true);
        this.rangeOriginLabelPaint.setTextAlign(Align.RIGHT);
        this.domainLabelPaint = new Paint();
        this.domainLabelPaint.setColor(-3355444);
        this.domainLabelPaint.setAntiAlias(true);
        this.domainLabelPaint.setTextAlign(Align.CENTER);
        this.rangeLabelPaint = new Paint();
        this.rangeLabelPaint.setColor(-3355444);
        this.rangeLabelPaint.setAntiAlias(true);
        this.rangeLabelPaint.setTextAlign(Align.RIGHT);
        this.domainCursorPaint = new Paint();
        this.domainCursorPaint.setColor(-256);
        this.rangeCursorPaint = new Paint();
        this.rangeCursorPaint.setColor(-256);
        this.cursorLabelPaint = new Paint();
        this.cursorLabelPaint.setColor(-256);
        this.cursorLabelBackgroundPaint = new Paint();
        this.cursorLabelBackgroundPaint.setColor(Color.argb(100, 50, 50, 50));
        setMarginTop(7.0f);
        setMarginRight(4.0f);
        setMarginBottom(4.0f);
        this.rangeValueFormat = new DecimalFormat("0.0");
        this.domainValueFormat = new DecimalFormat("0.0");
        this.axisValueLabelRegions = new ZHash();
        this.plot = plot;
    }

    public ZIndexable<RectRegion> getAxisValueLabelRegions() {
        return this.axisValueLabelRegions;
    }

    public void addAxisValueLabelRegion(RectRegion region, AxisValueLabelFormatter formatter) {
        this.axisValueLabelRegions.addToTop(region, formatter);
    }

    public void addDomainAxisValueLabelRegion(double min, double max, AxisValueLabelFormatter formatter) {
        addAxisValueLabelRegion(new RectRegion(Double.valueOf(min), Double.valueOf(max), Double.valueOf(Double.POSITIVE_INFINITY), Double.valueOf(Double.NEGATIVE_INFINITY), null), formatter);
    }

    public void addRangeAxisValueLabelRegion(double min, double max, AxisValueLabelFormatter formatter) {
        addAxisValueLabelRegion(new RectRegion(Double.valueOf(Double.POSITIVE_INFINITY), Double.valueOf(Double.NEGATIVE_INFINITY), Double.valueOf(min), Double.valueOf(max), null), formatter);
    }

    public AxisValueLabelFormatter getAxisValueLabelFormatterForVal(double x, double y) {
        for (RectRegion r : this.axisValueLabelRegions.elements()) {
            if (r.containsValue(Double.valueOf(x), Double.valueOf(y))) {
                return (AxisValueLabelFormatter) this.axisValueLabelRegions.get(r);
            }
        }
        return null;
    }

    public AxisValueLabelFormatter getAxisValueLabelFormatterForDomainVal(double val) {
        for (RectRegion r : this.axisValueLabelRegions.elements()) {
            if (r.containsDomainValue(Double.valueOf(val))) {
                return (AxisValueLabelFormatter) this.axisValueLabelRegions.get(r);
            }
        }
        return null;
    }

    public AxisValueLabelFormatter getAxisValueLabelFormatterForRangeVal(double val) {
        for (RectRegion r : this.axisValueLabelRegions.elements()) {
            if (r.containsRangeValue(Double.valueOf(val))) {
                return (AxisValueLabelFormatter) this.axisValueLabelRegions.get(r);
            }
        }
        return null;
    }

    public RectF getGridRect() {
        return this.paddedGridRect;
    }

    private String getFormattedRangeValue(Number value) {
        return this.rangeValueFormat.format(value);
    }

    private String getFormattedDomainValue(Number value) {
        return this.domainValueFormat.format(value);
    }

    public Double getYVal(PointF point) {
        return getYVal(point.y);
    }

    public Double getYVal(float yPix) {
        if (this.plot.getCalculatedMinY() == null || this.plot.getCalculatedMaxY() == null) {
            return null;
        }
        return Double.valueOf(ValPixConverter.pixToVal(yPix - this.paddedGridRect.top, this.plot.getCalculatedMinY().doubleValue(), this.plot.getCalculatedMaxY().doubleValue(), this.paddedGridRect.height(), true));
    }

    public Double getXVal(PointF point) {
        return getXVal(point.x);
    }

    public Double getXVal(float xPix) {
        if (this.plot.getCalculatedMinX() == null || this.plot.getCalculatedMaxX() == null) {
            return null;
        }
        return Double.valueOf(ValPixConverter.pixToVal(xPix - this.paddedGridRect.left, this.plot.getCalculatedMinX().doubleValue(), this.plot.getCalculatedMaxX().doubleValue(), this.paddedGridRect.width(), false));
    }

    protected void doOnDraw(Canvas canvas, RectF widgetRect) throws PlotRenderException {
        this.gridRect = getGridRect(widgetRect);
        this.paddedGridRect = getPaddedGridRect(this.gridRect);
        if (this.paddedGridRect.height() > 0.0f && this.paddedGridRect.width() > 0.0f && this.plot.getCalculatedMinX() != null && this.plot.getCalculatedMaxX() != null && this.plot.getCalculatedMinY() != null && this.plot.getCalculatedMaxY() != null) {
            drawGrid(canvas);
            drawData(canvas);
            drawCursors(canvas);
            if (isDrawMarkersEnabled()) {
                drawMarkers(canvas);
            }
        }
    }

    private RectF getGridRect(RectF widgetRect) {
        float f = 1.0f;
        float f2 = widgetRect.left + (this.rangeAxisLeft ? this.rangeLabelWidth : 1.0f);
        float f3 = widgetRect.top + (this.domainAxisBottom ? 1.0f : this.domainLabelWidth);
        float f4 = widgetRect.right - (this.rangeAxisLeft ? 1.0f : this.rangeLabelWidth);
        float f5 = widgetRect.bottom;
        if (this.domainAxisBottom) {
            f = this.domainLabelWidth;
        }
        return new RectF(f2, f3, f4, f5 - f);
    }

    private RectF getPaddedGridRect(RectF gridRect) {
        return new RectF(gridRect.left + this.gridPaddingLeft, gridRect.top + this.gridPaddingTop, gridRect.right - this.gridPaddingRight, gridRect.bottom - this.gridPaddingBottom);
    }

    private void drawTickText(Canvas canvas, XYAxisType axis, Number value, float xPix, float yPix, Paint labelPaint) {
        AxisValueLabelFormatter rf = null;
        String txt = null;
        double v = value.doubleValue();
        int canvasState = canvas.save();
        try {
            Paint p;
            switch (axis) {
                case DOMAIN:
                    rf = getAxisValueLabelFormatterForDomainVal(v);
                    txt = getFormattedDomainValue(value);
                    canvas.rotate(getDomainLabelOrientation(), xPix, yPix);
                    break;
                case RANGE:
                    rf = getAxisValueLabelFormatterForRangeVal(v);
                    txt = getFormattedRangeValue(value);
                    canvas.rotate(getRangeLabelOrientation(), xPix, yPix);
                    break;
            }
            if (rf != null) {
                p = new Paint(labelPaint);
                p.setColor(rf.getColor());
            } else {
                p = labelPaint;
            }
            canvas.drawText(txt, xPix, yPix, p);
        } finally {
            canvas.restoreToCount(canvasState);
        }
    }

    private void drawDomainTick(Canvas canvas, float xPix, Number xVal, Paint labelPaint, Paint linePaint, boolean drawLineOnly) {
        if (!drawLineOnly) {
            if (linePaint != null && (this.domainTick || this.domainLabelTickExtension > 0)) {
                if (this.domainAxisBottom) {
                    canvas.drawLine(xPix, this.domainTick ? this.gridRect.top : this.gridRect.bottom, xPix, this.gridRect.bottom + ((float) this.domainLabelTickExtension), linePaint);
                } else {
                    canvas.drawLine(xPix, this.gridRect.top - ((float) this.domainLabelTickExtension), xPix, this.domainTick ? this.gridRect.bottom : this.gridRect.top, linePaint);
                }
            }
            if (labelPaint != null) {
                float yPix;
                float fontHeight = FontUtils.getFontHeight(labelPaint);
                if (this.domainAxisBottom) {
                    yPix = ((this.gridRect.bottom + ((float) this.domainLabelTickExtension)) + this.domainLabelVerticalOffset) + fontHeight;
                } else {
                    yPix = (this.gridRect.top - ((float) this.domainLabelTickExtension)) - this.domainLabelVerticalOffset;
                }
                drawTickText(canvas, XYAxisType.DOMAIN, xVal, xPix + this.domainLabelHorizontalOffset, yPix, labelPaint);
            }
        } else if (linePaint == null) {
        } else {
            if (!this.domainSubTick && this.domainLabelSubTickExtension <= 0) {
                return;
            }
            if (this.domainAxisBottom) {
                canvas.drawLine(xPix, this.domainSubTick ? this.gridRect.top : this.gridRect.bottom, xPix, this.gridRect.bottom + ((float) this.domainLabelSubTickExtension), linePaint);
            } else {
                canvas.drawLine(xPix, this.gridRect.top - ((float) this.domainLabelSubTickExtension), xPix, this.domainSubTick ? this.gridRect.bottom : this.gridRect.top, linePaint);
            }
        }
    }

    public void drawRangeTick(Canvas canvas, float yPix, Number yVal, Paint labelPaint, Paint linePaint, boolean drawLineOnly) {
        if (!drawLineOnly) {
            if (linePaint != null && (this.rangeTick || this.rangeLabelTickExtension > 0)) {
                if (this.rangeAxisLeft) {
                    canvas.drawLine(this.gridRect.left - ((float) this.rangeLabelTickExtension), yPix, this.rangeTick ? this.gridRect.right : this.gridRect.left, yPix, linePaint);
                } else {
                    canvas.drawLine(this.rangeTick ? this.gridRect.left : this.gridRect.right, yPix, this.gridRect.right + ((float) this.rangeLabelTickExtension), yPix, linePaint);
                }
            }
            if (labelPaint != null) {
                float xPix;
                if (this.rangeAxisLeft) {
                    xPix = this.gridRect.left - (((float) this.rangeLabelTickExtension) + this.rangeLabelHorizontalOffset);
                } else {
                    xPix = this.gridRect.right + (((float) this.rangeLabelTickExtension) + this.rangeLabelHorizontalOffset);
                }
                drawTickText(canvas, XYAxisType.RANGE, yVal, xPix, yPix - this.rangeLabelVerticalOffset, labelPaint);
            }
        } else if (linePaint == null) {
        } else {
            if (!this.rangeSubTick && this.rangeLabelSubTickExtension <= 0) {
                return;
            }
            if (this.rangeAxisLeft) {
                canvas.drawLine(this.gridRect.left - ((float) this.rangeLabelSubTickExtension), yPix, this.rangeSubTick ? this.gridRect.right : this.gridRect.left, yPix, linePaint);
            } else {
                canvas.drawLine(this.rangeTick ? this.gridRect.left : this.gridRect.right, yPix, this.gridRect.right + ((float) this.rangeLabelSubTickExtension), yPix, linePaint);
            }
        }
    }

    protected void drawGrid(Canvas canvas) {
        float domainOriginF;
        Paint olp;
        float rangeOriginF;
        if (this.gridBackgroundPaint != null) {
            canvas.drawRect(this.gridRect, this.gridBackgroundPaint);
        }
        if (this.plot.getDomainOrigin() != null) {
            domainOriginF = ValPixConverter.valToPix(this.plot.getDomainOrigin().doubleValue(), this.plot.getCalculatedMinX().doubleValue(), this.plot.getCalculatedMaxX().doubleValue(), this.paddedGridRect.width(), false) + this.paddedGridRect.left;
        } else {
            domainOriginF = this.paddedGridRect.left;
        }
        XYStep domainStep = XYStepCalculator.getStep(this.plot, XYAxisType.DOMAIN, this.paddedGridRect, Double.valueOf(this.plot.getCalculatedMinX().doubleValue()), Double.valueOf(this.plot.getCalculatedMaxX().doubleValue()));
        if (domainOriginF >= this.paddedGridRect.left && domainOriginF <= this.paddedGridRect.right) {
            if (this.domainOriginLinePaint != null) {
                this.domainOriginLinePaint.setTextAlign(Align.CENTER);
            }
            if (this.domainLabelPaintMap != null) {
                olp = (Paint) this.domainLabelPaintMap.get(this.plot.getDomainOrigin());
            } else {
                olp = this.domainLabelPaint;
            }
            if (olp == null) {
                olp = this.domainLabelPaint;
            }
            drawDomainTick(canvas, domainOriginF, Double.valueOf(this.plot.getDomainOrigin().doubleValue()), olp, this.domainOriginLinePaint, false);
        }
        int i = 1;
        float xPix = domainOriginF - domainStep.getStepPix();
        while (xPix >= this.paddedGridRect.left) {
            double xVal = this.plot.getDomainOrigin().doubleValue() - (((double) i) * domainStep.getStepVal());
            Paint dlp = this.domainLabelPaintMap != null ? (Paint) this.domainLabelPaintMap.get(Double.valueOf(xVal)) : this.domainLabelPaint;
            if (dlp == null) {
                dlp = this.domainLabelPaint;
            }
            if (xPix >= this.paddedGridRect.left && xPix <= this.paddedGridRect.right) {
                if (i % getTicksPerDomainLabel() == 0) {
                    drawDomainTick(canvas, xPix, Double.valueOf(xVal), dlp, this.domainGridLinePaint, false);
                } else {
                    drawDomainTick(canvas, xPix, Double.valueOf(xVal), dlp, this.domainSubGridLinePaint, true);
                }
            }
            i++;
            xPix = domainOriginF - (((float) i) * domainStep.getStepPix());
        }
        i = 1;
        xPix = domainOriginF + domainStep.getStepPix();
        while (xPix <= this.paddedGridRect.right) {
            xVal = this.plot.getDomainOrigin().doubleValue() + (((double) i) * domainStep.getStepVal());
            dlp = this.domainLabelPaintMap != null ? (Paint) this.domainLabelPaintMap.get(Double.valueOf(xVal)) : this.domainLabelPaint;
            if (dlp == null) {
                dlp = this.domainLabelPaint;
            }
            if (xPix >= this.paddedGridRect.left && xPix <= this.paddedGridRect.right) {
                if (i % getTicksPerDomainLabel() == 0) {
                    drawDomainTick(canvas, xPix, Double.valueOf(xVal), dlp, this.domainGridLinePaint, false);
                } else {
                    drawDomainTick(canvas, xPix, Double.valueOf(xVal), dlp, this.domainSubGridLinePaint, true);
                }
            }
            i++;
            xPix = domainOriginF + (((float) i) * domainStep.getStepPix());
        }
        if (this.plot.getRangeOrigin() != null) {
            rangeOriginF = ValPixConverter.valToPix(this.plot.getRangeOrigin().doubleValue(), this.plot.getCalculatedMinY().doubleValue(), this.plot.getCalculatedMaxY().doubleValue(), this.paddedGridRect.height(), true) + this.paddedGridRect.top;
        } else {
            rangeOriginF = this.paddedGridRect.bottom;
        }
        XYStep rangeStep = XYStepCalculator.getStep(this.plot, XYAxisType.RANGE, this.paddedGridRect, Double.valueOf(this.plot.getCalculatedMinY().doubleValue()), Double.valueOf(this.plot.getCalculatedMaxY().doubleValue()));
        if (rangeOriginF >= this.paddedGridRect.top && rangeOriginF <= this.paddedGridRect.bottom) {
            if (this.rangeOriginLinePaint != null) {
                this.rangeOriginLinePaint.setTextAlign(Align.RIGHT);
            }
            if (this.rangeLabelPaintMap != null) {
                olp = (Paint) this.rangeLabelPaintMap.get(this.plot.getRangeOrigin());
            } else {
                olp = this.rangeLabelPaint;
            }
            if (olp == null) {
                olp = this.rangeLabelPaint;
            }
            drawRangeTick(canvas, rangeOriginF, Double.valueOf(this.plot.getRangeOrigin().doubleValue()), olp, this.rangeOriginLinePaint, false);
        }
        i = 1;
        float yPix = rangeOriginF - rangeStep.getStepPix();
        while (yPix >= this.paddedGridRect.top) {
            double yVal = this.plot.getRangeOrigin().doubleValue() + (((double) i) * rangeStep.getStepVal());
            Paint rlp = this.rangeLabelPaintMap != null ? (Paint) this.rangeLabelPaintMap.get(Double.valueOf(yVal)) : this.rangeLabelPaint;
            if (rlp == null) {
                rlp = this.rangeLabelPaint;
            }
            if (yPix >= this.paddedGridRect.top && yPix <= this.paddedGridRect.bottom) {
                if (i % getTicksPerRangeLabel() == 0) {
                    drawRangeTick(canvas, yPix, Double.valueOf(yVal), rlp, this.rangeGridLinePaint, false);
                } else {
                    drawRangeTick(canvas, yPix, Double.valueOf(yVal), rlp, this.rangeSubGridLinePaint, true);
                }
            }
            i++;
            yPix = rangeOriginF - (((float) i) * rangeStep.getStepPix());
        }
        i = 1;
        yPix = rangeOriginF + rangeStep.getStepPix();
        while (yPix <= this.paddedGridRect.bottom) {
            yVal = this.plot.getRangeOrigin().doubleValue() - (((double) i) * rangeStep.getStepVal());
            rlp = this.rangeLabelPaintMap != null ? (Paint) this.rangeLabelPaintMap.get(Double.valueOf(yVal)) : this.rangeLabelPaint;
            if (rlp == null) {
                rlp = this.rangeLabelPaint;
            }
            if (yPix >= this.paddedGridRect.top && yPix <= this.paddedGridRect.bottom) {
                if (i % getTicksPerRangeLabel() == 0) {
                    drawRangeTick(canvas, yPix, Double.valueOf(yVal), rlp, this.rangeGridLinePaint, false);
                } else {
                    drawRangeTick(canvas, yPix, Double.valueOf(yVal), rlp, this.rangeSubGridLinePaint, true);
                }
            }
            i++;
            yPix = rangeOriginF + (((float) i) * rangeStep.getStepPix());
        }
    }

    private void drawMarkerText(Canvas canvas, String text, ValueMarker marker, float x, float y) {
        x += 2.0f;
        y -= 2.0f;
        RectF textRect = new RectF(FontUtils.getStringDimensions(text, marker.getTextPaint()));
        textRect.offsetTo(x, y - textRect.height());
        if (textRect.right > this.paddedGridRect.right) {
            textRect.offset(-(textRect.right - this.paddedGridRect.right), 0.0f);
        }
        if (textRect.top < this.paddedGridRect.top) {
            textRect.offset(0.0f, this.paddedGridRect.top - textRect.top);
        }
        canvas.drawText(text, textRect.left, textRect.bottom, marker.getTextPaint());
    }

    protected void drawMarkers(Canvas canvas) {
        float xPix;
        for (YValueMarker marker : this.plot.getYValueMarkers()) {
            float yPix;
            if (marker.getValue() != null) {
                yPix = ValPixConverter.valToPix(marker.getValue().doubleValue(), this.plot.getCalculatedMinY().doubleValue(), this.plot.getCalculatedMaxY().doubleValue(), this.paddedGridRect.height(), true) + this.paddedGridRect.top;
                canvas.drawLine(this.paddedGridRect.left, yPix, this.paddedGridRect.right, yPix, marker.getLinePaint());
                xPix = ((XPositionMetric) marker.getTextPosition()).getPixelValue(this.paddedGridRect.width()) + this.paddedGridRect.left;
                if (marker.getText() != null) {
                    drawMarkerText(canvas, marker.getText(), marker, xPix, yPix);
                } else {
                    drawMarkerText(canvas, getFormattedRangeValue(marker.getValue()), marker, xPix, yPix);
                }
            }
        }
        for (ValueMarker marker2 : this.plot.getXValueMarkers()) {
            if (marker2.getValue() != null) {
                xPix = ValPixConverter.valToPix(marker2.getValue().doubleValue(), this.plot.getCalculatedMinX().doubleValue(), this.plot.getCalculatedMaxX().doubleValue(), this.paddedGridRect.width(), false) + this.paddedGridRect.left;
                canvas.drawLine(xPix, this.paddedGridRect.top, xPix, this.paddedGridRect.bottom, marker2.getLinePaint());
                yPix = ((YPositionMetric) marker2.getTextPosition()).getPixelValue(this.paddedGridRect.height()) + this.paddedGridRect.top;
                if (marker2.getText() != null) {
                    drawMarkerText(canvas, marker2.getText(), marker2, xPix, yPix);
                } else {
                    drawMarkerText(canvas, getFormattedDomainValue(marker2.getValue()), marker2, xPix, yPix);
                }
            }
        }
    }

    protected void drawCursors(Canvas canvas) {
        boolean hasDomainCursor = false;
        if (this.domainCursorPaint != null && this.domainCursorPosition <= this.paddedGridRect.right && this.domainCursorPosition >= this.paddedGridRect.left) {
            hasDomainCursor = true;
            canvas.drawLine(this.domainCursorPosition, this.paddedGridRect.top, this.domainCursorPosition, this.paddedGridRect.bottom, this.domainCursorPaint);
        }
        boolean hasRangeCursor = false;
        if (this.rangeCursorPaint != null && this.rangeCursorPosition >= this.paddedGridRect.top && this.rangeCursorPosition <= this.paddedGridRect.bottom) {
            hasRangeCursor = true;
            canvas.drawLine(this.paddedGridRect.left, this.rangeCursorPosition, this.paddedGridRect.right, this.rangeCursorPosition, this.rangeCursorPaint);
        }
        if (this.drawCursorLabelEnabled && this.cursorLabelPaint != null && hasRangeCursor && hasDomainCursor) {
            String label = ("X=" + getDomainValueFormat().format(getDomainCursorVal())) + " Y=" + getRangeValueFormat().format(getRangeCursorVal());
            RectF cursorRect = new RectF(FontUtils.getPackedStringDimensions(label, this.cursorLabelPaint));
            cursorRect.offsetTo(this.domainCursorPosition, this.rangeCursorPosition - cursorRect.height());
            if (cursorRect.right >= this.paddedGridRect.right) {
                cursorRect.offsetTo(this.domainCursorPosition - cursorRect.width(), cursorRect.top);
            }
            if (cursorRect.top <= this.paddedGridRect.top) {
                cursorRect.offsetTo(cursorRect.left, this.rangeCursorPosition);
            }
            if (this.cursorLabelBackgroundPaint != null) {
                canvas.drawRect(cursorRect, this.cursorLabelBackgroundPaint);
            }
            canvas.drawText(label, cursorRect.left, cursorRect.bottom, this.cursorLabelPaint);
        }
    }

    protected void drawData(Canvas canvas) throws PlotRenderException {
        try {
            canvas.save(31);
            canvas.clipRect(this.gridRect, Op.INTERSECT);
            for (XYSeriesRenderer renderer : this.plot.getRendererList()) {
                renderer.render(canvas, this.paddedGridRect);
            }
        } finally {
            canvas.restore();
        }
    }

    protected void drawPoint(Canvas canvas, PointF point, Paint paint) {
        canvas.drawPoint(point.x, point.y, paint);
    }

    public float getDomainLabelWidth() {
        return this.domainLabelWidth;
    }

    public void setDomainLabelWidth(float domainLabelWidth) {
        this.domainLabelWidth = domainLabelWidth;
    }

    public float getRangeLabelWidth() {
        return this.rangeLabelWidth;
    }

    public void setRangeLabelWidth(float rangeLabelWidth) {
        this.rangeLabelWidth = rangeLabelWidth;
    }

    public float getDomainLabelVerticalOffset() {
        return this.domainLabelVerticalOffset;
    }

    public void setDomainLabelVerticalOffset(float domainLabelVerticalOffset) {
        this.domainLabelVerticalOffset = domainLabelVerticalOffset;
    }

    public float getDomainLabelHorizontalOffset() {
        return this.domainLabelHorizontalOffset;
    }

    public void setDomainLabelHorizontalOffset(float domainLabelHorizontalOffset) {
        this.domainLabelHorizontalOffset = domainLabelHorizontalOffset;
    }

    public float getRangeLabelHorizontalOffset() {
        return this.rangeLabelHorizontalOffset;
    }

    public void setRangeLabelHorizontalOffset(float rangeLabelHorizontalOffset) {
        this.rangeLabelHorizontalOffset = rangeLabelHorizontalOffset;
    }

    public float getRangeLabelVerticalOffset() {
        return this.rangeLabelVerticalOffset;
    }

    public void setRangeLabelVerticalOffset(float rangeLabelVerticalOffset) {
        this.rangeLabelVerticalOffset = rangeLabelVerticalOffset;
    }

    public Paint getGridBackgroundPaint() {
        return this.gridBackgroundPaint;
    }

    public void setGridBackgroundPaint(Paint gridBackgroundPaint) {
        this.gridBackgroundPaint = gridBackgroundPaint;
    }

    public Paint getDomainLabelPaint() {
        return this.domainLabelPaint;
    }

    public void setDomainLabelPaint(Paint domainLabelPaint) {
        this.domainLabelPaint = domainLabelPaint;
    }

    public Paint getRangeLabelPaint() {
        return this.rangeLabelPaint;
    }

    public void setRangeLabelPaint(Paint rangeLabelPaint) {
        this.rangeLabelPaint = rangeLabelPaint;
    }

    public Paint getDomainGridLinePaint() {
        return this.domainGridLinePaint;
    }

    public void setDomainGridLinePaint(Paint gridLinePaint) {
        this.domainGridLinePaint = gridLinePaint;
    }

    public Paint getRangeGridLinePaint() {
        return this.rangeGridLinePaint;
    }

    public Paint getDomainSubGridLinePaint() {
        return this.domainSubGridLinePaint;
    }

    public void setDomainSubGridLinePaint(Paint gridLinePaint) {
        this.domainSubGridLinePaint = gridLinePaint;
    }

    public void setRangeGridLinePaint(Paint gridLinePaint) {
        this.rangeGridLinePaint = gridLinePaint;
    }

    public Paint getRangeSubGridLinePaint() {
        return this.rangeSubGridLinePaint;
    }

    public void setRangeSubGridLinePaint(Paint gridLinePaint) {
        this.rangeSubGridLinePaint = gridLinePaint;
    }

    public Format getRangeValueFormat() {
        return this.rangeValueFormat;
    }

    public void setRangeValueFormat(Format rangeValueFormat) {
        this.rangeValueFormat = rangeValueFormat;
    }

    public Format getDomainValueFormat() {
        return this.domainValueFormat;
    }

    public void setDomainValueFormat(Format domainValueFormat) {
        this.domainValueFormat = domainValueFormat;
    }

    public int getDomainLabelTickExtension() {
        return this.domainLabelTickExtension;
    }

    public void setDomainLabelTickExtension(int domainLabelTickExtension) {
        this.domainLabelTickExtension = domainLabelTickExtension;
    }

    public int getDomainLabelSubTickExtension() {
        return this.domainLabelSubTickExtension;
    }

    public void setDomainLabelSubTickExtension(int domainLabelSubTickExtension) {
        this.domainLabelSubTickExtension = domainLabelSubTickExtension;
    }

    public int getRangeLabelTickExtension() {
        return this.rangeLabelTickExtension;
    }

    public void setRangeLabelTickExtension(int rangeLabelTickExtension) {
        this.rangeLabelTickExtension = rangeLabelTickExtension;
    }

    public int getRangeLabelSubTickExtension() {
        return this.rangeLabelSubTickExtension;
    }

    public void setRangeLabelSubTickExtension(int rangeLabelSubTickExtension) {
        this.rangeLabelSubTickExtension = rangeLabelSubTickExtension;
    }

    public int getTicksPerRangeLabel() {
        return this.ticksPerRangeLabel;
    }

    public void setTicksPerRangeLabel(int ticksPerRangeLabel) {
        this.ticksPerRangeLabel = ticksPerRangeLabel;
    }

    public int getTicksPerDomainLabel() {
        return this.ticksPerDomainLabel;
    }

    public void setTicksPerDomainLabel(int ticksPerDomainLabel) {
        this.ticksPerDomainLabel = ticksPerDomainLabel;
    }

    public void setGridPaddingTop(float gridPaddingTop) {
        this.gridPaddingTop = gridPaddingTop;
    }

    public float getGridPaddingBottom() {
        return this.gridPaddingBottom;
    }

    public void setGridPaddingBottom(float gridPaddingBottom) {
        this.gridPaddingBottom = gridPaddingBottom;
    }

    public float getGridPaddingLeft() {
        return this.gridPaddingLeft;
    }

    public void setGridPaddingLeft(float gridPaddingLeft) {
        this.gridPaddingLeft = gridPaddingLeft;
    }

    public float getGridPaddingRight() {
        return this.gridPaddingRight;
    }

    public void setGridPaddingRight(float gridPaddingRight) {
        this.gridPaddingRight = gridPaddingRight;
    }

    public float getGridPaddingTop() {
        return this.gridPaddingTop;
    }

    public void setGridPadding(float left, float top, float right, float bottom) {
        setGridPaddingLeft(left);
        setGridPaddingTop(top);
        setGridPaddingRight(right);
        setGridPaddingBottom(bottom);
    }

    public Paint getDomainOriginLinePaint() {
        return this.domainOriginLinePaint;
    }

    public void setDomainOriginLinePaint(Paint domainOriginLinePaint) {
        this.domainOriginLinePaint = domainOriginLinePaint;
    }

    public Paint getRangeOriginLinePaint() {
        return this.rangeOriginLinePaint;
    }

    public void setRangeOriginLinePaint(Paint rangeOriginLinePaint) {
        this.rangeOriginLinePaint = rangeOriginLinePaint;
    }

    public Paint getDomainOriginLabelPaint() {
        return this.domainOriginLabelPaint;
    }

    public void setDomainOriginLabelPaint(Paint domainOriginLabelPaint) {
        this.domainOriginLabelPaint = domainOriginLabelPaint;
    }

    public Paint getRangeOriginLabelPaint() {
        return this.rangeOriginLabelPaint;
    }

    public void setRangeOriginLabelPaint(Paint rangeOriginLabelPaint) {
        this.rangeOriginLabelPaint = rangeOriginLabelPaint;
    }

    public void setCursorPosition(float x, float y) {
        setDomainCursorPosition(x);
        setRangeCursorPosition(y);
    }

    public void setCursorPosition(PointF point) {
        setCursorPosition(point.x, point.y);
    }

    public float getDomainCursorPosition() {
        return this.domainCursorPosition;
    }

    public Double getDomainCursorVal() {
        return getXVal(getDomainCursorPosition());
    }

    public void setDomainCursorPosition(float domainCursorPosition) {
        this.domainCursorPosition = domainCursorPosition;
    }

    public float getRangeCursorPosition() {
        return this.rangeCursorPosition;
    }

    public Double getRangeCursorVal() {
        return getYVal(getRangeCursorPosition());
    }

    public void setRangeCursorPosition(float rangeCursorPosition) {
        this.rangeCursorPosition = rangeCursorPosition;
    }

    public Paint getCursorLabelPaint() {
        return this.cursorLabelPaint;
    }

    public void setCursorLabelPaint(Paint cursorLabelPaint) {
        this.cursorLabelPaint = cursorLabelPaint;
    }

    public Paint getCursorLabelBackgroundPaint() {
        return this.cursorLabelBackgroundPaint;
    }

    public void setCursorLabelBackgroundPaint(Paint cursorLabelBackgroundPaint) {
        this.cursorLabelBackgroundPaint = cursorLabelBackgroundPaint;
    }

    public boolean isDrawMarkersEnabled() {
        return this.drawMarkersEnabled;
    }

    public void setDrawMarkersEnabled(boolean drawMarkersEnabled) {
        this.drawMarkersEnabled = drawMarkersEnabled;
    }

    public boolean isRangeAxisLeft() {
        return this.rangeAxisLeft;
    }

    public void setRangeAxisLeft(boolean rangeAxisLeft) {
        this.rangeAxisLeft = rangeAxisLeft;
    }

    public boolean isDomainAxisBottom() {
        return this.domainAxisBottom;
    }

    public void setDomainAxisBottom(boolean domainAxisBottom) {
        this.domainAxisBottom = domainAxisBottom;
    }

    public boolean isRangeTick() {
        return this.rangeTick;
    }

    public void setRangeTick(boolean rangeTick) {
        this.rangeTick = rangeTick;
    }

    public boolean isRangeSubTick() {
        return this.rangeSubTick;
    }

    public void setRangeSubTick(boolean rangeSubTick) {
        this.rangeSubTick = rangeSubTick;
    }

    public boolean isDomainTick() {
        return this.domainTick;
    }

    public void setDomainTick(boolean domainTick) {
        this.domainTick = domainTick;
    }

    public boolean isDomainSubTick() {
        return this.domainSubTick;
    }

    public void setDomainSubTick(boolean domainSubTick) {
        this.domainSubTick = domainSubTick;
    }

    public void setRangeAxisPosition(boolean rangeAxisLeft, boolean rangeAxisOverlay, int tickSize, String maxLableString) {
        setRangeAxisLeft(rangeAxisLeft);
        Paint p;
        Paint po;
        if (rangeAxisOverlay) {
            setRangeLabelWidth(1.0f);
            setRangeLabelHorizontalOffset(-2.0f);
            setRangeLabelVerticalOffset(2.0f);
            p = getRangeLabelPaint();
            if (p != null) {
                p.setTextAlign(rangeAxisLeft ? Align.LEFT : Align.RIGHT);
            }
            po = getRangeOriginLabelPaint();
            if (po != null) {
                po.setTextAlign(rangeAxisLeft ? Align.LEFT : Align.RIGHT);
            }
            setRangeLabelTickExtension(0);
            return;
        }
        setRangeLabelWidth(1.0f);
        setRangeLabelHorizontalOffset(1.0f);
        setRangeLabelTickExtension(tickSize);
        p = getRangeLabelPaint();
        if (p != null) {
            p.setTextAlign(!rangeAxisLeft ? Align.LEFT : Align.RIGHT);
            Rect r = FontUtils.getPackedStringDimensions(maxLableString, p);
            setRangeLabelVerticalOffset((float) (r.top / 2));
            setRangeLabelWidth((float) (r.right + getRangeLabelTickExtension()));
        }
        po = getRangeOriginLabelPaint();
        if (po != null) {
            Align align;
            if (rangeAxisLeft) {
                align = Align.RIGHT;
            } else {
                align = Align.LEFT;
            }
            po.setTextAlign(align);
        }
    }

    public void setDomainAxisPosition(boolean domainAxisBottom, boolean domainAxisOverlay, int tickSize, String maxLabelString) {
        setDomainAxisBottom(domainAxisBottom);
        Paint p;
        if (domainAxisOverlay) {
            setDomainLabelWidth(1.0f);
            setDomainLabelVerticalOffset(2.0f);
            setDomainLabelTickExtension(0);
            p = getDomainLabelPaint();
            if (p != null) {
                Rect r = FontUtils.getPackedStringDimensions(maxLabelString, p);
                if (domainAxisBottom) {
                    setDomainLabelVerticalOffset((float) (r.top * 2));
                    return;
                } else {
                    setDomainLabelVerticalOffset(((float) r.top) - 1.0f);
                    return;
                }
            }
            return;
        }
        setDomainLabelWidth(1.0f);
        setDomainLabelTickExtension(tickSize);
        p = getDomainLabelPaint();
        if (p != null) {
            float fontHeight = FontUtils.getFontHeight(p);
            if (domainAxisBottom) {
                setDomainLabelVerticalOffset(-4.0f);
            } else {
                setDomainLabelVerticalOffset(1.0f);
            }
            setDomainLabelWidth(((float) getDomainLabelTickExtension()) + fontHeight);
        }
    }
}
