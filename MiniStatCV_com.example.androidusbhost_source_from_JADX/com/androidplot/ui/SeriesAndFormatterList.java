package com.androidplot.ui;

import com.androidplot.Series;
import java.util.LinkedList;
import java.util.List;

public class SeriesAndFormatterList<SeriesType extends Series, FormatterType> {
    private LinkedList<FormatterType> formatterList = new LinkedList();
    private LinkedList<SeriesType> seriesList = new LinkedList();

    public boolean contains(SeriesType series) {
        return this.seriesList.contains(series);
    }

    public int size() {
        return this.seriesList.size();
    }

    public List<SeriesType> getSeriesList() {
        return this.seriesList;
    }

    public List<FormatterType> getFormatterList() {
        return this.formatterList;
    }

    public boolean add(SeriesType series, FormatterType formatter) {
        if (series == null || formatter == null) {
            throw new IllegalArgumentException("series and formatter must not be null.");
        } else if (this.seriesList.contains(series)) {
            return false;
        } else {
            this.seriesList.add(series);
            this.formatterList.add(formatter);
            return true;
        }
    }

    public boolean remove(SeriesType series) {
        int index = this.seriesList.indexOf(series);
        if (index < 0) {
            return false;
        }
        this.seriesList.remove(index);
        this.formatterList.remove(index);
        return true;
    }

    public FormatterType getFormatter(SeriesType series) {
        return this.formatterList.get(this.seriesList.indexOf(series));
    }

    public FormatterType getFormatter(int index) {
        return this.formatterList.get(index);
    }

    public SeriesType getSeries(int index) {
        return (Series) this.seriesList.get(index);
    }

    public FormatterType setFormatter(SeriesType series, FormatterType formatter) {
        return this.formatterList.set(this.seriesList.indexOf(series), formatter);
    }
}
