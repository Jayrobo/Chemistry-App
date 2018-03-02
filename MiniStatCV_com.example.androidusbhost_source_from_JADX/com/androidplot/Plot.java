package com.androidplot;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.androidplot.exception.PlotRenderException;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.BoxModel;
import com.androidplot.ui.Formatter;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.Resizable;
import com.androidplot.ui.SeriesAndFormatterList;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.Configurator;
import com.androidplot.util.DisplayDimensions;
import com.androidplot.util.PixelUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class Plot<SeriesType extends Series, FormatterType extends Formatter, RendererType extends SeriesRenderer> extends View implements Resizable {
    private static final String BASE_PACKAGE = "com.androidplot.";
    private static final int DEFAULT_TITLE_WIDGET_TEXT_SIZE_SP = 10;
    private static final String TAG = Plot.class.getName();
    private static final String XML_ATTR_PREFIX = "androidplot";
    private Paint backgroundPaint;
    private Paint borderPaint;
    private float borderRadiusX;
    private float borderRadiusY;
    private BorderStyle borderStyle;
    private BoxModel boxModel;
    private DisplayDimensions displayDims;
    private boolean drawBorderEnabled;
    private boolean isIdle;
    private boolean keepRunning;
    private LayoutManager layoutManager;
    private final ArrayList<PlotListener> listeners;
    private final BufferedCanvas pingPong;
    private RenderMode renderMode;
    private final Object renderSynch;
    private Thread renderThread;
    private LinkedList<RendererType> renderers;
    private LinkedHashMap<Class, SeriesAndFormatterList<SeriesType, FormatterType>> seriesRegistry;
    private TextLabelWidget titleWidget;

    class C00481 implements Runnable {
        C00481() {
        }

        public void run() {
            Plot.this.keepRunning = true;
            while (Plot.this.keepRunning) {
                Plot.this.isIdle = false;
                synchronized (Plot.this.pingPong) {
                    Plot.this.renderOnCanvas(Plot.this.pingPong.getCanvas());
                    Plot.this.pingPong.swap();
                }
                synchronized (Plot.this.renderSynch) {
                    Plot.this.postInvalidate();
                    if (Plot.this.keepRunning) {
                        try {
                            Plot.this.renderSynch.wait();
                        } catch (InterruptedException e) {
                            Plot.this.keepRunning = false;
                        }
                    }
                }
            }
            System.out.println("AndroidPlot render thread finished.");
        }
    }

    public enum BorderStyle {
        ROUNDED,
        SQUARE,
        NONE
    }

    private class BufferedCanvas {
        private volatile Bitmap bgBuffer;
        private Canvas canvas;
        private volatile Bitmap fgBuffer;

        private BufferedCanvas() {
            this.canvas = new Canvas();
        }

        public synchronized void swap() {
            Bitmap tmp = this.bgBuffer;
            this.bgBuffer = this.fgBuffer;
            this.fgBuffer = tmp;
        }

        public synchronized void resize(int h, int w) {
            if (w <= 0 || h <= 0) {
                this.bgBuffer = null;
                this.fgBuffer = null;
            } else {
                this.bgBuffer = Bitmap.createBitmap(w, h, Config.ARGB_4444);
                this.fgBuffer = Bitmap.createBitmap(w, h, Config.ARGB_4444);
            }
        }

        public synchronized Canvas getCanvas() {
            Canvas canvas;
            if (this.bgBuffer != null) {
                this.canvas.setBitmap(this.bgBuffer);
                canvas = this.canvas;
            } else {
                canvas = null;
            }
            return canvas;
        }

        public Bitmap getBitmap() {
            return this.fgBuffer;
        }
    }

    public enum RenderMode {
        USE_BACKGROUND_THREAD,
        USE_MAIN_THREAD
    }

    protected abstract void onPreInit();

    protected abstract void processAttrs(TypedArray typedArray);

    public DisplayDimensions getDisplayDimensions() {
        return this.displayDims;
    }

    public Plot(Context context, String title) {
        this(context, title, RenderMode.USE_MAIN_THREAD);
    }

    public Plot(Context context, String title, RenderMode mode) {
        super(context);
        this.boxModel = new BoxModel(3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f);
        this.borderStyle = BorderStyle.SQUARE;
        this.borderRadiusX = 15.0f;
        this.borderRadiusY = 15.0f;
        this.drawBorderEnabled = true;
        this.displayDims = new DisplayDimensions();
        this.renderMode = RenderMode.USE_MAIN_THREAD;
        this.pingPong = new BufferedCanvas();
        this.renderSynch = new Object();
        this.keepRunning = false;
        this.isIdle = true;
        this.listeners = new ArrayList();
        this.seriesRegistry = new LinkedHashMap();
        this.renderers = new LinkedList();
        this.borderPaint = new Paint();
        this.borderPaint.setColor(Color.rgb(150, 150, 150));
        this.borderPaint.setStyle(Style.STROKE);
        this.borderPaint.setStrokeWidth(1.0f);
        this.borderPaint.setAntiAlias(true);
        this.backgroundPaint = new Paint();
        this.backgroundPaint.setColor(-12303292);
        this.backgroundPaint.setStyle(Style.FILL);
        this.renderMode = mode;
        init(null, null, 0);
        setTitle(title);
    }

    public Plot(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.boxModel = new BoxModel(3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f);
        this.borderStyle = BorderStyle.SQUARE;
        this.borderRadiusX = 15.0f;
        this.borderRadiusY = 15.0f;
        this.drawBorderEnabled = true;
        this.displayDims = new DisplayDimensions();
        this.renderMode = RenderMode.USE_MAIN_THREAD;
        this.pingPong = new BufferedCanvas();
        this.renderSynch = new Object();
        this.keepRunning = false;
        this.isIdle = true;
        this.listeners = new ArrayList();
        this.seriesRegistry = new LinkedHashMap();
        this.renderers = new LinkedList();
        this.borderPaint = new Paint();
        this.borderPaint.setColor(Color.rgb(150, 150, 150));
        this.borderPaint.setStyle(Style.STROKE);
        this.borderPaint.setStrokeWidth(1.0f);
        this.borderPaint.setAntiAlias(true);
        this.backgroundPaint = new Paint();
        this.backgroundPaint.setColor(-12303292);
        this.backgroundPaint.setStyle(Style.FILL);
        init(context, attrs, 0);
    }

    public Plot(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.boxModel = new BoxModel(3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f);
        this.borderStyle = BorderStyle.SQUARE;
        this.borderRadiusX = 15.0f;
        this.borderRadiusY = 15.0f;
        this.drawBorderEnabled = true;
        this.displayDims = new DisplayDimensions();
        this.renderMode = RenderMode.USE_MAIN_THREAD;
        this.pingPong = new BufferedCanvas();
        this.renderSynch = new Object();
        this.keepRunning = false;
        this.isIdle = true;
        this.listeners = new ArrayList();
        this.seriesRegistry = new LinkedHashMap();
        this.renderers = new LinkedList();
        this.borderPaint = new Paint();
        this.borderPaint.setColor(Color.rgb(150, 150, 150));
        this.borderPaint.setStyle(Style.STROKE);
        this.borderPaint.setStrokeWidth(1.0f);
        this.borderPaint.setAntiAlias(true);
        this.backgroundPaint = new Paint();
        this.backgroundPaint.setColor(-12303292);
        this.backgroundPaint.setStyle(Style.FILL);
        init(context, attrs, defStyle);
    }

    protected boolean isHwAccelerationSupported() {
        return false;
    }

    public void setRenderMode(RenderMode mode) {
        this.renderMode = mode;
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        PixelUtils.init(getContext());
        this.layoutManager = new LayoutManager();
        this.titleWidget = new TextLabelWidget(this.layoutManager, new SizeMetrics(25.0f, SizeLayoutType.ABSOLUTE, 100.0f, SizeLayoutType.ABSOLUTE), TextOrientationType.HORIZONTAL);
        this.titleWidget.position(0.0f, XLayoutStyle.RELATIVE_TO_CENTER, 0.0f, YLayoutStyle.ABSOLUTE_FROM_TOP, AnchorPosition.TOP_MIDDLE);
        this.titleWidget.getLabelPaint().setTextSize(PixelUtils.spToPix(10.0f));
        onPreInit();
        this.layoutManager.moveToTop(this.titleWidget);
        if (!(context == null || attrs == null)) {
            loadAttrs(attrs, defStyle);
        }
        this.layoutManager.onPostInit();
        if (this.renderMode == RenderMode.USE_BACKGROUND_THREAD) {
            this.renderThread = new Thread(new C00481());
        }
    }

    private void processBaseAttrs(TypedArray attrs) {
        setTitle(attrs.getString(0));
        getTitleWidget().getLabelPaint().setTextSize(attrs.getDimension(1, PixelUtils.spToPix(10.0f)));
    }

    private void loadAttrs(AttributeSet attrs, int defStyle) {
        if (attrs != null) {
            Field styleableFieldInR = null;
            TypedArray typedAttrs = null;
            Class styleableClass = null;
            try {
                styleableClass = Class.forName(getContext().getPackageName() + ".R$styleable");
            } catch (ClassNotFoundException e) {
            }
            if (styleableClass != null) {
                String styleableName = getClass().getName().substring(BASE_PACKAGE.length()).replace('.', '_');
                try {
                    styleableFieldInR = styleableClass.getField(styleableName);
                } catch (NoSuchFieldException e2) {
                    Log.d(TAG, "Styleable definition not found for: " + styleableName);
                }
                if (styleableFieldInR != null) {
                    try {
                        typedAttrs = getContext().obtainStyledAttributes(attrs, (int[]) styleableFieldInR.get(null), defStyle, 0);
                        if (typedAttrs != null) {
                            processAttrs(typedAttrs);
                            typedAttrs.recycle();
                        }
                    } catch (IllegalAccessException e3) {
                        if (typedAttrs != null) {
                            processAttrs(typedAttrs);
                            typedAttrs.recycle();
                        }
                    } catch (Throwable th) {
                        if (typedAttrs != null) {
                            processAttrs(typedAttrs);
                            typedAttrs.recycle();
                        }
                    }
                }
                try {
                    styleableFieldInR = styleableClass.getField(Plot.class.getSimpleName());
                    if (styleableFieldInR != null) {
                        typedAttrs = getContext().obtainStyledAttributes(attrs, (int[]) styleableFieldInR.get(null), defStyle, 0);
                    }
                    if (typedAttrs != null) {
                        processBaseAttrs(typedAttrs);
                        typedAttrs.recycle();
                    }
                } catch (IllegalAccessException e4) {
                    if (typedAttrs != null) {
                        processBaseAttrs(typedAttrs);
                        typedAttrs.recycle();
                    }
                } catch (NoSuchFieldException e5) {
                    Log.d(TAG, "Styleable definition not found for: " + Plot.class.getSimpleName());
                    if (typedAttrs != null) {
                        processBaseAttrs(typedAttrs);
                        typedAttrs.recycle();
                    }
                } catch (Throwable th2) {
                    if (typedAttrs != null) {
                        processBaseAttrs(typedAttrs);
                        typedAttrs.recycle();
                    }
                }
            }
            HashMap attrHash = new HashMap();
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                String attrName = attrs.getAttributeName(i);
                if (attrName != null && attrName.toUpperCase().startsWith(XML_ATTR_PREFIX.toUpperCase())) {
                    attrHash.put(attrName.substring(XML_ATTR_PREFIX.length() + 1), attrs.getAttributeValue(i));
                }
            }
            Configurator.configure(getContext(), (Object) this, attrHash);
        }
    }

    public RenderMode getRenderMode() {
        return this.renderMode;
    }

    public synchronized boolean addListener(PlotListener listener) {
        boolean z;
        z = !this.listeners.contains(listener) && this.listeners.add(listener);
        return z;
    }

    public synchronized boolean removeListener(PlotListener listener) {
        return this.listeners.remove(listener);
    }

    protected void notifyListenersBeforeDraw(Canvas canvas) {
        Iterator i$ = this.listeners.iterator();
        while (i$.hasNext()) {
            ((PlotListener) i$.next()).onBeforeDraw(this, canvas);
        }
    }

    protected void notifyListenersAfterDraw(Canvas canvas) {
        Iterator i$ = this.listeners.iterator();
        while (i$.hasNext()) {
            ((PlotListener) i$.next()).onAfterDraw(this, canvas);
        }
    }

    public synchronized boolean addSeries(SeriesType series, FormatterType formatter) {
        boolean z;
        Class rendererClass = formatter.getRendererClass();
        SeriesAndFormatterList<SeriesType, FormatterType> sfList = (SeriesAndFormatterList) this.seriesRegistry.get(rendererClass);
        if (sfList == null) {
            if (getRenderer(rendererClass) == null) {
                this.renderers.add(formatter.getRendererInstance(this));
            }
            sfList = new SeriesAndFormatterList();
            this.seriesRegistry.put(rendererClass, sfList);
        }
        if (series instanceof PlotListener) {
            addListener((PlotListener) series);
        }
        if (sfList.contains(series)) {
            z = false;
        } else {
            sfList.add(series, formatter);
            z = true;
        }
        return z;
    }

    public synchronized boolean removeSeries(SeriesType series, Class rendererClass) {
        boolean result;
        result = ((SeriesAndFormatterList) this.seriesRegistry.get(rendererClass)).remove(series);
        if (((SeriesAndFormatterList) this.seriesRegistry.get(rendererClass)).size() <= 0) {
            this.seriesRegistry.remove(rendererClass);
        }
        if (series instanceof PlotListener) {
            removeListener((PlotListener) series);
        }
        return result;
    }

    public synchronized void removeSeries(SeriesType series) {
        for (Class rendererClass : this.seriesRegistry.keySet()) {
            ((SeriesAndFormatterList) this.seriesRegistry.get(rendererClass)).remove(series);
        }
        Iterator<SeriesAndFormatterList<SeriesType, FormatterType>> it = this.seriesRegistry.values().iterator();
        while (it.hasNext()) {
            if (((SeriesAndFormatterList) it.next()).size() <= 0) {
                it.remove();
            }
        }
        if (series instanceof PlotListener) {
            removeListener((PlotListener) series);
        }
    }

    public void clear() {
        Iterator<SeriesAndFormatterList<SeriesType, FormatterType>> it = this.seriesRegistry.values().iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    public boolean isEmpty() {
        return this.seriesRegistry.isEmpty();
    }

    public FormatterType getFormatter(SeriesType series, Class rendererClass) {
        return (Formatter) ((SeriesAndFormatterList) this.seriesRegistry.get(rendererClass)).getFormatter((Series) series);
    }

    public SeriesAndFormatterList<SeriesType, FormatterType> getSeriesAndFormatterListForRenderer(Class rendererClass) {
        return (SeriesAndFormatterList) this.seriesRegistry.get(rendererClass);
    }

    public Set<SeriesType> getSeriesSet() {
        Set<SeriesType> seriesSet = new LinkedHashSet();
        for (SeriesRenderer renderer : getRendererList()) {
            List<SeriesType> seriesList = getSeriesListForRenderer(renderer.getClass());
            if (seriesList != null) {
                for (SeriesType series : seriesList) {
                    seriesSet.add(series);
                }
            }
        }
        return seriesSet;
    }

    public List<SeriesType> getSeriesListForRenderer(Class rendererClass) {
        SeriesAndFormatterList<SeriesType, FormatterType> lst = (SeriesAndFormatterList) this.seriesRegistry.get(rendererClass);
        if (lst == null) {
            return null;
        }
        return lst.getSeriesList();
    }

    public RendererType getRenderer(Class rendererClass) {
        Iterator i$ = this.renderers.iterator();
        while (i$.hasNext()) {
            SeriesRenderer renderer = (SeriesRenderer) i$.next();
            if (renderer.getClass() == rendererClass) {
                return renderer;
            }
        }
        return null;
    }

    public List<RendererType> getRendererList() {
        return this.renderers;
    }

    public void setMarkupEnabled(boolean enabled) {
        this.layoutManager.setMarkupEnabled(enabled);
    }

    public void redraw() {
        if (this.renderMode == RenderMode.USE_BACKGROUND_THREAD) {
            if (this.isIdle) {
                synchronized (this.renderSynch) {
                    this.renderSynch.notify();
                }
            }
        } else if (this.renderMode != RenderMode.USE_MAIN_THREAD) {
            throw new IllegalArgumentException("Unsupported Render Mode: " + this.renderMode);
        } else if (Looper.myLooper() == Looper.getMainLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public synchronized void layout(DisplayDimensions dims) {
        this.displayDims = dims;
        this.layoutManager.layout(this.displayDims);
    }

    protected void onDetachedFromWindow() {
        synchronized (this.renderSynch) {
            this.keepRunning = false;
            this.renderSynch.notify();
        }
    }

    protected synchronized void onSizeChanged(int w, int h, int oldw, int oldh) {
        PixelUtils.init(getContext());
        if (VERSION.SDK_INT >= 11 && !isHwAccelerationSupported() && isHardwareAccelerated()) {
            setLayerType(1, null);
        }
        if (this.renderMode == RenderMode.USE_BACKGROUND_THREAD) {
            this.pingPong.resize(h, w);
        }
        RectF cRect = new RectF(0.0f, 0.0f, (float) w, (float) h);
        RectF mRect = this.boxModel.getMarginatedRect(cRect);
        layout(new DisplayDimensions(cRect, mRect, this.boxModel.getPaddedRect(mRect)));
        super.onSizeChanged(w, h, oldw, oldh);
        if (!(this.renderThread == null || this.renderThread.isAlive())) {
            this.renderThread.start();
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.renderMode == RenderMode.USE_BACKGROUND_THREAD) {
            synchronized (this.pingPong) {
                Bitmap bmp = this.pingPong.getBitmap();
                if (bmp != null) {
                    canvas.drawBitmap(bmp, 0.0f, 0.0f, null);
                }
            }
        } else if (this.renderMode == RenderMode.USE_MAIN_THREAD) {
            renderOnCanvas(canvas);
        } else {
            throw new IllegalArgumentException("Unsupported Render Mode: " + this.renderMode);
        }
    }

    protected synchronized void renderOnCanvas(Canvas canvas) {
        try {
            notifyListenersBeforeDraw(canvas);
            canvas.drawColor(0, Mode.CLEAR);
            if (this.backgroundPaint != null) {
                drawBackground(canvas, this.displayDims.marginatedRect);
            }
            this.layoutManager.draw(canvas);
            if (getBorderPaint() != null) {
                drawBorder(canvas, this.displayDims.marginatedRect);
            }
        } catch (PlotRenderException e) {
            Log.e(TAG, "Exception while rendering Plot.", e);
            e.printStackTrace();
        } catch (Exception e2) {
            Log.e(TAG, "Exception while rendering Plot.", e2);
        } catch (Throwable th) {
            this.isIdle = true;
            notifyListenersAfterDraw(canvas);
        }
        this.isIdle = true;
        notifyListenersAfterDraw(canvas);
    }

    public void setBorderStyle(BorderStyle style, Float radiusX, Float radiusY) {
        if (style == BorderStyle.ROUNDED) {
            if (radiusX == null || radiusY == null) {
                throw new IllegalArgumentException("radiusX and radiusY cannot be null when using BorderStyle.ROUNDED");
            }
            this.borderRadiusX = radiusX.floatValue();
            this.borderRadiusY = radiusY.floatValue();
        }
        this.borderStyle = style;
    }

    protected void drawBorder(Canvas canvas, RectF dims) {
        switch (this.borderStyle) {
            case ROUNDED:
                canvas.drawRoundRect(dims, this.borderRadiusX, this.borderRadiusY, this.borderPaint);
                return;
            case SQUARE:
                canvas.drawRect(dims, this.borderPaint);
                return;
            default:
                return;
        }
    }

    protected void drawBackground(Canvas canvas, RectF dims) {
        switch (this.borderStyle) {
            case ROUNDED:
                canvas.drawRoundRect(dims, this.borderRadiusX, this.borderRadiusY, this.backgroundPaint);
                return;
            case SQUARE:
                canvas.drawRect(dims, this.backgroundPaint);
                return;
            default:
                return;
        }
    }

    public String getTitle() {
        return getTitleWidget().getText();
    }

    public void setTitle(String title) {
        this.titleWidget.setText(title);
    }

    public LayoutManager getLayoutManager() {
        return this.layoutManager;
    }

    public void setLayoutManager(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public TextLabelWidget getTitleWidget() {
        return this.titleWidget;
    }

    public void setTitleWidget(TextLabelWidget titleWidget) {
        this.titleWidget = titleWidget;
    }

    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    public void setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
    }

    public void setPlotMargins(float left, float top, float right, float bottom) {
        setPlotMarginLeft(left);
        setPlotMarginTop(top);
        setPlotMarginRight(right);
        setPlotMarginBottom(bottom);
    }

    public void setPlotPadding(float left, float top, float right, float bottom) {
        setPlotPaddingLeft(left);
        setPlotPaddingTop(top);
        setPlotPaddingRight(right);
        setPlotPaddingBottom(bottom);
    }

    public float getPlotMarginTop() {
        return this.boxModel.getMarginTop();
    }

    public void setPlotMarginTop(float plotMarginTop) {
        this.boxModel.setMarginTop(plotMarginTop);
    }

    public float getPlotMarginBottom() {
        return this.boxModel.getMarginBottom();
    }

    public void setPlotMarginBottom(float plotMarginBottom) {
        this.boxModel.setMarginBottom(plotMarginBottom);
    }

    public float getPlotMarginLeft() {
        return this.boxModel.getMarginLeft();
    }

    public void setPlotMarginLeft(float plotMarginLeft) {
        this.boxModel.setMarginLeft(plotMarginLeft);
    }

    public float getPlotMarginRight() {
        return this.boxModel.getMarginRight();
    }

    public void setPlotMarginRight(float plotMarginRight) {
        this.boxModel.setMarginRight(plotMarginRight);
    }

    public float getPlotPaddingTop() {
        return this.boxModel.getPaddingTop();
    }

    public void setPlotPaddingTop(float plotPaddingTop) {
        this.boxModel.setPaddingTop(plotPaddingTop);
    }

    public float getPlotPaddingBottom() {
        return this.boxModel.getPaddingBottom();
    }

    public void setPlotPaddingBottom(float plotPaddingBottom) {
        this.boxModel.setPaddingBottom(plotPaddingBottom);
    }

    public float getPlotPaddingLeft() {
        return this.boxModel.getPaddingLeft();
    }

    public void setPlotPaddingLeft(float plotPaddingLeft) {
        this.boxModel.setPaddingLeft(plotPaddingLeft);
    }

    public float getPlotPaddingRight() {
        return this.boxModel.getPaddingRight();
    }

    public void setPlotPaddingRight(float plotPaddingRight) {
        this.boxModel.setPaddingRight(plotPaddingRight);
    }

    public Paint getBorderPaint() {
        return this.borderPaint;
    }

    public void setBorderPaint(Paint borderPaint) {
        if (borderPaint == null) {
            this.borderPaint = null;
            return;
        }
        this.borderPaint = new Paint(borderPaint);
        this.borderPaint.setStyle(Style.STROKE);
    }
}
