package com.androidplot.pie;

import com.androidplot.Series;

public class Segment implements Series<Number> {
    private String title;
    private Number value;

    public Segment(String title, Number value) {
        this.title = title;
        setValue(value);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Number getValue() {
        return this.value;
    }

    public void setValue(Number value) {
        this.value = value;
    }
}
