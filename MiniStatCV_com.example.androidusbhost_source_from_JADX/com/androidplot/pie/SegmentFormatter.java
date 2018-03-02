package com.androidplot.pie;

import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import com.androidplot.ui.Formatter;
import com.androidplot.ui.SeriesRenderer;

public class SegmentFormatter extends Formatter<PieChart> {
    private static final int DEFAULT_EDGE_COLOR = -16777216;
    private static final float DEFAULT_EDGE_THICKNESS = 3.0f;
    private static final int DEFAULT_FILL_COLOR = 0;
    private static final int DEFAULT_LABEL_COLOR = -1;
    private static final float DEFAULT_LABEL_FONT_SIZE = 18.0f;
    private static final float DEFAULT_LABEL_MARKER_THICKNESS = 3.0f;
    private Paint fillPaint;
    private Paint innerEdgePaint;
    private Paint labelMarkerPaint;
    private Paint labelPaint;
    private Paint outerEdgePaint;
    private Paint radialEdgePaint;

    public SegmentFormatter() {
        setFillPaint(new Paint());
        setOuterEdgePaint(new Paint());
        getOuterEdgePaint().setStyle(Style.STROKE);
        getOuterEdgePaint().setStrokeWidth(3.0f);
        getOuterEdgePaint().setAntiAlias(true);
        setInnerEdgePaint(new Paint());
        getInnerEdgePaint().setStyle(Style.STROKE);
        getInnerEdgePaint().setStrokeWidth(3.0f);
        getInnerEdgePaint().setAntiAlias(true);
        setRadialEdgePaint(new Paint());
        getRadialEdgePaint().setStyle(Style.STROKE);
        getRadialEdgePaint().setStrokeWidth(3.0f);
        getRadialEdgePaint().setAntiAlias(true);
        setLabelPaint(new Paint());
        getLabelPaint().setColor(-1);
        getLabelPaint().setTextSize(DEFAULT_LABEL_FONT_SIZE);
        getLabelPaint().setAntiAlias(true);
        getLabelPaint().setTextAlign(Align.CENTER);
        setLabelMarkerPaint(new Paint());
        getLabelMarkerPaint().setColor(-1);
        getLabelMarkerPaint().setStrokeWidth(3.0f);
    }

    public SegmentFormatter(Integer fillColor) {
        setFillPaint(new Paint());
        setOuterEdgePaint(new Paint());
        getOuterEdgePaint().setStyle(Style.STROKE);
        getOuterEdgePaint().setStrokeWidth(3.0f);
        getOuterEdgePaint().setAntiAlias(true);
        setInnerEdgePaint(new Paint());
        getInnerEdgePaint().setStyle(Style.STROKE);
        getInnerEdgePaint().setStrokeWidth(3.0f);
        getInnerEdgePaint().setAntiAlias(true);
        setRadialEdgePaint(new Paint());
        getRadialEdgePaint().setStyle(Style.STROKE);
        getRadialEdgePaint().setStrokeWidth(3.0f);
        getRadialEdgePaint().setAntiAlias(true);
        setLabelPaint(new Paint());
        getLabelPaint().setColor(-1);
        getLabelPaint().setTextSize(DEFAULT_LABEL_FONT_SIZE);
        getLabelPaint().setAntiAlias(true);
        getLabelPaint().setTextAlign(Align.CENTER);
        setLabelMarkerPaint(new Paint());
        getLabelMarkerPaint().setColor(-1);
        getLabelMarkerPaint().setStrokeWidth(3.0f);
        if (fillColor != null) {
            getFillPaint().setColor(fillColor.intValue());
        } else {
            getFillPaint().setColor(0);
        }
    }

    public SegmentFormatter(Integer fillColor, Integer borderColor) {
        this(fillColor);
        getInnerEdgePaint().setColor(borderColor.intValue());
        getOuterEdgePaint().setColor(borderColor.intValue());
        getRadialEdgePaint().setColor(borderColor.intValue());
    }

    public SegmentFormatter(Integer fillColor, Integer outerEdgeColor, Integer innerEdgeColor, Integer radialEdgeColor) {
        this(fillColor);
        if (getOuterEdgePaint() != null) {
            getOuterEdgePaint().setColor(outerEdgeColor.intValue());
        } else {
            this.outerEdgePaint = new Paint();
            getOuterEdgePaint().setColor(DEFAULT_EDGE_COLOR);
        }
        if (getInnerEdgePaint() != null) {
            getInnerEdgePaint().setColor(innerEdgeColor.intValue());
        } else {
            this.outerEdgePaint = new Paint();
            getInnerEdgePaint().setColor(DEFAULT_EDGE_COLOR);
        }
        if (getRadialEdgePaint() != null) {
            getRadialEdgePaint().setColor(radialEdgeColor.intValue());
            return;
        }
        this.radialEdgePaint = new Paint();
        getRadialEdgePaint().setColor(DEFAULT_EDGE_COLOR);
    }

    public Class<? extends SeriesRenderer> getRendererClass() {
        return PieRenderer.class;
    }

    public SeriesRenderer getRendererInstance(PieChart plot) {
        return new PieRenderer(plot);
    }

    public Paint getInnerEdgePaint() {
        return this.innerEdgePaint;
    }

    public void setInnerEdgePaint(Paint innerEdgePaint) {
        this.innerEdgePaint = innerEdgePaint;
    }

    public Paint getOuterEdgePaint() {
        return this.outerEdgePaint;
    }

    public void setOuterEdgePaint(Paint outerEdgePaint) {
        this.outerEdgePaint = outerEdgePaint;
    }

    public Paint getRadialEdgePaint() {
        return this.radialEdgePaint;
    }

    public void setRadialEdgePaint(Paint radialEdgePaint) {
        this.radialEdgePaint = radialEdgePaint;
    }

    public Paint getFillPaint() {
        return this.fillPaint;
    }

    public void setFillPaint(Paint fillPaint) {
        this.fillPaint = fillPaint;
    }

    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    public void setLabelPaint(Paint labelPaint) {
        this.labelPaint = labelPaint;
    }

    public Paint getLabelMarkerPaint() {
        return this.labelMarkerPaint;
    }

    public void setLabelMarkerPaint(Paint labelMarkerPaint) {
        this.labelMarkerPaint = labelMarkerPaint;
    }
}
