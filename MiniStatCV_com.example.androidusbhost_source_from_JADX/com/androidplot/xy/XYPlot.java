package com.androidplot.xy;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.util.AttributeSet;
import com.androidplot.Plot;
import com.androidplot.Plot.RenderMode;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.DynamicTableModel;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

public class XYPlot extends Plot<XYSeries, XYSeriesFormatter, XYSeriesRenderer> {
    private static final int DEFAULT_DOMAIN_LABEL_WIDGET_H_DP = 10;
    private static final int DEFAULT_DOMAIN_LABEL_WIDGET_TEXT_SIZE_SP = 20;
    private static final int DEFAULT_DOMAIN_LABEL_WIDGET_W_DP = 80;
    private static final int DEFAULT_DOMAIN_LABEL_WIDGET_X_OFFSET_DP = 20;
    private static final int DEFAULT_DOMAIN_LABEL_WIDGET_Y_OFFSET_DP = 0;
    private static final int DEFAULT_GRAPH_WIDGET_BOTTOM_MARGIN_DP = 3;
    private static final int DEFAULT_GRAPH_WIDGET_H_DP = 18;
    private static final int DEFAULT_GRAPH_WIDGET_LEFT_MARGIN_DP = 3;
    private static final int DEFAULT_GRAPH_WIDGET_RIGHT_MARGIN_DP = 3;
    private static final int DEFAULT_GRAPH_WIDGET_TOP_MARGIN_DP = 3;
    private static final int DEFAULT_GRAPH_WIDGET_W_DP = 10;
    private static final int DEFAULT_GRAPH_WIDGET_X_OFFSET_DP = 0;
    private static final int DEFAULT_GRAPH_WIDGET_Y_OFFSET_DP = 0;
    private static final int DEFAULT_LEGEND_WIDGET_H_DP = 10;
    private static final int DEFAULT_LEGEND_WIDGET_ICON_SIZE_DP = 7;
    private static final int DEFAULT_LEGEND_WIDGET_X_OFFSET_DP = 40;
    private static final int DEFAULT_LEGEND_WIDGET_Y_OFFSET_DP = 0;
    private static final int DEFAULT_PLOT_BOTTOM_MARGIN_DP = 2;
    private static final int DEFAULT_PLOT_LEFT_MARGIN_DP = 2;
    private static final int DEFAULT_PLOT_RIGHT_MARGIN_DP = 2;
    private static final int DEFAULT_RANGE_LABEL_WIDGET_H_DP = 50;
    private static final int DEFAULT_RANGE_LABEL_WIDGET_TEXT_SIZE_SP = 20;
    private static final int DEFAULT_RANGE_LABEL_WIDGET_W_DP = 10;
    private static final int DEFAULT_RANGE_LABEL_WIDGET_X_OFFSET_DP = 0;
    private static final int DEFAULT_RANGE_LABEL_WIDGET_Y_OFFSET_DP = 0;
    private Number calculatedDomainOrigin;
    private Number calculatedMaxX;
    private Number calculatedMaxY;
    private Number calculatedMinX;
    private Number calculatedMinY;
    private Number calculatedRangeOrigin;
    private RectRegion defaultBounds;
    private XYFramingModel domainFramingModel = XYFramingModel.EDGE;
    private TextLabelWidget domainLabelWidget;
    private Number domainLeftMax = null;
    private Number domainLeftMin = null;
    private BoundaryMode domainLowerBoundaryMode = BoundaryMode.AUTO;
    private BoundaryMode domainOriginBoundaryMode;
    private Number domainOriginExtent = null;
    private Number domainRightMax = null;
    private Number domainRightMin = null;
    private XYStepMode domainStepMode = XYStepMode.SUBDIVIDE;
    private double domainStepValue = 10.0d;
    private BoundaryMode domainUpperBoundaryMode = BoundaryMode.AUTO;
    private boolean drawDomainOriginEnabled = true;
    private boolean drawRangeOriginEnabled = true;
    private XYGraphWidget graphWidget;
    private XYLegendWidget legendWidget;
    private Number prevMaxX;
    private Number prevMaxY;
    private Number prevMinX;
    private Number prevMinY;
    private Number rangeBottomMax = null;
    private Number rangeBottomMin = null;
    private XYFramingModel rangeFramingModel = XYFramingModel.EDGE;
    private TextLabelWidget rangeLabelWidget;
    private BoundaryMode rangeLowerBoundaryMode = BoundaryMode.AUTO;
    private BoundaryMode rangeOriginBoundaryMode;
    private Number rangeOriginExtent = null;
    private XYStepMode rangeStepMode = XYStepMode.SUBDIVIDE;
    private double rangeStepValue = 10.0d;
    private Number rangeTopMax = null;
    private Number rangeTopMin = null;
    private BoundaryMode rangeUpperBoundaryMode = BoundaryMode.AUTO;
    private Number userDomainOrigin;
    private Number userMaxX;
    private Number userMaxY;
    private Number userMinX;
    private Number userMinY;
    private Number userRangeOrigin;
    private ArrayList<XValueMarker> xValueMarkers;
    private ArrayList<YValueMarker> yValueMarkers;

    public XYPlot(Context context, String title) {
        super(context, title);
    }

    public XYPlot(Context context, String title, RenderMode mode) {
        super(context, title, mode);
    }

    public XYPlot(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public XYPlot(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onPreInit() {
        this.legendWidget = new XYLegendWidget(getLayoutManager(), this, new SizeMetrics(PixelUtils.dpToPix(10.0f), SizeLayoutType.ABSOLUTE, 0.5f, SizeLayoutType.RELATIVE), new DynamicTableModel(0, 1), new SizeMetrics(PixelUtils.dpToPix(7.0f), SizeLayoutType.ABSOLUTE, PixelUtils.dpToPix(7.0f), SizeLayoutType.ABSOLUTE));
        this.graphWidget = new XYGraphWidget(getLayoutManager(), this, new SizeMetrics(PixelUtils.dpToPix(18.0f), SizeLayoutType.FILL, PixelUtils.dpToPix(10.0f), SizeLayoutType.FILL));
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(-12303292);
        backgroundPaint.setStyle(Style.FILL);
        this.graphWidget.setBackgroundPaint(backgroundPaint);
        this.domainLabelWidget = new TextLabelWidget(getLayoutManager(), new SizeMetrics(PixelUtils.dpToPix(10.0f), SizeLayoutType.ABSOLUTE, PixelUtils.dpToPix(80.0f), SizeLayoutType.ABSOLUTE), TextOrientationType.HORIZONTAL);
        this.rangeLabelWidget = new TextLabelWidget(getLayoutManager(), new SizeMetrics(PixelUtils.dpToPix(50.0f), SizeLayoutType.ABSOLUTE, PixelUtils.dpToPix(10.0f), SizeLayoutType.ABSOLUTE), TextOrientationType.VERTICAL_ASCENDING);
        this.legendWidget.position(PixelUtils.dpToPix(40.0f), XLayoutStyle.ABSOLUTE_FROM_RIGHT, PixelUtils.dpToPix(0.0f), YLayoutStyle.ABSOLUTE_FROM_BOTTOM, AnchorPosition.RIGHT_BOTTOM);
        this.graphWidget.position(PixelUtils.dpToPix(0.0f), XLayoutStyle.ABSOLUTE_FROM_RIGHT, PixelUtils.dpToPix(0.0f), YLayoutStyle.ABSOLUTE_FROM_CENTER, AnchorPosition.RIGHT_MIDDLE);
        this.domainLabelWidget.position(PixelUtils.dpToPix(20.0f), XLayoutStyle.ABSOLUTE_FROM_LEFT, PixelUtils.dpToPix(0.0f), YLayoutStyle.ABSOLUTE_FROM_BOTTOM, AnchorPosition.LEFT_BOTTOM);
        this.rangeLabelWidget.position(PixelUtils.dpToPix(0.0f), XLayoutStyle.ABSOLUTE_FROM_LEFT, PixelUtils.dpToPix(0.0f), YLayoutStyle.ABSOLUTE_FROM_CENTER, AnchorPosition.LEFT_MIDDLE);
        getLayoutManager().moveToTop(getTitleWidget());
        getLayoutManager().moveToTop(getLegendWidget());
        this.graphWidget.setMarginTop(PixelUtils.dpToPix(3.0f));
        this.graphWidget.setMarginBottom(PixelUtils.dpToPix(3.0f));
        this.graphWidget.setMarginLeft(PixelUtils.dpToPix(3.0f));
        this.graphWidget.setMarginRight(PixelUtils.dpToPix(3.0f));
        getDomainLabelWidget().pack();
        getRangeLabelWidget().pack();
        setPlotMarginLeft(PixelUtils.dpToPix(2.0f));
        setPlotMarginRight(PixelUtils.dpToPix(2.0f));
        setPlotMarginBottom(PixelUtils.dpToPix(2.0f));
        this.xValueMarkers = new ArrayList();
        this.yValueMarkers = new ArrayList();
        setDefaultBounds(new RectRegion(Integer.valueOf(-1), Integer.valueOf(1), Integer.valueOf(-1), Integer.valueOf(1)));
    }

    protected void processAttrs(TypedArray attrs) {
        String domainLabelAttr = attrs.getString(0);
        if (domainLabelAttr != null) {
            setDomainLabel(domainLabelAttr);
        }
        String rangeLabelAttr = attrs.getString(2);
        if (rangeLabelAttr != null) {
            setRangeLabel(rangeLabelAttr);
        }
        getDomainLabelWidget().getLabelPaint().setTextSize(attrs.getDimension(1, getDomainLabelWidget().getLabelPaint().getTextSize()));
        getRangeLabelWidget().getLabelPaint().setTextSize(attrs.getDimension(3, getRangeLabelWidget().getLabelPaint().getTextSize()));
        getGraphWidget().setMarginTop(attrs.getDimension(4, getGraphWidget().getMarginTop()));
        getGraphWidget().setMarginBottom(attrs.getDimension(5, getGraphWidget().getMarginBottom()));
        getGraphWidget().setMarginLeft(attrs.getDimension(6, getGraphWidget().getMarginLeft()));
        getGraphWidget().setMarginRight(attrs.getDimension(7, getGraphWidget().getMarginRight()));
    }

    public void setGridPadding(float left, float top, float right, float bottom) {
        getGraphWidget().setGridPaddingTop(top);
        getGraphWidget().setGridPaddingBottom(bottom);
        getGraphWidget().setGridPaddingLeft(left);
        getGraphWidget().setGridPaddingRight(right);
    }

    protected void notifyListenersBeforeDraw(Canvas canvas) {
        super.notifyListenersBeforeDraw(canvas);
        calculateMinMaxVals();
    }

    public boolean containsPoint(float x, float y) {
        if (getGraphWidget().getGridRect() != null) {
            return getGraphWidget().getGridRect().contains(x, y);
        }
        return false;
    }

    public boolean containsPoint(PointF point) {
        return containsPoint(point.x, point.y);
    }

    public void setCursorPosition(PointF point) {
        getGraphWidget().setCursorPosition(point);
    }

    public void setCursorPosition(float x, float y) {
        getGraphWidget().setCursorPosition(x, y);
    }

    public Number getYVal(PointF point) {
        return getGraphWidget().getYVal(point);
    }

    public Number getXVal(PointF point) {
        return getGraphWidget().getXVal(point);
    }

    private boolean isXValWithinView(double xVal) {
        return ((this.userMinY == null || xVal >= this.userMinY.doubleValue()) && this.userMaxY == null) || xVal <= this.userMaxY.doubleValue();
    }

    private boolean isPointVisible(Number x, Number y) {
        if (x == null || y == null || !isValWithinRange(y.doubleValue(), this.userMinY, this.userMaxY) || !isValWithinRange(x.doubleValue(), this.userMinX, this.userMaxX)) {
            return false;
        }
        return true;
    }

    private boolean isValWithinRange(double val, Number min, Number max) {
        boolean isAboveMinThreshold;
        if (min == null || val >= min.doubleValue()) {
            isAboveMinThreshold = true;
        } else {
            isAboveMinThreshold = false;
        }
        boolean isBelowMaxThreshold;
        if (max == null || val <= max.doubleValue()) {
            isBelowMaxThreshold = true;
        } else {
            isBelowMaxThreshold = false;
        }
        if (isAboveMinThreshold && isBelowMaxThreshold) {
            return true;
        }
        return false;
    }

    public void calculateMinMaxVals() {
        this.prevMinX = this.calculatedMinX;
        this.prevMaxX = this.calculatedMaxX;
        this.prevMinY = this.calculatedMinY;
        this.prevMaxY = this.calculatedMaxY;
        this.calculatedMinX = this.userMinX;
        this.calculatedMaxX = this.userMaxX;
        this.calculatedMinY = this.userMinY;
        this.calculatedMaxY = this.userMaxY;
        for (XYSeries series : getSeriesSet()) {
            for (int i = 0; i < series.size(); i++) {
                Number thisX = series.getX(i);
                Number thisY = series.getY(i);
                if (isPointVisible(thisX, thisY)) {
                    if (this.userMinX == null && thisX != null && (this.calculatedMinX == null || thisX.doubleValue() < this.calculatedMinX.doubleValue())) {
                        this.calculatedMinX = thisX;
                    }
                    if (this.userMaxX == null && thisX != null && (this.calculatedMaxX == null || thisX.doubleValue() > this.calculatedMaxX.doubleValue())) {
                        this.calculatedMaxX = thisX;
                    }
                    if (this.userMinY == null && thisY != null && (this.calculatedMinY == null || thisY.doubleValue() < this.calculatedMinY.doubleValue())) {
                        this.calculatedMinY = thisY;
                    }
                    if (this.userMaxY == null && thisY != null && (this.calculatedMaxY == null || thisY.doubleValue() > this.calculatedMaxY.doubleValue())) {
                        this.calculatedMaxY = thisY;
                    }
                }
            }
        }
        switch (this.domainFramingModel) {
            case ORIGIN:
                updateDomainMinMaxForOriginModel();
                break;
            case EDGE:
                updateDomainMinMaxForEdgeModel();
                this.calculatedMinX = ApplyUserMinMax(this.calculatedMinX, this.domainLeftMin, this.domainLeftMax);
                this.calculatedMaxX = ApplyUserMinMax(this.calculatedMaxX, this.domainRightMin, this.domainRightMax);
                break;
            default:
                throw new UnsupportedOperationException("Domain Framing Model not yet supported: " + this.domainFramingModel);
        }
        switch (this.rangeFramingModel) {
            case ORIGIN:
                updateRangeMinMaxForOriginModel();
                break;
            case EDGE:
                if (getSeriesSet().size() > 0) {
                    updateRangeMinMaxForEdgeModel();
                    this.calculatedMinY = ApplyUserMinMax(this.calculatedMinY, this.rangeBottomMin, this.rangeBottomMax);
                    this.calculatedMaxY = ApplyUserMinMax(this.calculatedMaxY, this.rangeTopMin, this.rangeTopMax);
                    break;
                }
                break;
            default:
                throw new UnsupportedOperationException("Range Framing Model not yet supported: " + this.domainFramingModel);
        }
        this.calculatedDomainOrigin = this.userDomainOrigin != null ? this.userDomainOrigin : getCalculatedMinX();
        this.calculatedRangeOrigin = this.userRangeOrigin != null ? this.userRangeOrigin : getCalculatedMinY();
    }

    private void updateDomainMinMaxForEdgeModel() {
        switch (this.domainUpperBoundaryMode) {
            case FIXED:
            case AUTO:
                break;
            case GROW:
                if (this.prevMaxX != null && this.calculatedMaxX.doubleValue() <= this.prevMaxX.doubleValue()) {
                    this.calculatedMaxX = this.prevMaxX;
                    break;
                }
            case SHRINNK:
                if (this.prevMaxX != null && this.calculatedMaxX.doubleValue() >= this.prevMaxX.doubleValue()) {
                    this.calculatedMaxX = this.prevMaxX;
                    break;
                }
            default:
                throw new UnsupportedOperationException("DomainUpperBoundaryMode not yet implemented: " + this.domainUpperBoundaryMode);
        }
        switch (this.domainLowerBoundaryMode) {
            case FIXED:
            case AUTO:
                return;
            case GROW:
                if (this.prevMinX != null && this.calculatedMinX.doubleValue() >= this.prevMinX.doubleValue()) {
                    this.calculatedMinX = this.prevMinX;
                    return;
                }
                return;
            case SHRINNK:
                if (this.prevMinX != null && this.calculatedMinX.doubleValue() <= this.prevMinX.doubleValue()) {
                    this.calculatedMinX = this.prevMinX;
                    return;
                }
                return;
            default:
                throw new UnsupportedOperationException("DomainLowerBoundaryMode not supported: " + this.domainLowerBoundaryMode);
        }
    }

    public void updateRangeMinMaxForEdgeModel() {
        switch (this.rangeUpperBoundaryMode) {
            case FIXED:
            case AUTO:
                break;
            case GROW:
                if (this.prevMaxY != null && this.calculatedMaxY.doubleValue() <= this.prevMaxY.doubleValue()) {
                    this.calculatedMaxY = this.prevMaxY;
                    break;
                }
            case SHRINNK:
                if (this.prevMaxY != null && this.calculatedMaxY.doubleValue() >= this.prevMaxY.doubleValue()) {
                    this.calculatedMaxY = this.prevMaxY;
                    break;
                }
            default:
                throw new UnsupportedOperationException("RangeUpperBoundaryMode not supported: " + this.rangeUpperBoundaryMode);
        }
        switch (this.rangeLowerBoundaryMode) {
            case FIXED:
            case AUTO:
                return;
            case GROW:
                if (this.prevMinY != null && this.calculatedMinY.doubleValue() >= this.prevMinY.doubleValue()) {
                    this.calculatedMinY = this.prevMinY;
                    return;
                }
                return;
            case SHRINNK:
                if (this.prevMinY != null && this.calculatedMinY.doubleValue() <= this.prevMinY.doubleValue()) {
                    this.calculatedMinY = this.prevMinY;
                    return;
                }
                return;
            default:
                throw new UnsupportedOperationException("RangeLowerBoundaryMode not supported: " + this.rangeLowerBoundaryMode);
        }
    }

    private Number ApplyUserMinMax(Number value, Number min, Number max) {
        if (!(min == null || value == null || value.doubleValue() > min.doubleValue())) {
            value = min;
        }
        if (max == null || value == null || value.doubleValue() < max.doubleValue()) {
            return value;
        }
        return max;
    }

    public void centerOnDomainOrigin(Number origin) {
        centerOnDomainOrigin(origin, null, BoundaryMode.AUTO);
    }

    public void centerOnDomainOrigin(Number origin, Number extent, BoundaryMode mode) {
        if (origin == null) {
            throw new NullPointerException("Origin param cannot be null.");
        }
        this.domainFramingModel = XYFramingModel.ORIGIN;
        setUserDomainOrigin(origin);
        this.domainOriginExtent = extent;
        this.domainOriginBoundaryMode = mode;
        if (this.domainOriginBoundaryMode == BoundaryMode.FIXED) {
            double domO = this.userDomainOrigin.doubleValue();
            double domE = this.domainOriginExtent.doubleValue();
            this.userMaxX = Double.valueOf(domO + domE);
            this.userMinX = Double.valueOf(domO - domE);
            return;
        }
        this.userMaxX = null;
        this.userMinX = null;
    }

    public void centerOnRangeOrigin(Number origin) {
        centerOnRangeOrigin(origin, null, BoundaryMode.AUTO);
    }

    public void centerOnRangeOrigin(Number origin, Number extent, BoundaryMode mode) {
        if (origin == null) {
            throw new NullPointerException("Origin param cannot be null.");
        }
        this.rangeFramingModel = XYFramingModel.ORIGIN;
        setUserRangeOrigin(origin);
        this.rangeOriginExtent = extent;
        this.rangeOriginBoundaryMode = mode;
        if (this.rangeOriginBoundaryMode == BoundaryMode.FIXED) {
            double raO = this.userRangeOrigin.doubleValue();
            double raE = this.rangeOriginExtent.doubleValue();
            this.userMaxY = Double.valueOf(raO + raE);
            this.userMinY = Double.valueOf(raO - raE);
            return;
        }
        this.userMaxY = null;
        this.userMinY = null;
    }

    private double distance(double x, double y) {
        if (x > y) {
            return x - y;
        }
        return y - x;
    }

    public void updateDomainMinMaxForOriginModel() {
        double delta;
        double origin = this.userDomainOrigin.doubleValue();
        double maxXDelta = distance(this.calculatedMaxX.doubleValue(), origin);
        double minXDelta = distance(this.calculatedMinX.doubleValue(), origin);
        if (maxXDelta > minXDelta) {
            delta = maxXDelta;
        } else {
            delta = minXDelta;
        }
        double dlb = origin - delta;
        double dub = origin + delta;
        switch (this.domainOriginBoundaryMode) {
            case FIXED:
                return;
            case AUTO:
                this.calculatedMinX = Double.valueOf(dlb);
                this.calculatedMaxX = Double.valueOf(dub);
                return;
            case GROW:
                if (this.prevMinX == null || dlb < this.prevMinX.doubleValue()) {
                    this.calculatedMinX = Double.valueOf(dlb);
                } else {
                    this.calculatedMinX = this.prevMinX;
                }
                if (this.prevMaxX == null || dub > this.prevMaxX.doubleValue()) {
                    this.calculatedMaxX = Double.valueOf(dub);
                    return;
                } else {
                    this.calculatedMaxX = this.prevMaxX;
                    return;
                }
            case SHRINNK:
                if (this.prevMinX == null || dlb > this.prevMinX.doubleValue()) {
                    this.calculatedMinX = Double.valueOf(dlb);
                } else {
                    this.calculatedMinX = this.prevMinX;
                }
                if (this.prevMaxX == null || dub < this.prevMaxX.doubleValue()) {
                    this.calculatedMaxX = Double.valueOf(dub);
                    return;
                } else {
                    this.calculatedMaxX = this.prevMaxX;
                    return;
                }
            default:
                throw new UnsupportedOperationException("Domain Origin Boundary Mode not yet supported: " + this.domainOriginBoundaryMode);
        }
    }

    public void updateRangeMinMaxForOriginModel() {
        switch (this.rangeOriginBoundaryMode) {
            case AUTO:
                double origin = this.userRangeOrigin.doubleValue();
                double maxYDelta = distance(this.calculatedMaxY.doubleValue(), origin);
                double minYDelta = distance(this.calculatedMinY.doubleValue(), origin);
                if (maxYDelta > minYDelta) {
                    this.calculatedMinY = Double.valueOf(origin - maxYDelta);
                    this.calculatedMaxY = Double.valueOf(origin + maxYDelta);
                    return;
                }
                this.calculatedMinY = Double.valueOf(origin - minYDelta);
                this.calculatedMaxY = Double.valueOf(origin + minYDelta);
                return;
            default:
                throw new UnsupportedOperationException("Range Origin Boundary Mode not yet supported: " + this.rangeOriginBoundaryMode);
        }
    }

    public int getTicksPerRangeLabel() {
        return this.graphWidget.getTicksPerRangeLabel();
    }

    public void setTicksPerRangeLabel(int ticksPerRangeLabel) {
        this.graphWidget.setTicksPerRangeLabel(ticksPerRangeLabel);
    }

    public int getTicksPerDomainLabel() {
        return this.graphWidget.getTicksPerDomainLabel();
    }

    public void setTicksPerDomainLabel(int ticksPerDomainLabel) {
        this.graphWidget.setTicksPerDomainLabel(ticksPerDomainLabel);
    }

    public XYStepMode getDomainStepMode() {
        return this.domainStepMode;
    }

    public void setDomainStepMode(XYStepMode domainStepMode) {
        this.domainStepMode = domainStepMode;
    }

    public double getDomainStepValue() {
        return this.domainStepValue;
    }

    public void setDomainStepValue(double domainStepValue) {
        this.domainStepValue = domainStepValue;
    }

    public void setDomainStep(XYStepMode mode, double value) {
        setDomainStepMode(mode);
        setDomainStepValue(value);
    }

    public XYStepMode getRangeStepMode() {
        return this.rangeStepMode;
    }

    public void setRangeStepMode(XYStepMode rangeStepMode) {
        this.rangeStepMode = rangeStepMode;
    }

    public double getRangeStepValue() {
        return this.rangeStepValue;
    }

    public void setRangeStepValue(double rangeStepValue) {
        this.rangeStepValue = rangeStepValue;
    }

    public void setRangeStep(XYStepMode mode, double value) {
        setRangeStepMode(mode);
        setRangeStepValue(value);
    }

    public String getDomainLabel() {
        return getDomainLabelWidget().getText();
    }

    public void setDomainLabel(String domainLabel) {
        getDomainLabelWidget().setText(domainLabel);
    }

    public String getRangeLabel() {
        return getRangeLabelWidget().getText();
    }

    public void setRangeLabel(String rangeLabel) {
        getRangeLabelWidget().setText(rangeLabel);
    }

    public XYLegendWidget getLegendWidget() {
        return this.legendWidget;
    }

    public void setLegendWidget(XYLegendWidget legendWidget) {
        this.legendWidget = legendWidget;
    }

    public XYGraphWidget getGraphWidget() {
        return this.graphWidget;
    }

    public void setGraphWidget(XYGraphWidget graphWidget) {
        this.graphWidget = graphWidget;
    }

    public TextLabelWidget getDomainLabelWidget() {
        return this.domainLabelWidget;
    }

    public void setDomainLabelWidget(TextLabelWidget domainLabelWidget) {
        this.domainLabelWidget = domainLabelWidget;
    }

    public TextLabelWidget getRangeLabelWidget() {
        return this.rangeLabelWidget;
    }

    public void setRangeLabelWidget(TextLabelWidget rangeLabelWidget) {
        this.rangeLabelWidget = rangeLabelWidget;
    }

    public Format getRangeValueFormat() {
        return this.graphWidget.getRangeValueFormat();
    }

    public void setRangeValueFormat(Format rangeValueFormat) {
        this.graphWidget.setRangeValueFormat(rangeValueFormat);
    }

    public Format getDomainValueFormat() {
        return this.graphWidget.getDomainValueFormat();
    }

    public void setDomainValueFormat(Format domainValueFormat) {
        this.graphWidget.setDomainValueFormat(domainValueFormat);
    }

    public synchronized void setDomainBoundaries(Number lowerBoundary, Number upperBoundary, BoundaryMode mode) {
        setDomainBoundaries(lowerBoundary, mode, upperBoundary, mode);
    }

    public synchronized void setDomainBoundaries(Number lowerBoundary, BoundaryMode lowerBoundaryMode, Number upperBoundary, BoundaryMode upperBoundaryMode) {
        setDomainLowerBoundary(lowerBoundary, lowerBoundaryMode);
        setDomainUpperBoundary(upperBoundary, upperBoundaryMode);
    }

    public synchronized void setRangeBoundaries(Number lowerBoundary, Number upperBoundary, BoundaryMode mode) {
        setRangeBoundaries(lowerBoundary, mode, upperBoundary, mode);
    }

    public synchronized void setRangeBoundaries(Number lowerBoundary, BoundaryMode lowerBoundaryMode, Number upperBoundary, BoundaryMode upperBoundaryMode) {
        setRangeLowerBoundary(lowerBoundary, lowerBoundaryMode);
        setRangeUpperBoundary(upperBoundary, upperBoundaryMode);
    }

    protected synchronized void setDomainUpperBoundaryMode(BoundaryMode mode) {
        this.domainUpperBoundaryMode = mode;
    }

    protected synchronized void setUserMaxX(Number boundary) {
        this.userMaxX = boundary;
    }

    public synchronized void setDomainUpperBoundary(Number boundary, BoundaryMode mode) {
        if (mode != BoundaryMode.FIXED) {
            boundary = null;
        }
        setUserMaxX(boundary);
        setDomainUpperBoundaryMode(mode);
        setDomainFramingModel(XYFramingModel.EDGE);
    }

    protected synchronized void setDomainLowerBoundaryMode(BoundaryMode mode) {
        this.domainLowerBoundaryMode = mode;
    }

    protected synchronized void setUserMinX(Number boundary) {
        this.userMinX = boundary;
    }

    public synchronized void setDomainLowerBoundary(Number boundary, BoundaryMode mode) {
        if (mode != BoundaryMode.FIXED) {
            boundary = null;
        }
        setUserMinX(boundary);
        setDomainLowerBoundaryMode(mode);
        setDomainFramingModel(XYFramingModel.EDGE);
    }

    protected synchronized void setRangeUpperBoundaryMode(BoundaryMode mode) {
        this.rangeUpperBoundaryMode = mode;
    }

    protected synchronized void setUserMaxY(Number boundary) {
        this.userMaxY = boundary;
    }

    public synchronized void setRangeUpperBoundary(Number boundary, BoundaryMode mode) {
        if (mode != BoundaryMode.FIXED) {
            boundary = null;
        }
        setUserMaxY(boundary);
        setRangeUpperBoundaryMode(mode);
        setRangeFramingModel(XYFramingModel.EDGE);
    }

    protected synchronized void setRangeLowerBoundaryMode(BoundaryMode mode) {
        this.rangeLowerBoundaryMode = mode;
    }

    protected synchronized void setUserMinY(Number boundary) {
        this.userMinY = boundary;
    }

    public synchronized void setRangeLowerBoundary(Number boundary, BoundaryMode mode) {
        if (mode != BoundaryMode.FIXED) {
            boundary = null;
        }
        setUserMinY(boundary);
        setRangeLowerBoundaryMode(mode);
        setRangeFramingModel(XYFramingModel.EDGE);
    }

    private Number getUserMinX() {
        return this.userMinX;
    }

    private Number getUserMaxX() {
        return this.userMaxX;
    }

    private Number getUserMinY() {
        return this.userMinY;
    }

    private Number getUserMaxY() {
        return this.userMaxY;
    }

    public Number getDomainOrigin() {
        return this.calculatedDomainOrigin;
    }

    public Number getRangeOrigin() {
        return this.calculatedRangeOrigin;
    }

    protected BoundaryMode getDomainUpperBoundaryMode() {
        return this.domainUpperBoundaryMode;
    }

    protected BoundaryMode getDomainLowerBoundaryMode() {
        return this.domainLowerBoundaryMode;
    }

    protected BoundaryMode getRangeUpperBoundaryMode() {
        return this.rangeUpperBoundaryMode;
    }

    protected BoundaryMode getRangeLowerBoundaryMode() {
        return this.rangeLowerBoundaryMode;
    }

    public synchronized void setUserDomainOrigin(Number origin) {
        if (origin == null) {
            throw new NullPointerException("Origin value cannot be null.");
        }
        this.userDomainOrigin = origin;
    }

    public synchronized void setUserRangeOrigin(Number origin) {
        if (origin == null) {
            throw new NullPointerException("Origin value cannot be null.");
        }
        this.userRangeOrigin = origin;
    }

    public XYFramingModel getDomainFramingModel() {
        return this.domainFramingModel;
    }

    protected void setDomainFramingModel(XYFramingModel domainFramingModel) {
        this.domainFramingModel = domainFramingModel;
    }

    public XYFramingModel getRangeFramingModel() {
        return this.rangeFramingModel;
    }

    protected void setRangeFramingModel(XYFramingModel rangeFramingModel) {
        this.rangeFramingModel = rangeFramingModel;
    }

    public Number getCalculatedMinX() {
        return this.calculatedMinX != null ? this.calculatedMinX : getDefaultBounds().getMinX();
    }

    public Number getCalculatedMaxX() {
        return this.calculatedMaxX != null ? this.calculatedMaxX : getDefaultBounds().getMaxX();
    }

    public Number getCalculatedMinY() {
        return this.calculatedMinY != null ? this.calculatedMinY : getDefaultBounds().getMinY();
    }

    public Number getCalculatedMaxY() {
        return this.calculatedMaxY != null ? this.calculatedMaxY : getDefaultBounds().getMaxY();
    }

    public boolean isDrawDomainOriginEnabled() {
        return this.drawDomainOriginEnabled;
    }

    public void setDrawDomainOriginEnabled(boolean drawDomainOriginEnabled) {
        this.drawDomainOriginEnabled = drawDomainOriginEnabled;
    }

    public boolean isDrawRangeOriginEnabled() {
        return this.drawRangeOriginEnabled;
    }

    public void setDrawRangeOriginEnabled(boolean drawRangeOriginEnabled) {
        this.drawRangeOriginEnabled = drawRangeOriginEnabled;
    }

    public boolean addMarker(YValueMarker marker) {
        if (this.yValueMarkers.contains(marker)) {
            return false;
        }
        return this.yValueMarkers.add(marker);
    }

    public YValueMarker removeMarker(YValueMarker marker) {
        int markerIndex = this.yValueMarkers.indexOf(marker);
        if (markerIndex == -1) {
            return null;
        }
        return (YValueMarker) this.yValueMarkers.remove(markerIndex);
    }

    public int removeMarkers() {
        return removeXMarkers() + removeYMarkers();
    }

    public int removeYMarkers() {
        int numMarkersRemoved = this.yValueMarkers.size();
        this.yValueMarkers.clear();
        return numMarkersRemoved;
    }

    public boolean addMarker(XValueMarker marker) {
        return !this.xValueMarkers.contains(marker) && this.xValueMarkers.add(marker);
    }

    public XValueMarker removeMarker(XValueMarker marker) {
        int markerIndex = this.xValueMarkers.indexOf(marker);
        if (markerIndex == -1) {
            return null;
        }
        return (XValueMarker) this.xValueMarkers.remove(markerIndex);
    }

    public int removeXMarkers() {
        int numMarkersRemoved = this.xValueMarkers.size();
        this.xValueMarkers.clear();
        return numMarkersRemoved;
    }

    protected List<YValueMarker> getYValueMarkers() {
        return this.yValueMarkers;
    }

    protected List<XValueMarker> getXValueMarkers() {
        return this.xValueMarkers;
    }

    public RectRegion getDefaultBounds() {
        return this.defaultBounds;
    }

    public void setDefaultBounds(RectRegion defaultBounds) {
        this.defaultBounds = defaultBounds;
    }

    public Number getRangeTopMin() {
        return this.rangeTopMin;
    }

    public synchronized void setRangeTopMin(Number rangeTopMin) {
        this.rangeTopMin = rangeTopMin;
    }

    public Number getRangeTopMax() {
        return this.rangeTopMax;
    }

    public synchronized void setRangeTopMax(Number rangeTopMax) {
        this.rangeTopMax = rangeTopMax;
    }

    public Number getRangeBottomMin() {
        return this.rangeBottomMin;
    }

    public synchronized void setRangeBottomMin(Number rangeBottomMin) {
        this.rangeBottomMin = rangeBottomMin;
    }

    public Number getRangeBottomMax() {
        return this.rangeBottomMax;
    }

    public synchronized void setRangeBottomMax(Number rangeBottomMax) {
        this.rangeBottomMax = rangeBottomMax;
    }

    public Number getDomainLeftMin() {
        return this.domainLeftMin;
    }

    public synchronized void setDomainLeftMin(Number domainLeftMin) {
        this.domainLeftMin = domainLeftMin;
    }

    public Number getDomainLeftMax() {
        return this.domainLeftMax;
    }

    public synchronized void setDomainLeftMax(Number domainLeftMax) {
        this.domainLeftMax = domainLeftMax;
    }

    public Number getDomainRightMin() {
        return this.domainRightMin;
    }

    public synchronized void setDomainRightMin(Number domainRightMin) {
        this.domainRightMin = domainRightMin;
    }

    public Number getDomainRightMax() {
        return this.domainRightMax;
    }

    public synchronized void setDomainRightMax(Number domainRightMax) {
        this.domainRightMax = domainRightMax;
    }
}
