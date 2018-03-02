package com.androidplot.xy;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.SeriesAndFormatterList;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TableModel;
import com.androidplot.ui.widget.Widget;
import com.androidplot.util.FontUtils;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

public class XYLegendWidget extends Widget {
    private static final RegionEntryComparator regionEntryComparator = new RegionEntryComparator();
    private boolean drawIconBackgroundEnabled = true;
    private boolean drawIconBorderEnabled = true;
    private Paint iconBorderPaint;
    private SizeMetrics iconSizeMetrics;
    private XYPlot plot;
    private TableModel tableModel;
    private Paint textPaint = new Paint();

    private enum CellType {
        SERIES,
        REGION
    }

    private static class RegionEntryComparator implements Comparator<Entry<XYRegionFormatter, String>> {
        private RegionEntryComparator() {
        }

        public int compare(Entry<XYRegionFormatter, String> o1, Entry<XYRegionFormatter, String> o2) {
            return ((String) o1.getValue()).compareTo((String) o2.getValue());
        }
    }

    public XYLegendWidget(LayoutManager layoutManager, XYPlot plot, SizeMetrics widgetSizeMetrics, TableModel tableModel, SizeMetrics iconSizeMetrics) {
        super(layoutManager, widgetSizeMetrics);
        this.textPaint.setColor(-3355444);
        this.textPaint.setAntiAlias(true);
        this.iconBorderPaint = new Paint();
        this.iconBorderPaint.setStyle(Style.STROKE);
        this.plot = plot;
        setTableModel(tableModel);
        this.iconSizeMetrics = iconSizeMetrics;
    }

    public synchronized void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;
    }

    private RectF getIconRect(RectF cellRect) {
        float cellRectCenterY = cellRect.top + (cellRect.height() / 2.0f);
        RectF iconRect = this.iconSizeMetrics.getRectF(cellRect);
        iconRect.offsetTo(cellRect.left + 1.0f, cellRectCenterY - (iconRect.height() / 2.0f));
        return iconRect;
    }

    private static float getRectCenterY(RectF cellRect) {
        return cellRect.top + (cellRect.height() / 2.0f);
    }

    private void beginDrawingCell(Canvas canvas, RectF iconRect) {
        Paint bgPaint = this.plot.getGraphWidget().getGridBackgroundPaint();
        if (this.drawIconBackgroundEnabled && bgPaint != null) {
            canvas.drawRect(iconRect, bgPaint);
        }
    }

    private void finishDrawingCell(Canvas canvas, RectF cellRect, RectF iconRect, String text) {
        Paint bgPaint = this.plot.getGraphWidget().getGridBackgroundPaint();
        if (this.drawIconBorderEnabled && bgPaint != null) {
            this.iconBorderPaint.setColor(bgPaint.getColor());
            canvas.drawRect(iconRect, this.iconBorderPaint);
        }
        float centeredTextOriginY = getRectCenterY(cellRect) + (FontUtils.getFontHeight(this.textPaint) / 2.0f);
        if (this.textPaint.getTextAlign().equals(Align.RIGHT)) {
            canvas.drawText(text, iconRect.left - 2.0f, centeredTextOriginY, this.textPaint);
        } else {
            canvas.drawText(text, iconRect.right + 2.0f, centeredTextOriginY, this.textPaint);
        }
    }

    protected void drawRegionLegendIcon(Canvas canvas, RectF rect, XYRegionFormatter formatter) {
        canvas.drawRect(rect, formatter.getPaint());
    }

    private void drawRegionLegendCell(Canvas canvas, XYRegionFormatter formatter, RectF cellRect, String text) {
        RectF iconRect = getIconRect(cellRect);
        beginDrawingCell(canvas, iconRect);
        drawRegionLegendIcon(canvas, iconRect, formatter);
        finishDrawingCell(canvas, cellRect, iconRect, text);
    }

    private void drawSeriesLegendCell(Canvas canvas, XYSeriesRenderer renderer, XYSeriesFormatter formatter, RectF cellRect, String seriesTitle) {
        RectF iconRect = getIconRect(cellRect);
        beginDrawingCell(canvas, iconRect);
        renderer.drawSeriesLegendIcon(canvas, iconRect, formatter);
        finishDrawingCell(canvas, cellRect, iconRect, seriesTitle);
    }

    protected synchronized void doOnDraw(Canvas canvas, RectF widgetRect) {
        if (!this.plot.isEmpty()) {
            TreeSet<Entry<XYRegionFormatter, String>> sortedRegions = new TreeSet(new RegionEntryComparator());
            int seriesCount = 0;
            for (XYSeriesRenderer renderer : this.plot.getRendererList()) {
                SeriesAndFormatterList sfl = this.plot.getSeriesAndFormatterListForRenderer(renderer.getClass());
                if (sfl != null) {
                    seriesCount += sfl.size();
                }
                sortedRegions.addAll(renderer.getUniqueRegionFormatters().entrySet());
            }
            Iterator<RectF> it = this.tableModel.getIterator(widgetRect, seriesCount + sortedRegions.size());
            for (XYSeriesRenderer renderer2 : this.plot.getRendererList()) {
                SeriesAndFormatterList<XYSeries, XYSeriesFormatter> sfl2 = this.plot.getSeriesAndFormatterListForRenderer(renderer2.getClass());
                if (sfl2 != null) {
                    for (int i = 0; i < sfl2.size() && it.hasNext(); i++) {
                        XYSeriesFormatter formatter = (XYSeriesFormatter) sfl2.getFormatter(i);
                        drawSeriesLegendCell(canvas, renderer2, formatter, (RectF) it.next(), ((XYSeries) sfl2.getSeries(i)).getTitle());
                    }
                }
            }
            Iterator i$ = sortedRegions.iterator();
            while (i$.hasNext()) {
                Entry<XYRegionFormatter, String> entry = (Entry) i$.next();
                if (!it.hasNext()) {
                    break;
                }
                Canvas canvas2 = canvas;
                drawRegionLegendCell(canvas2, (XYRegionFormatter) entry.getKey(), (RectF) it.next(), (String) entry.getValue());
            }
        }
    }

    public Paint getTextPaint() {
        return this.textPaint;
    }

    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
    }

    public boolean isDrawIconBackgroundEnabled() {
        return this.drawIconBackgroundEnabled;
    }

    public void setDrawIconBackgroundEnabled(boolean drawIconBackgroundEnabled) {
        this.drawIconBackgroundEnabled = drawIconBackgroundEnabled;
    }

    public boolean isDrawIconBorderEnabled() {
        return this.drawIconBorderEnabled;
    }

    public void setDrawIconBorderEnabled(boolean drawIconBorderEnabled) {
        this.drawIconBorderEnabled = drawIconBorderEnabled;
    }

    public TableModel getTableModel() {
        return this.tableModel;
    }

    public SizeMetrics getIconSizeMetrics() {
        return this.iconSizeMetrics;
    }

    public void setIconSizeMetrics(SizeMetrics iconSizeMetrics) {
        this.iconSizeMetrics = iconSizeMetrics;
    }
}
