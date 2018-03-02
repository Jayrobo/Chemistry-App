package com.androidplot.xy;

import android.graphics.Canvas;
import android.graphics.RectF;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.util.ValPixConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class BarRenderer<T extends BarFormatter> extends XYSeriesRenderer<T> {
    private Comparator<Bar> barComparator = new BarComparator();
    private float barGap = 1.0f;
    private float barWidth = 5.0f;
    private BarRenderStyle renderStyle = BarRenderStyle.OVERLAID;
    private BarWidthStyle widthStyle = BarWidthStyle.FIXED_WIDTH;

    public class Bar {
        protected BarGroup barGroup;
        public final int intX = ((int) this.pixX);
        public final int intY;
        public final float pixX;
        public final float pixY;
        public final XYSeries series;
        public final int seriesIndex;
        public final double xVal;
        public final double yVal;

        public Bar(XYSeries series, int seriesIndex, RectF plotArea) {
            this.series = series;
            this.seriesIndex = seriesIndex;
            this.xVal = series.getX(seriesIndex).doubleValue();
            this.pixX = ValPixConverter.valToPix(this.xVal, ((XYPlot) BarRenderer.this.getPlot()).getCalculatedMinX().doubleValue(), ((XYPlot) BarRenderer.this.getPlot()).getCalculatedMaxX().doubleValue(), plotArea.width(), false) + plotArea.left;
            if (series.getY(seriesIndex) != null) {
                this.yVal = series.getY(seriesIndex).doubleValue();
                this.pixY = ValPixConverter.valToPix(this.yVal, ((XYPlot) BarRenderer.this.getPlot()).getCalculatedMinY().doubleValue(), ((XYPlot) BarRenderer.this.getPlot()).getCalculatedMaxY().doubleValue(), plotArea.height(), true) + plotArea.top;
                this.intY = (int) this.pixY;
                return;
            }
            this.yVal = 0.0d;
            this.pixY = plotArea.bottom;
            this.intY = (int) this.pixY;
        }

        public BarFormatter formatter() {
            return BarRenderer.this.getFormatter(this.seriesIndex, this.series);
        }
    }

    public class BarComparator implements Comparator<Bar> {
        public int compare(Bar bar1, Bar bar2) {
            switch (BarRenderer.this.renderStyle) {
                case OVERLAID:
                    return Integer.valueOf(bar1.intY).compareTo(Integer.valueOf(bar2.intY));
                case SIDE_BY_SIDE:
                    return bar1.series.getTitle().compareToIgnoreCase(bar2.series.getTitle());
                case STACKED:
                    return bar1.series.getTitle().compareToIgnoreCase(bar2.series.getTitle());
                default:
                    return 0;
            }
        }
    }

    private class BarGroup {
        public ArrayList<Bar> bars = new ArrayList();
        public int intX;
        public int leftX;
        public RectF plotArea;
        public BarGroup prev;
        public int rightX;
        public int width;

        public BarGroup(int intX, RectF plotArea) {
            this.intX = intX;
            this.plotArea = plotArea;
        }

        public void addBar(Bar bar) {
            bar.barGroup = this;
            this.bars.add(bar);
        }
    }

    public enum BarRenderStyle {
        OVERLAID,
        STACKED,
        SIDE_BY_SIDE
    }

    public enum BarWidthStyle {
        FIXED_WIDTH,
        VARIABLE_WIDTH
    }

    public BarRenderer(XYPlot plot) {
        super(plot);
    }

    public void setBarWidth(float barWidth) {
        this.barWidth = barWidth;
    }

    public void setBarGap(float barGap) {
        this.barGap = barGap;
    }

    public void setBarRenderStyle(BarRenderStyle renderStyle) {
        this.renderStyle = renderStyle;
    }

    public void setBarWidthStyle(BarWidthStyle widthStyle) {
        this.widthStyle = widthStyle;
    }

    public void setBarWidthStyle(BarWidthStyle style, float value) {
        setBarWidthStyle(style);
        switch (style) {
            case FIXED_WIDTH:
                setBarWidth(value);
                return;
            case VARIABLE_WIDTH:
                setBarGap(value);
                return;
            default:
                return;
        }
    }

    public void setBarComparator(Comparator<Bar> barComparator) {
        this.barComparator = barComparator;
    }

    public void doDrawLegendIcon(Canvas canvas, RectF rect, BarFormatter formatter) {
        canvas.drawRect(rect, formatter.getFillPaint());
        canvas.drawRect(rect, formatter.getBorderPaint());
    }

    public T getFormatter(int index, XYSeries series) {
        return (BarFormatter) getFormatter(series);
    }

    public void onRender(Canvas canvas, RectF plotArea) throws PlotRenderException {
        List<XYSeries> sl = ((XYPlot) getPlot()).getSeriesListForRenderer(getClass());
        TreeMap<Number, BarGroup> axisMap = new TreeMap();
        if (sl != null) {
            BarGroup barGroup;
            for (XYSeries series : sl) {
                for (int i = 0; i < series.size(); i++) {
                    if (series.getX(i) != null) {
                        Bar bar = new Bar(series, i, plotArea);
                        if (axisMap.containsKey(Integer.valueOf(bar.intX))) {
                            barGroup = (BarGroup) axisMap.get(Integer.valueOf(bar.intX));
                        } else {
                            BarGroup barGroup2 = new BarGroup(bar.intX, plotArea);
                            axisMap.put(Integer.valueOf(bar.intX), barGroup2);
                        }
                        barGroup.addBar(bar);
                    }
                }
            }
            BarGroup prev = null;
            for (Entry<Number, BarGroup> mapEntry : axisMap.entrySet()) {
                BarGroup current = (BarGroup) mapEntry.getValue();
                current.prev = prev;
                prev = current;
            }
            int gap = (int) this.barGap;
            int rough_width = (int) ((plotArea.width() - ((float) ((axisMap.size() - 1) * gap))) / ((float) (axisMap.size() - 1)));
            if (rough_width < 0) {
                rough_width = 0;
            }
            if (gap > rough_width) {
                gap = rough_width / 2;
            }
            for (Number key : axisMap.keySet()) {
                barGroup = (BarGroup) axisMap.get(key);
                switch (this.widthStyle) {
                    case FIXED_WIDTH:
                        barGroup.leftX = barGroup.intX - ((int) (this.barWidth / 2.0f));
                        barGroup.width = (int) this.barWidth;
                        barGroup.rightX = barGroup.leftX + barGroup.width;
                        break;
                    case VARIABLE_WIDTH:
                        if (barGroup.prev != null) {
                            if (((barGroup.intX - barGroup.prev.intX) - gap) - 1 <= ((int) (((double) rough_width) * 1.5d))) {
                                barGroup.leftX = (barGroup.prev.rightX + gap) + 1;
                                if (barGroup.leftX > barGroup.intX) {
                                    barGroup.leftX = barGroup.intX;
                                }
                                barGroup.rightX = barGroup.intX + (rough_width / 2);
                                barGroup.width = barGroup.rightX - barGroup.leftX;
                                break;
                            }
                            barGroup.leftX = barGroup.intX - (rough_width / 2);
                            barGroup.width = rough_width;
                            barGroup.rightX = barGroup.leftX + barGroup.width;
                            break;
                        }
                        barGroup.leftX = barGroup.intX - (rough_width / 2);
                        barGroup.width = rough_width;
                        barGroup.rightX = barGroup.leftX + barGroup.width;
                        break;
                }
                double rangeOrigin = ((XYPlot) getPlot()).getRangeOrigin().doubleValue();
                float basePositionY = ValPixConverter.valToPix(rangeOrigin, ((XYPlot) getPlot()).getCalculatedMinY().doubleValue(), ((XYPlot) getPlot()).getCalculatedMaxY().doubleValue(), plotArea.height(), true) + plotArea.top;
                Iterator i$;
                Bar b;
                BarFormatter formatter;
                PointLabelFormatter plf;
                PointLabeler pointLabeler;
                Canvas canvas2;
                switch (this.renderStyle) {
                    case OVERLAID:
                        Collections.sort(barGroup.bars, this.barComparator);
                        i$ = barGroup.bars.iterator();
                        while (i$.hasNext()) {
                            b = (Bar) i$.next();
                            formatter = b.formatter();
                            plf = formatter.getPointLabelFormatter();
                            pointLabeler = null;
                            if (formatter != null) {
                                pointLabeler = formatter.getPointLabeler();
                            }
                            if (b.yVal < rangeOrigin) {
                                if (b.barGroup.width >= 2) {
                                    canvas.drawRect((float) b.barGroup.leftX, basePositionY, (float) b.barGroup.rightX, (float) b.intY, formatter.getFillPaint());
                                }
                                canvas.drawRect((float) b.barGroup.leftX, basePositionY, (float) b.barGroup.rightX, (float) b.intY, formatter.getBorderPaint());
                            } else {
                                if (b.barGroup.width >= 2) {
                                    canvas.drawRect((float) b.barGroup.leftX, (float) b.intY, (float) b.barGroup.rightX, basePositionY, formatter.getFillPaint());
                                }
                                canvas.drawRect((float) b.barGroup.leftX, (float) b.intY, (float) b.barGroup.rightX, basePositionY, formatter.getBorderPaint());
                            }
                            if (!(plf == null || pointLabeler == null)) {
                                canvas2 = canvas;
                                canvas2.drawText(pointLabeler.getLabel(b.series, b.seriesIndex), ((float) b.intX) + plf.hOffset, ((float) b.intY) + plf.vOffset, plf.getTextPaint());
                            }
                        }
                        break;
                    case SIDE_BY_SIDE:
                        int width = barGroup.width / barGroup.bars.size();
                        int leftX = barGroup.leftX;
                        Collections.sort(barGroup.bars, this.barComparator);
                        i$ = barGroup.bars.iterator();
                        while (i$.hasNext()) {
                            b = (Bar) i$.next();
                            formatter = b.formatter();
                            plf = formatter.getPointLabelFormatter();
                            pointLabeler = null;
                            if (formatter != null) {
                                pointLabeler = formatter.getPointLabeler();
                            }
                            if (b.yVal < rangeOrigin) {
                                if (b.barGroup.width >= 2) {
                                    canvas.drawRect((float) leftX, basePositionY, (float) (leftX + width), (float) b.intY, formatter.getFillPaint());
                                }
                                canvas.drawRect((float) leftX, basePositionY, (float) (leftX + width), (float) b.intY, formatter.getBorderPaint());
                            } else {
                                if (b.barGroup.width >= 2) {
                                    canvas.drawRect((float) leftX, (float) b.intY, (float) (leftX + width), basePositionY, formatter.getFillPaint());
                                }
                                canvas.drawRect((float) leftX, (float) b.intY, (float) (leftX + width), basePositionY, formatter.getBorderPaint());
                            }
                            if (!(plf == null || pointLabeler == null)) {
                                canvas2 = canvas;
                                canvas2.drawText(pointLabeler.getLabel(b.series, b.seriesIndex), ((float) ((width / 2) + leftX)) + plf.hOffset, ((float) b.intY) + plf.vOffset, plf.getTextPaint());
                            }
                            leftX += width;
                        }
                        break;
                    case STACKED:
                        int bottom = (int) barGroup.plotArea.bottom;
                        Collections.sort(barGroup.bars, this.barComparator);
                        i$ = barGroup.bars.iterator();
                        while (i$.hasNext()) {
                            b = (Bar) i$.next();
                            formatter = b.formatter();
                            plf = formatter.getPointLabelFormatter();
                            pointLabeler = null;
                            if (formatter != null) {
                                pointLabeler = formatter.getPointLabeler();
                            }
                            int top = bottom - (((int) b.barGroup.plotArea.bottom) - b.intY);
                            if (b.barGroup.width >= 2) {
                                canvas.drawRect((float) b.barGroup.leftX, (float) top, (float) b.barGroup.rightX, (float) bottom, formatter.getFillPaint());
                            }
                            canvas.drawRect((float) b.barGroup.leftX, (float) top, (float) b.barGroup.rightX, (float) bottom, formatter.getBorderPaint());
                            if (!(plf == null || pointLabeler == null)) {
                                canvas2 = canvas;
                                canvas2.drawText(pointLabeler.getLabel(b.series, b.seriesIndex), ((float) b.intX) + plf.hOffset, ((float) top) + plf.vOffset, plf.getTextPaint());
                            }
                            bottom = top;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
