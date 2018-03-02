package com.androidplot.xy;

import android.graphics.Path;
import android.graphics.PointF;

public class BezierLineAndPointRenderer extends LineAndPointRenderer<BezierLineAndPointFormatter> {
    public BezierLineAndPointRenderer(XYPlot plot) {
        super(plot);
    }

    protected void appendToPath(Path path, PointF thisPoint, PointF lastPoint) {
        PointF mid = new PointF();
        mid.set((lastPoint.x + thisPoint.x) / 2.0f, (lastPoint.y + thisPoint.y) / 2.0f);
        path.quadTo((lastPoint.x + mid.x) / 2.0f, lastPoint.y, mid.x, mid.y);
        path.quadTo((mid.x + thisPoint.x) / 2.0f, lastPoint.y, thisPoint.x, thisPoint.y);
    }
}
