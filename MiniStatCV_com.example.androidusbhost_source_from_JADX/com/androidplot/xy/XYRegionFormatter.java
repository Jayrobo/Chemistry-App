package com.androidplot.xy;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import com.androidplot.util.Configurator;

public class XYRegionFormatter {
    private Paint paint = new Paint();

    public XYRegionFormatter(Context ctx, int xmlCfgId) {
        this.paint.setStyle(Style.FILL);
        this.paint.setAntiAlias(true);
        if (getClass().equals(XYRegionFormatter.class)) {
            Configurator.configure(ctx, (Object) this, xmlCfgId);
        }
    }

    public XYRegionFormatter(int color) {
        this.paint.setStyle(Style.FILL);
        this.paint.setAntiAlias(true);
        this.paint.setColor(color);
    }

    public int getColor() {
        return this.paint.getColor();
    }

    public void setColor(int color) {
        this.paint.setColor(color);
    }

    public Paint getPaint() {
        return this.paint;
    }
}
