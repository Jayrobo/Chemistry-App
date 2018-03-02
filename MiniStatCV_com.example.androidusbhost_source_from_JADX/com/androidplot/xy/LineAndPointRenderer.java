package com.androidplot.xy;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Pair;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.util.ValPixConverter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LineAndPointRenderer<FormatterType extends LineAndPointFormatter> extends XYSeriesRenderer<FormatterType> {
    public LineAndPointRenderer(XYPlot plot) {
        super(plot);
    }

    public void onRender(Canvas canvas, RectF plotArea) throws PlotRenderException {
        List<XYSeries> seriesList = ((XYPlot) getPlot()).getSeriesListForRenderer(getClass());
        if (seriesList != null) {
            for (XYSeries series : seriesList) {
                drawSeries(canvas, plotArea, series, (LineAndPointFormatter) getFormatter(series));
            }
        }
    }

    public void doDrawLegendIcon(Canvas canvas, RectF rect, LineAndPointFormatter formatter) {
        float centerY = rect.centerY();
        float centerX = rect.centerX();
        if (formatter.getFillPaint() != null) {
            canvas.drawRect(rect, formatter.getFillPaint());
        }
        if (formatter.getLinePaint() != null) {
            canvas.drawLine(rect.left, rect.bottom, rect.right, rect.top, formatter.getLinePaint());
        }
        if (formatter.getVertexPaint() != null) {
            canvas.drawPoint(centerX, centerY, formatter.getVertexPaint());
        }
    }

    protected void appendToPath(Path path, PointF thisPoint, PointF lastPoint) {
        path.lineTo(thisPoint.x, thisPoint.y);
    }

    protected void drawSeries(Canvas canvas, RectF plotArea, XYSeries series, LineAndPointFormatter formatter) {
        PointF lastPoint = null;
        PointF firstPoint = null;
        Paint linePaint = formatter.getLinePaint();
        Path path = null;
        ArrayList<Pair<PointF, Integer>> arrayList = new ArrayList(series.size());
        for (int i = 0; i < series.size(); i++) {
            PointF thisPoint;
            Number y = series.getY(i);
            Number x = series.getX(i);
            if (y == null || x == null) {
                thisPoint = null;
            } else {
                thisPoint = ValPixConverter.valToPix(x, y, plotArea, ((XYPlot) getPlot()).getCalculatedMinX(), ((XYPlot) getPlot()).getCalculatedMaxX(), ((XYPlot) getPlot()).getCalculatedMinY(), ((XYPlot) getPlot()).getCalculatedMaxY());
                arrayList.add(new Pair(thisPoint, Integer.valueOf(i)));
            }
            if (linePaint == null || thisPoint == null) {
                if (lastPoint != null) {
                    renderPath(canvas, plotArea, path, firstPoint, lastPoint, formatter);
                }
                firstPoint = null;
                lastPoint = null;
            } else {
                if (firstPoint == null) {
                    path = new Path();
                    firstPoint = thisPoint;
                    path.moveTo(firstPoint.x, firstPoint.y);
                }
                if (lastPoint != null) {
                    appendToPath(path, thisPoint, lastPoint);
                }
                lastPoint = thisPoint;
            }
        }
        if (!(linePaint == null || firstPoint == null)) {
            renderPath(canvas, plotArea, path, firstPoint, lastPoint, formatter);
        }
        Paint vertexPaint = formatter.getVertexPaint();
        PointLabelFormatter plf = formatter.getPointLabelFormatter();
        if (vertexPaint != null || plf != null) {
            Iterator i$ = arrayList.iterator();
            while (i$.hasNext()) {
                Pair<PointF, Integer> p = (Pair) i$.next();
                PointLabeler pointLabeler = formatter.getPointLabeler();
                if (vertexPaint != null) {
                    canvas.drawPoint(((PointF) p.first).x, ((PointF) p.first).y, formatter.getVertexPaint());
                }
                if (!(plf == null || pointLabeler == null)) {
                    Canvas canvas2 = canvas;
                    canvas2.drawText(pointLabeler.getLabel(series, ((Integer) p.second).intValue()), plf.hOffset + ((PointF) p.first).x, ((PointF) p.first).y + plf.vOffset, plf.getTextPaint());
                }
            }
        }
    }

    protected void renderPath(Canvas canvas, RectF plotArea, Path path, PointF firstPoint, PointF lastPoint, LineAndPointFormatter formatter) {
        Path path2 = new Path(path);
        switch (formatter.getFillDirection()) {
            case BOTTOM:
                path.lineTo(lastPoint.x, plotArea.bottom);
                path.lineTo(firstPoint.x, plotArea.bottom);
                path.close();
                break;
            case TOP:
                path.lineTo(lastPoint.x, plotArea.top);
                path.lineTo(firstPoint.x, plotArea.top);
                path.close();
                break;
            case RANGE_ORIGIN:
                float originPix = ValPixConverter.valToPix(((XYPlot) getPlot()).getRangeOrigin().doubleValue(), ((XYPlot) getPlot()).getCalculatedMinY().doubleValue(), ((XYPlot) getPlot()).getCalculatedMaxY().doubleValue(), plotArea.height(), true) + plotArea.top;
                path.lineTo(lastPoint.x, originPix);
                path.lineTo(firstPoint.x, originPix);
                path.close();
                break;
            default:
                throw new UnsupportedOperationException("Fill direction not yet implemented: " + formatter.getFillDirection());
        }
        if (formatter.getFillPaint() != null) {
            canvas.drawPath(path, formatter.getFillPaint());
        }
        double minX = ((XYPlot) getPlot()).getCalculatedMinX().doubleValue();
        double maxX = ((XYPlot) getPlot()).getCalculatedMaxX().doubleValue();
        double minY = ((XYPlot) getPlot()).getCalculatedMinY().doubleValue();
        double maxY = ((XYPlot) getPlot()).getCalculatedMaxY().doubleValue();
        for (RectRegion r : RectRegion.regionsWithin(formatter.getRegions().elements(), Double.valueOf(minX), Double.valueOf(maxX), Double.valueOf(minY), Double.valueOf(maxY))) {
            XYRegionFormatter f = formatter.getRegionFormatter(r);
            RectF regionRect = r.getRectF(plotArea, Double.valueOf(minX), Double.valueOf(maxX), Double.valueOf(minY), Double.valueOf(maxY));
            if (regionRect != null) {
                try {
                    canvas.save(31);
                    canvas.clipPath(path);
                    canvas.drawRect(regionRect, f.getPaint());
                } finally {
                    canvas.restore();
                }
            }
        }
        if (formatter.getLinePaint() != null) {
            canvas.drawPath(path2, formatter.getLinePaint());
        }
        path.rewind();
    }
}
