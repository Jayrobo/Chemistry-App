package com.androidplot.ui;

import android.content.Context;
import com.androidplot.Plot;
import com.androidplot.util.Configurator;

public abstract class Formatter<PlotType extends Plot> {
    public abstract Class<? extends SeriesRenderer> getRendererClass();

    public abstract SeriesRenderer getRendererInstance(PlotType plotType);

    public Formatter<PlotType> configure(Context ctx, int xmlCfgId) {
        Configurator.configure(ctx, (Object) this, xmlCfgId);
        return this;
    }
}
