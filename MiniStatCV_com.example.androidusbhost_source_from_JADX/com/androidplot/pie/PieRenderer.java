package com.androidplot.pie;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region.Op;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.SeriesRenderer;
import java.util.Set;

public class PieRenderer extends SeriesRenderer<PieChart, Segment, SegmentFormatter> {
    private DonutMode donutMode = DonutMode.PERCENT;
    private float donutSize = 0.5f;
    private float endDeg = 360.0f;
    private float startDeg = 0.0f;

    public enum DonutMode {
        PERCENT,
        DP,
        PIXELS
    }

    public PieRenderer(PieChart plot) {
        super(plot);
    }

    public float getRadius(RectF rect) {
        return rect.width() < rect.height() ? rect.width() / 2.0f : rect.height() / 2.0f;
    }

    public void onRender(Canvas canvas, RectF plotArea) throws PlotRenderException {
        float radius = getRadius(plotArea);
        PointF origin = new PointF(plotArea.centerX(), plotArea.centerY());
        double[] values = getValues();
        double scale = calculateScale(values);
        float offset = this.startDeg;
        Set<Segment> segments = ((PieChart) getPlot()).getSeriesSet();
        RectF rec = new RectF(origin.x - radius, origin.y - radius, origin.x + radius, origin.y + radius);
        int i = 0;
        for (Segment segment : segments) {
            float lastOffset = offset;
            float sweep = (float) ((values[i] * scale) * 360.0d);
            offset += sweep;
            drawSegment(canvas, rec, segment, (SegmentFormatter) ((PieChart) getPlot()).getFormatter(segment, getClass()), radius, lastOffset, sweep);
            i++;
        }
    }

    protected void drawSegment(Canvas canvas, RectF bounds, Segment seg, SegmentFormatter f, float rad, float startAngle, float sweep) {
        float donutSizePx;
        canvas.save();
        float cx = bounds.centerX();
        float cy = bounds.centerY();
        switch (this.donutMode) {
            case PERCENT:
                donutSizePx = this.donutSize * rad;
                break;
            case PIXELS:
                if (this.donutSize > 0.0f) {
                    donutSizePx = this.donutSize;
                } else {
                    donutSizePx = rad + this.donutSize;
                }
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented.");
        }
        if (Math.abs(sweep - 360.0f) > Float.MIN_VALUE) {
            PointF r1Outer = calculateLineEnd(cx, cy, rad, startAngle);
            PointF r1Inner = calculateLineEnd(cx, cy, donutSizePx, startAngle);
            PointF r2Outer = calculateLineEnd(cx, cy, rad, startAngle + sweep);
            PointF r2Inner = calculateLineEnd(cx, cy, donutSizePx, startAngle + sweep);
            Path clip = new Path();
            clip.arcTo(new RectF(bounds.left - rad, bounds.top - rad, bounds.right + rad, bounds.bottom + rad), startAngle, sweep);
            clip.lineTo(cx, cy);
            clip.close();
            canvas.clipPath(clip);
            Path p = new Path();
            p.arcTo(bounds, startAngle, sweep);
            p.lineTo(r2Inner.x, r2Inner.y);
            Path path = p;
            path.arcTo(new RectF(cx - donutSizePx, cy - donutSizePx, cx + donutSizePx, cy + donutSizePx), startAngle + sweep, -sweep);
            p.close();
            canvas.drawPath(p, f.getFillPaint());
            canvas.drawLine(r1Inner.x, r1Inner.y, r1Outer.x, r1Outer.y, f.getRadialEdgePaint());
            canvas.drawLine(r2Inner.x, r2Inner.y, r2Outer.x, r2Outer.y, f.getRadialEdgePaint());
        } else {
            canvas.save(2);
            Path chart = new Path();
            chart.addCircle(cx, cy, rad, Direction.CW);
            Path inside = new Path();
            inside.addCircle(cx, cy, donutSizePx, Direction.CW);
            canvas.clipPath(inside, Op.DIFFERENCE);
            canvas.drawPath(chart, f.getFillPaint());
            canvas.restore();
        }
        canvas.drawCircle(cx, cy, donutSizePx, f.getInnerEdgePaint());
        canvas.drawCircle(cx, cy, rad, f.getOuterEdgePaint());
        canvas.restore();
        drawSegmentLabel(canvas, calculateLineEnd(cx, cy, rad - ((rad - donutSizePx) / 2.0f), (sweep / 2.0f) + startAngle), seg, f);
    }

    protected void drawSegmentLabel(Canvas canvas, PointF origin, Segment seg, SegmentFormatter f) {
        canvas.drawText(seg.getTitle(), origin.x, origin.y, f.getLabelPaint());
    }

    protected void doDrawLegendIcon(Canvas canvas, RectF rect, SegmentFormatter formatter) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    protected double calculateScale(double[] values) {
        double total = 0.0d;
        for (double d : values) {
            total += d;
        }
        return 1.0d / total;
    }

    protected double[] getValues() {
        double[] result = new double[((PieChart) getPlot()).getSeriesSet().size()];
        int i = 0;
        for (Segment seg : ((PieChart) getPlot()).getSeriesSet()) {
            result[i] = seg.getValue().doubleValue();
            i++;
        }
        return result;
    }

    protected PointF calculateLineEnd(float x, float y, float rad, float deg) {
        return calculateLineEnd(new PointF(x, y), rad, deg);
    }

    protected PointF calculateLineEnd(PointF origin, float rad, float deg) {
        double radians = (((double) deg) * 3.141592653589793d) / 180.0d;
        return new PointF(origin.x + ((float) (((double) rad) * Math.cos(radians))), origin.y + ((float) (((double) rad) * Math.sin(radians))));
    }

    public void setDonutSize(float size, DonutMode mode) {
        switch (mode) {
            case PERCENT:
                if (size < 0.0f || size > 1.0f) {
                    throw new IllegalArgumentException("Size parameter must be between 0 and 1 when operating in PERCENT mode.");
                }
            case PIXELS:
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented.");
        }
        this.donutMode = mode;
        this.donutSize = size;
    }

    public Segment getContainingSegment(PointF point) {
        RectF plotArea = ((PieChart) getPlot()).getPieWidget().getWidgetDimensions().marginatedRect;
        PointF origin = new PointF(plotArea.centerX(), plotArea.centerY());
        double angle = Math.atan2((double) (point.y - origin.y), (double) (point.x - origin.x)) * 57.29577951308232d;
        if (angle < 0.0d) {
            angle += 360.0d;
        }
        Set<Segment> segments = ((PieChart) getPlot()).getSeriesSet();
        int i = 0;
        double[] values = getValues();
        double scale = calculateScale(values);
        float offset = this.startDeg;
        for (Segment segment : segments) {
            float lastOffset = offset;
            offset += (float) ((values[i] * scale) * 360.0d);
            if (angle >= ((double) lastOffset) && angle <= ((double) offset)) {
                return segment;
            }
            i++;
        }
        return null;
    }

    public void setStartDeg(float deg) {
        this.startDeg = deg;
    }

    public float getStartDeg() {
        return this.startDeg;
    }

    public void setEndDeg(float deg) {
        this.endDeg = deg;
    }

    public float getEndDeg() {
        return this.endDeg;
    }
}
