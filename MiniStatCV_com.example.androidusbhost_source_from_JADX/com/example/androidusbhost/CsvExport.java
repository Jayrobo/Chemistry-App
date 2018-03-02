package com.example.androidusbhost;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CsvExport {
    private static final String TAG = "CsvExporter";
    private static String cDate = null;
    private static String fName = null;
    private DatabaseHandler databaseHandler;

    public CsvExport(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public Uri dumpDatabaseToCsv(Double sV, Double suV, Double endV, Double sr, String fileName) {
        String srate;
        fName = fileName;
        boolean flag = true;
        List<DataTable> entries = this.databaseHandler.getAllEntry();
        if (sr.doubleValue() == 1.0d) {
            srate = "5";
        } else if (sr.doubleValue() == 2.0d) {
            srate = "10";
        } else if (sr.doubleValue() == 3.0d) {
            srate = "20";
        } else if (sr.doubleValue() == 4.0d) {
            srate = "50";
        } else if (sr.doubleValue() == 5.0d) {
            srate = "100";
        } else if (sr.doubleValue() == 6.0d) {
            srate = "200";
        } else {
            srate = "100";
        }
        StringBuilder stringBuilder = new StringBuilder();
        String parameter = "initial potential (V): " + Double.toString(sV.doubleValue()) + ",swtiching potential (V): " + Double.toString(suV.doubleValue()) + ",final potential (V): " + Double.toString(endV.doubleValue()) + ",scan rate (mV/s): " + srate;
        stringBuilder.append("Cyclic voltammetry");
        stringBuilder.append("\n");
        stringBuilder.append(parameter);
        stringBuilder.append("\n");
        stringBuilder.append("ID,Date,Time,Voltage,Current");
        stringBuilder.append("\n");
        for (DataTable entry : entries) {
            if (flag) {
                cDate = new SimpleDateFormat("yyyyMMdd").format(Long.valueOf(new Date(entry.dateTime.longValue()).getTime()));
                flag = false;
            }
            stringBuilder.append(entry.toCsv());
            stringBuilder.append("\n");
        }
        return dumpStringToCsv(stringBuilder.toString());
    }

    public String uniqueFilename() {
        return String.format("%s_%s.csv", new Object[]{fName, cDate});
    }

    public Uri dumpStringToCsv(String dataDumpString) {
        File root = Environment.getExternalStorageDirectory();
        if (root.canWrite()) {
            File dir = new File(root.getAbsolutePath() + "/ExperimentData");
            dir.mkdirs();
            File file = new File(dir, uniqueFilename());
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                out.write(dataDumpString.getBytes());
                return Uri.fromFile(file);
            } catch (IOException e2) {
                e2.printStackTrace();
                try {
                    out.close();
                } catch (IOException e22) {
                    e22.printStackTrace();
                }
            }
        } else {
            Log.e(TAG, "No permissions to write to disk!");
            throw new RuntimeException("Failed to write CSV to disk!");
        }
    }
}
