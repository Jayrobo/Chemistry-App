package com.androidplot.util;

import android.util.Log;
import com.androidplot.Plot;
import java.util.Arrays;
import java.util.List;

public class Redrawer implements Runnable {
    private static final String TAG = Redrawer.class.getName();
    private boolean keepAlive;
    private boolean keepRunning;
    private List<Plot> plots;
    private long sleepTime;

    public Redrawer(List<Plot> plots, float maxRefreshRate, boolean startImmediately) {
        this.plots = plots;
        setMaxRefreshRate(maxRefreshRate);
        new Thread(this).start();
        if (startImmediately) {
            run();
        }
    }

    public Redrawer(Plot plot, float maxRefreshRate, boolean startImmediately) {
        this(Arrays.asList(new Plot[]{plot}), maxRefreshRate, startImmediately);
    }

    public synchronized void pause() {
        this.keepRunning = false;
        notify();
        Log.d(TAG, "Redrawer paused.");
    }

    public synchronized void start() {
        this.keepRunning = true;
        notify();
        Log.d(TAG, "Redrawer started.");
    }

    public synchronized void finish() {
        this.keepRunning = false;
        this.keepAlive = false;
        notify();
    }

    public void run() {
        this.keepAlive = true;
        while (this.keepAlive) {
            try {
                if (this.keepRunning) {
                    for (Plot plot : this.plots) {
                        plot.redraw();
                    }
                    synchronized (this) {
                        wait(this.sleepTime);
                    }
                } else {
                    synchronized (this) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                Log.d(TAG, "Redrawer thread exited.");
                return;
            } catch (Throwable th) {
                Log.d(TAG, "Redrawer thread exited.");
            }
        }
        Log.d(TAG, "Redrawer thread exited.");
    }

    public void setMaxRefreshRate(float refreshRate) {
        this.sleepTime = (long) (1000.0f / refreshRate);
        Log.d(TAG, "Set Redrawer refresh rate to " + refreshRate + "( " + this.sleepTime + " ms)");
    }
}
