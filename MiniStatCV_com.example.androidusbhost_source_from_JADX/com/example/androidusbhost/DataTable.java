package com.example.androidusbhost;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataTable {
    public Long dateTime;
    public int id;
    public Double valADC;
    public Short volt;

    public static class Factory {
        public DataTable fromCsv(String csvString) {
            String[] components = csvString.split(",");
            if (components.length != 4) {
                throw new RuntimeException("Error! This CSV string is malformed: " + csvString);
            }
            DataTable dt = new DataTable();
            dt.id = Integer.parseInt(components[0]);
            dt.dateTime = Long.valueOf(Long.parseLong(components[1]));
            dt.volt = Short.valueOf(Short.parseShort(components[2]));
            dt.valADC = Double.valueOf(Double.parseDouble(components[3]));
            return dt;
        }
    }

    public DataTable(int id, Long _DateTime, Short _Volt, Double _DataValue) {
        this.id = id;
        this.dateTime = _DateTime;
        this.volt = _Volt;
        this.valADC = _DataValue;
    }

    public DataTable(Long _DateTime, Short _Volt, Double _DataValue) {
        this.dateTime = _DateTime;
        this.volt = _Volt;
        this.valADC = _DataValue;
    }

    public String toCsv() {
        Date d = new Date(this.dateTime.longValue());
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat tFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        String date = dFormat.format(d);
        String time = tFormat.format(d);
        return String.format("%d,%s,%s,%d,%f", new Object[]{Integer.valueOf(this.id), date, time, this.volt, this.valADC});
    }
}
