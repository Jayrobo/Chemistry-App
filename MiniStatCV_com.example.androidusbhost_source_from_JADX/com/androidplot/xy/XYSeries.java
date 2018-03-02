package com.androidplot.xy;

import android.util.Pair;
import com.androidplot.Series;

public interface XYSeries extends Series<Pair<Number, Number>> {
    Number getX(int i);

    Number getY(int i);

    int size();
}
