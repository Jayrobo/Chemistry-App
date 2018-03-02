package com.androidplot.ui.widget;

import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import com.androidplot.ui.LayoutManager;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.util.FontUtils;

public class TextLabelWidget extends Widget {
    private static final String TAG = TextLabelWidget.class.getName();
    private boolean autoPackEnabled;
    private Paint labelPaint;
    private TextOrientationType orientation;
    private String text;

    public TextLabelWidget(LayoutManager layoutManager, SizeMetrics sizeMetrics) {
        this(layoutManager, sizeMetrics, TextOrientationType.HORIZONTAL);
    }

    public TextLabelWidget(LayoutManager layoutManager, String title, SizeMetrics sizeMetrics, TextOrientationType orientation) {
        this(layoutManager, sizeMetrics, orientation);
        setText(title);
    }

    public TextLabelWidget(LayoutManager layoutManager, SizeMetrics sizeMetrics, TextOrientationType orientation) {
        super(layoutManager, new SizeMetrics(0.0f, SizeLayoutType.ABSOLUTE, 0.0f, SizeLayoutType.ABSOLUTE));
        this.autoPackEnabled = true;
        this.labelPaint = new Paint();
        this.labelPaint.setColor(-1);
        this.labelPaint.setAntiAlias(true);
        this.labelPaint.setTextAlign(Align.CENTER);
        setSize(sizeMetrics);
        this.orientation = orientation;
    }

    protected void onMetricsChanged(SizeMetrics olds, SizeMetrics news) {
        if (this.autoPackEnabled) {
            pack();
        }
    }

    public void onPostInit() {
        if (this.autoPackEnabled) {
            pack();
        }
    }

    public void pack() {
        Rect size = FontUtils.getStringDimensions(this.text, getLabelPaint());
        if (size != null) {
            switch (this.orientation) {
                case HORIZONTAL:
                    setSize(new SizeMetrics((float) size.height(), SizeLayoutType.ABSOLUTE, (float) (size.width() + 2), SizeLayoutType.ABSOLUTE));
                    break;
                case VERTICAL_ASCENDING:
                case VERTICAL_DESCENDING:
                    setSize(new SizeMetrics((float) size.width(), SizeLayoutType.ABSOLUTE, (float) (size.height() + 2), SizeLayoutType.ABSOLUTE));
                    break;
            }
            refreshLayout();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void doOnDraw(android.graphics.Canvas r6, android.graphics.RectF r7) {
        /*
        r5 = this;
        r2 = r5.text;
        if (r2 == 0) goto L_0x000c;
    L_0x0004:
        r2 = r5.text;
        r2 = r2.length();
        if (r2 != 0) goto L_0x000d;
    L_0x000c:
        return;
    L_0x000d:
        r2 = r5.labelPaint;
        r2 = r2.getFontMetrics();
        r1 = r2.descent;
        r2 = com.androidplot.ui.AnchorPosition.CENTER;
        r0 = com.androidplot.ui.widget.Widget.getAnchorCoordinates(r7, r2);
        r2 = 31;
        r6.save(r2);	 Catch:{ all -> 0x0055 }
        r2 = r0.x;	 Catch:{ all -> 0x0055 }
        r3 = r0.y;	 Catch:{ all -> 0x0055 }
        r6.translate(r2, r3);	 Catch:{ all -> 0x0055 }
        r2 = com.androidplot.ui.widget.TextLabelWidget.C00571.$SwitchMap$com$androidplot$ui$TextOrientationType;	 Catch:{ all -> 0x0055 }
        r3 = r5.orientation;	 Catch:{ all -> 0x0055 }
        r3 = r3.ordinal();	 Catch:{ all -> 0x0055 }
        r2 = r2[r3];	 Catch:{ all -> 0x0055 }
        switch(r2) {
            case 1: goto L_0x005f;
            case 2: goto L_0x005a;
            case 3: goto L_0x006b;
            default: goto L_0x0034;
        };	 Catch:{ all -> 0x0055 }
    L_0x0034:
        r2 = new java.lang.UnsupportedOperationException;	 Catch:{ all -> 0x0055 }
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0055 }
        r3.<init>();	 Catch:{ all -> 0x0055 }
        r4 = "Orientation ";
        r3 = r3.append(r4);	 Catch:{ all -> 0x0055 }
        r4 = r5.orientation;	 Catch:{ all -> 0x0055 }
        r3 = r3.append(r4);	 Catch:{ all -> 0x0055 }
        r4 = " not yet implemented for TextLabelWidget.";
        r3 = r3.append(r4);	 Catch:{ all -> 0x0055 }
        r3 = r3.toString();	 Catch:{ all -> 0x0055 }
        r2.<init>(r3);	 Catch:{ all -> 0x0055 }
        throw r2;	 Catch:{ all -> 0x0055 }
    L_0x0055:
        r2 = move-exception;
        r6.restore();
        throw r2;
    L_0x005a:
        r2 = -1028390912; // 0xffffffffc2b40000 float:-90.0 double:NaN;
        r6.rotate(r2);	 Catch:{ all -> 0x0055 }
    L_0x005f:
        r2 = r5.text;	 Catch:{ all -> 0x0055 }
        r3 = 0;
        r4 = r5.labelPaint;	 Catch:{ all -> 0x0055 }
        r6.drawText(r2, r3, r1, r4);	 Catch:{ all -> 0x0055 }
        r6.restore();
        goto L_0x000c;
    L_0x006b:
        r2 = 1119092736; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r6.rotate(r2);	 Catch:{ all -> 0x0055 }
        goto L_0x005f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.androidplot.ui.widget.TextLabelWidget.doOnDraw(android.graphics.Canvas, android.graphics.RectF):void");
    }

    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    public void setLabelPaint(Paint labelPaint) {
        this.labelPaint = labelPaint;
        if (this.autoPackEnabled) {
            pack();
        }
    }

    public TextOrientationType getOrientation() {
        return this.orientation;
    }

    public void setOrientation(TextOrientationType orientation) {
        this.orientation = orientation;
        if (this.autoPackEnabled) {
            pack();
        }
    }

    public boolean isAutoPackEnabled() {
        return this.autoPackEnabled;
    }

    public void setAutoPackEnabled(boolean autoPackEnabled) {
        this.autoPackEnabled = autoPackEnabled;
        if (autoPackEnabled) {
            pack();
        }
    }

    public void setText(String text) {
        this.text = text;
        if (this.autoPackEnabled) {
            pack();
        }
    }

    public String getText() {
        return this.text;
    }
}
