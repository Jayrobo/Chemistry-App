package com.example.androidusbhost;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.XPositionMetric;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.ui.YPositionMetric;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XValueMarker;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.androidplot.xy.YValueMarker;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends Activity implements OnTouchListener {
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final Float CURRENT_MAX = Float.valueOf((VOLTAGE_HIGH.floatValue() / RESISTOR_FEEDBACK.floatValue()) * 1000000.0f);
    private static final Float CURRENT_MIN = Float.valueOf((VOLTAGE_LOW.floatValue() / RESISTOR_FEEDBACK.floatValue()) * 1000000.0f);
    static int DEFAULT_ENDV = 0;
    static int DEFAULT_SCANRATE = 1;
    static int DEFAULT_SCANUPTO = 0;
    static int DEFAULT_STARTV = 0;
    static final int NONE = 0;
    private static final String NO_SELECTION_TXT = "nothing selected";
    static final int ONE_FINGER_DRAG = 1;
    private static final Float RESISTOR_FEEDBACK = Float.valueOf(100000.0f);
    static final int TWO_FINGERS_DRAG = 2;
    private static final Float VOLTAGE_HIGH = Float.valueOf(1.25f);
    private static final Float VOLTAGE_LOW = Float.valueOf(-1.25f);
    private static final int targetProductID = 10;
    private static final int targetVendorID = 1240;
    private double DOMAIN_MAX;
    private double DOMAIN_MIN;
    Double END_V;
    private double RANGE_MAX;
    private double RANGE_MIN;
    Double SCANUPTO_V;
    Double SCAN_RATE;
    Double START_V;
    Button btnCheck;
    Button btnExport;
    Button btnSettings;
    Button btnStart;
    Button btnStop;
    public SimpleXYSeries dataSeries;
    private LineAndPointFormatter dataSeriesFormat;
    TextView dateText;
    private DatabaseHandler db = new DatabaseHandler(this);
    UsbDevice deviceFound = null;
    Button dialogCancel;
    Button dialogExpCancel;
    Button dialogExport;
    Button dialogStart;
    float distBetweenFingers;
    UsbEndpoint endpointIn = null;
    UsbEndpoint endpointOut = null;
    public long experimentID;
    TextView expidText;
    EditText exportTitle;
    PointF firstFinger;
    float lastScrolling;
    float lastZooming;
    float leftBoundary;
    PendingIntent mPermissionIntent;
    private final BroadcastReceiver mUsbDeviceReceiver = new C00672();
    private final BroadcastReceiver mUsbReceiver = new C00661();
    private LineAndPointFormatter maxFormat;
    private TextLabelWidget maxPeakLabel;
    public SimpleXYSeries maxSeries;
    private PointF maxXY;
    private LineAndPointFormatter minFormat;
    private TextLabelWidget minPeakLabel;
    public SimpleXYSeries minSeries;
    private PointF minXY;
    int mode = 0;
    NumberPicker pickEndV;
    NumberPicker pickScanRate;
    NumberPicker pickScanUpTo;
    NumberPicker pickStartV;
    public XYPlot plot;
    float rightBoundary;
    byte[] startcommand = new byte[5];
    boolean stopThread = false;
    TextView textDeviceName;
    TextView textInfo;
    TextView textRx;
    TextView textSearchedEndpoint;
    TextView textStatus;
    TextView textView;
    ThreadUsbTx threadUsbTx;
    UsbDeviceConnection usbDeviceConnection;
    UsbInterface usbInterface;
    UsbInterface usbInterfaceFound = null;
    UsbManager usbManager;
    float zoomRatio = 2.0f;

    class C00661 extends BroadcastReceiver {
        C00661() {
        }

        public void onReceive(Context context, Intent intent) {
            if (MainActivity.ACTION_USB_PERMISSION.equals(intent.getAction())) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");
                    if (!intent.getBooleanExtra("permission", false)) {
                        Toast.makeText(MainActivity.this, MainActivity.this.getString(C0077R.string.denyPerm), 1).show();
                        MainActivity.this.textStatus.setText("permission denied for device " + device);
                    } else if (device != null) {
                        MainActivity.this.connectUsb();
                    }
                }
            }
        }
    }

    class C00672 extends BroadcastReceiver {
        C00672() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(action)) {
                MainActivity.this.deviceFound = (UsbDevice) intent.getParcelableExtra("device");
                MainActivity.this.connectUsb();
            } else if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action)) {
                UsbDevice device = (UsbDevice) intent.getParcelableExtra("device");
                if (device != null && device == MainActivity.this.deviceFound) {
                    MainActivity.this.releaseUsb();
                }
                MainActivity.this.textInfo.setText("");
            }
        }
    }

    class C00683 implements OnClickListener {
        C00683() {
        }

        public void onClick(View arg0) {
            MainActivity.this.connectUsb();
        }
    }

    class C00725 implements OnClickListener {
        C00725() {
        }

        public void onClick(View v) {
            if (MainActivity.this.usbInterfaceFound != null) {
                MainActivity.this.threadUsbTx.insertCmd(new byte[]{(byte) 84});
                return;
            }
            Toast.makeText(MainActivity.this, MainActivity.this.getString(C0077R.string.noConnect), 1).show();
        }
    }

    class ThreadUsbTx extends Thread {
        private double ADLarge = 0.009766d;
        private double ADSmall = 0.00244d;
        private double Rf = 100000.0d;
        private double Vbaseline = 1.25d;
        byte[] adcStr = new byte[4];
        Queue<byte[]> cmdQueue = new LinkedList();
        byte[] cmdToSent = null;
        private double currentMax = -9999.0d;
        private double currentMin = 9999.0d;
        byte[] dataToRead = new byte[7];
        private DatabaseHandler db;
        private long initialTime;
        byte[] readList;
        byte[] readListt;
        boolean receiving;
        boolean running = true;
        private double toMicro = 1000000.0d;
        UsbDeviceConnection txConnection;
        UsbEndpoint txEndpointIn;
        UsbEndpoint txEndpointOut;
        private short voltMax = (short) 0;
        private short voltMin = (short) 0;
        byte[] voltStr = new byte[4];

        class C00761 implements Runnable {
            C00761() {
            }

            public void run() {
                MainActivity.this.initiateZoomScroll();
                MainActivity.this.btnStart.setEnabled(true);
                MainActivity.this.btnCheck.setEnabled(true);
                MainActivity.this.btnExport.setEnabled(true);
                MainActivity.this.btnStop.setEnabled(false);
                Toast.makeText(MainActivity.this, MainActivity.this.getString(C0077R.string.runcomplete), 0).show();
            }
        }

        ThreadUsbTx(UsbDeviceConnection conn, UsbEndpoint endpointin, UsbEndpoint endpointout) {
            this.db = new DatabaseHandler(MainActivity.this);
            this.txConnection = conn;
            this.txEndpointIn = endpointin;
            this.txEndpointOut = endpointout;
        }

        public void setRunning(boolean r) {
            this.running = r;
        }

        public void insertCmd(byte[] cmd) {
            synchronized (this.cmdQueue) {
                this.cmdQueue.add(cmd);
            }
        }

        public void run() {
            while (this.running) {
                synchronized (this.cmdQueue) {
                    if (this.cmdQueue.size() > 0) {
                        this.cmdToSent = (byte[]) this.cmdQueue.remove();
                    }
                }
                if (this.cmdToSent != null) {
                    int usbResult = MainActivity.this.usbDeviceConnection.bulkTransfer(MainActivity.this.endpointOut, this.cmdToSent, this.cmdToSent.length, 0);
                    if (this.cmdToSent[0] == (byte) 83) {
                        this.receiving = true;
                        boolean tiFlag = true;
                        this.currentMax = -9999.0d;
                        this.currentMin = 9999.0d;
                        while (this.receiving) {
                            if (MainActivity.this.usbDeviceConnection.bulkTransfer(MainActivity.this.endpointIn, this.dataToRead, this.dataToRead.length, 1000) == 7) {
                                this.voltStr[0] = this.dataToRead[0];
                                this.voltStr[1] = this.dataToRead[1];
                                this.voltStr[2] = this.dataToRead[2];
                                this.voltStr[3] = this.dataToRead[3];
                                short volt = (short) (Short.valueOf(new String(this.voltStr), 16).shortValue() - 1000);
                                Double currentValue = Double.valueOf(((-(((((double) Integer.valueOf((this.dataToRead[4] - 1) & MotionEventCompat.ACTION_MASK).intValue()) * this.ADLarge) + (((double) (Integer.valueOf((this.dataToRead[5] - 1) & MotionEventCompat.ACTION_MASK).intValue() / 64)) * this.ADSmall)) - this.Vbaseline)) / this.Rf) * this.toMicro);
                                Long dateTime = Long.valueOf(Calendar.getInstance().getTimeInMillis());
                                if (tiFlag) {
                                    this.initialTime = dateTime.longValue();
                                    tiFlag = false;
                                }
                                if (this.currentMax < currentValue.doubleValue()) {
                                    this.voltMax = volt;
                                    this.currentMax = currentValue.doubleValue();
                                }
                                if (this.currentMin > currentValue.doubleValue()) {
                                    this.voltMin = volt;
                                    this.currentMin = currentValue.doubleValue();
                                }
                                this.db.addEntry(new DataTable(dateTime, Short.valueOf(volt), currentValue));
                                MainActivity.this.dataSeries.addLast(Short.valueOf(volt), currentValue);
                                MainActivity.this.plot.redraw();
                            } else {
                                this.receiving = false;
                                this.voltMin = (short) Math.round((float) this.voltMin);
                                this.voltMax = (short) Math.round((float) this.voltMax);
                                this.currentMin = ((double) Math.round(this.currentMin * 100.0d)) / 100.0d;
                                this.currentMax = ((double) Math.round(this.currentMax * 100.0d)) / 100.0d;
                                MainActivity.this.minSeries.addLast(Short.valueOf(this.voltMin), Double.valueOf(this.currentMin));
                                MainActivity.this.maxSeries.addLast(Short.valueOf(this.voltMax), Double.valueOf(this.currentMax));
                                MainActivity.this.minPeakLabel.setText("minimum peak V = " + Double.toString((double) this.voltMin) + " mV, i = " + Double.toString(this.currentMin) + " uA");
                                MainActivity.this.maxPeakLabel.setText("maximum peak V = " + Double.toString((double) this.voltMax) + " mV, i = " + Double.toString(this.currentMax) + " uA");
                                MainActivity.this.plot.redraw();
                            }
                        }
                    } else {
                        byte b = this.cmdToSent[0];
                    }
                    MainActivity.this.runOnUiThread(new C00761());
                    this.cmdToSent = null;32
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0077R.layout.activity_main);
        definitions();
        initializeGraph();
        final Context context = this;
        this.btnSettings.setEnabled(false);
        this.btnStop.setEnabled(false);
        this.dateText.setText(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
        this.mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        registerReceiver(this.mUsbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
        registerReceiver(this.mUsbDeviceReceiver, new IntentFilter("android.hardware.usb.action.USB_DEVICE_ATTACHED"));
        registerReceiver(this.mUsbDeviceReceiver, new IntentFilter("android.hardware.usb.action.USB_DEVICE_DETACHED"));
        this.btnCheck.setOnClickListener(new C00683());
        this.btnStart.setOnClickListener(new OnClickListener() {
            public void onClick(View arg123) {
                if (MainActivity.this.usbInterfaceFound != null) {
                    MainActivity.this.btnStart.setEnabled(false);
                    MainActivity.this.btnCheck.setEnabled(false);
                    MainActivity.this.btnSettings.setEnabled(false);
                    MainActivity.this.btnExport.setEnabled(false);
                    MainActivity.this.minPeakLabel.setText(null);
                    MainActivity.this.maxPeakLabel.setText(null);
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(C0077R.layout.dialog_expset);
                    dialog.setTitle(MainActivity.this.getString(C0077R.string.dialogCVtitle));
                    dialog.show();
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    MainActivity.this.dialogStart = (Button) dialog.findViewById(C0077R.id.startEx);
                    MainActivity.this.dialogCancel = (Button) dialog.findViewById(C0077R.id.cancelEx);
                    MainActivity.this.pickStartV = (NumberPicker) dialog.findViewById(C0077R.id.startscan_pick);
                    MainActivity.this.pickStartV.setMaxValue(16);
                    MainActivity.this.pickStartV.setMinValue(0);
                    MainActivity.this.pickStartV.setDisplayedValues(new String[]{"-0.60", "-0.50", "-0.40", "-0.30", "-0.20", "-0.10", "0.00", "0.10", "0.20", "0.30", "0.40", "0.50", "0.60", "0.70", "0.80", "0.90", "1.00"});
                    MainActivity.this.pickStartV.setValue(MainActivity.DEFAULT_STARTV);
                    MainActivity.this.pickStartV.setWrapSelectorWheel(false);
                    MainActivity.this.pickStartV.setDescendantFocusability(393216);
                    MainActivity.this.pickScanUpTo = (NumberPicker) dialog.findViewById(C0077R.id.scanupto_pick);
                    MainActivity.this.pickScanUpTo.setMaxValue(16);
                    MainActivity.this.pickScanUpTo.setMinValue(0);
                    MainActivity.this.pickScanUpTo.setDisplayedValues(new String[]{"-0.60", "-0.50", "-0.40", "-0.30", "-0.20", "-0.10", "0.00", "0.10", "0.20", "0.30", "0.40", "0.50", "0.60", "0.70", "0.80", "0.90", "1.00"});
                    MainActivity.this.pickScanUpTo.setValue(MainActivity.DEFAULT_SCANUPTO);
                    MainActivity.this.pickScanUpTo.setWrapSelectorWheel(false);
                    MainActivity.this.pickScanUpTo.setDescendantFocusability(393216);
                    MainActivity.this.pickEndV = (NumberPicker) dialog.findViewById(C0077R.id.endV_pick);
                    MainActivity.this.pickEndV.setMaxValue(16);
                    MainActivity.this.pickEndV.setMinValue(0);
                    MainActivity.this.pickEndV.setDisplayedValues(new String[]{"-0.60", "-0.50", "-0.40", "-0.30", "-0.20", "-0.10", "0.00", "0.10", "0.20", "0.30", "0.40", "0.50", "0.60", "0.70", "0.80", "0.90", "1.00"});
                    MainActivity.this.pickEndV.setValue(MainActivity.DEFAULT_ENDV);
                    MainActivity.this.pickEndV.setWrapSelectorWheel(false);
                    MainActivity.this.pickEndV.setDescendantFocusability(393216);
                    MainActivity.this.pickScanRate = (NumberPicker) dialog.findViewById(C0077R.id.scanrate_pick);
                    MainActivity.this.pickScanRate.setMaxValue(6);
                    MainActivity.this.pickScanRate.setMinValue(1);
                    MainActivity.this.pickScanRate.setDisplayedValues(new String[]{"5", "10", "20", "50", "100", "200"});
                    MainActivity.this.pickScanRate.setValue(MainActivity.DEFAULT_SCANRATE);
                    MainActivity.this.pickScanRate.setWrapSelectorWheel(false);
                    MainActivity.this.pickScanRate.setDescendantFocusability(393216);
                    MainActivity.this.dialogCancel.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            dialog.dismiss();
                            MainActivity.this.btnStart.setEnabled(true);
                            MainActivity.this.btnCheck.setEnabled(true);
                            MainActivity.this.btnSettings.setEnabled(false);
                            MainActivity.this.btnExport.setEnabled(true);
                        }
                    });
                    MainActivity.this.dialogStart.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            MainActivity.this.initializeGraph();
                            double startV = (double) MainActivity.this.pickStartV.getValue();
                            double scanuptoV = (double) MainActivity.this.pickScanUpTo.getValue();
                            double endV = (double) MainActivity.this.pickEndV.getValue();
                            double scanRate = (double) MainActivity.this.pickScanRate.getValue();
                            MainActivity.DEFAULT_STARTV = MainActivity.this.pickStartV.getValue();
                            MainActivity.DEFAULT_SCANUPTO = MainActivity.this.pickScanUpTo.getValue();
                            MainActivity.DEFAULT_ENDV = MainActivity.this.pickEndV.getValue();
                            MainActivity.DEFAULT_SCANRATE = MainActivity.this.pickScanRate.getValue();
                            MainActivity.this.START_V = Double.valueOf((startV - 6.0d) / 10.0d);
                            MainActivity.this.SCANUPTO_V = Double.valueOf((scanuptoV - 6.0d) / 10.0d);
                            MainActivity.this.END_V = Double.valueOf((endV - 6.0d) / 10.0d);
                            MainActivity.this.SCAN_RATE = Double.valueOf(scanRate);
                            byte supVb = (byte) ((int) scanuptoV);
                            byte eVb = (byte) ((int) endV);
                            byte srb = (byte) ((int) scanRate);
                            Log.d("start voltage", Byte.toString((byte) ((int) startV)));
                            Log.d("scan up to", Byte.toString(supVb));
                            Log.d("end voltage", Byte.toString(eVb));
                            Log.d("scan rate", Byte.toString(srb));
                            MainActivity.this.db.deleteAll();
                            dialog.dismiss();
                            byte[] bArr = new byte[]{(byte) 83, sVb, supVb, eVb, srb};
                            MainActivity.this.startcommand = bArr;
                            MainActivity.this.threadUsbTx.insertCmd(MainActivity.this.startcommand);
                        }
                    });
                    return;
                }
                Toast.makeText(MainActivity.this, MainActivity.this.getString(C0077R.string.noConnect), 1).show();
            }
        });
        this.btnStop.setOnClickListener(new C00725());
        this.btnExport.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (MainActivity.this.START_V == null || MainActivity.this.SCANUPTO_V == null || MainActivity.this.END_V == null || MainActivity.this.SCAN_RATE == null) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getString(C0077R.string.noexport), 0).show();
                    return;
                }
                final Dialog dialog_exp = new Dialog(context);
                dialog_exp.setContentView(C0077R.layout.dialog_export);
                dialog_exp.setTitle(MainActivity.this.getString(C0077R.string.dialogExptitle));
                dialog_exp.show();
                dialog_exp.setCancelable(false);
                dialog_exp.setCanceledOnTouchOutside(false);
                MainActivity.this.dialogExport = (Button) dialog_exp.findViewById(C0077R.id.exportconfirm);
                MainActivity.this.dialogExpCancel = (Button) dialog_exp.findViewById(C0077R.id.exportcancel);
                MainActivity.this.exportTitle = (EditText) dialog_exp.findViewById(C0077R.id.exportTitle);
                MainActivity.this.dialogExport.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        String fileName = MainActivity.this.exportTitle.getText().toString();
                        if (TextUtils.isEmpty(fileName)) {
                            MainActivity.this.exportTitle.setError(MainActivity.this.getString(C0077R.string.exptitleerr));
                            return;
                        }
                        ((InputMethodManager) MainActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(MainActivity.this.exportTitle.getWindowToken(), 0);
                        Uri csvUri = new CsvExport(MainActivity.this.db).dumpDatabaseToCsv(MainActivity.this.START_V, MainActivity.this.SCANUPTO_V, MainActivity.this.END_V, MainActivity.this.SCAN_RATE, fileName);
                        dialog_exp.dismiss();
                        MainActivity.this.initializeGraph();
                        MainActivity.this.plot.redraw();
                        Toast.makeText(MainActivity.this, MainActivity.this.getString(C0077R.string.expcomplete), 0).show();
                    }
                });
                MainActivity.this.dialogExpCancel.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        dialog_exp.dismiss();
                        MainActivity.this.plot.redraw();
                    }
                });
            }
        });
    }

    public boolean onTouch(View arg0, MotionEvent event) {
        switch (event.getAction() & MotionEventCompat.ACTION_MASK) {
            case 0:
                this.firstFinger = new PointF(event.getX(), event.getY());
                this.mode = 1;
                this.stopThread = true;
                break;
            case 1:
            case 6:
                this.mode = 0;
                break;
            case 2:
                if (this.mode != 1) {
                    if (this.mode == 2) {
                        float oldDist = this.distBetweenFingers;
                        this.distBetweenFingers = spacing(event);
                        zoom(oldDist / this.distBetweenFingers);
                        this.plot.setDomainBoundaries(Float.valueOf(this.minXY.x), Float.valueOf(this.maxXY.x), BoundaryMode.FIXED);
                        this.plot.redraw();
                        break;
                    }
                }
                PointF oldFirstFinger = this.firstFinger;
                this.firstFinger = new PointF(event.getX(), event.getY());
                scroll(oldFirstFinger.x - this.firstFinger.x);
                this.plot.setDomainBoundaries(Float.valueOf(this.minXY.x), Float.valueOf(this.maxXY.x), BoundaryMode.FIXED);
                this.plot.redraw();
                break;
                break;
            case 5:
                this.distBetweenFingers = spacing(event);
                if (this.distBetweenFingers > 5.0f) {
                    this.mode = 2;
                    break;
                }
                break;
        }
        return true;
    }

    private void zoom(float scale) {
        float domainSpan = this.maxXY.x - this.minXY.x;
        float oldMax = this.maxXY.x;
        float oldMin = this.minXY.x;
        float domainMidPoint = this.maxXY.x - (domainSpan / 2.0f);
        float offset = (domainSpan * scale) / 2.0f;
        this.minXY.x = domainMidPoint - offset;
        this.maxXY.x = domainMidPoint + offset;
        if (this.maxXY.x - this.minXY.x < 5.0f) {
            this.minXY.x = oldMin;
            this.maxXY.x = oldMax;
        }
        if (this.minXY.x < this.leftBoundary) {
            this.minXY.x = this.leftBoundary;
            this.maxXY.x = this.leftBoundary + (this.zoomRatio * domainSpan);
            if (this.maxXY.x > this.dataSeries.getX(this.dataSeries.size() - 1).floatValue()) {
                this.maxXY.x = this.rightBoundary;
            }
        }
        if (this.maxXY.x > this.dataSeries.getX(this.dataSeries.size() - 1).floatValue()) {
            this.maxXY.x = this.rightBoundary;
            this.minXY.x = this.rightBoundary - (this.zoomRatio * domainSpan);
            if (this.minXY.x < this.leftBoundary) {
                this.minXY.x = this.leftBoundary;
            }
        }
    }

    private void scroll(float pan) {
        float domainSpan = this.maxXY.x - this.minXY.x;
        float offset = pan * (domainSpan / ((float) this.plot.getWidth()));
        this.minXY.x += offset;
        this.maxXY.x += offset;
        if (this.minXY.x < this.leftBoundary) {
            this.minXY.x = this.leftBoundary;
            this.maxXY.x = this.leftBoundary + domainSpan;
        }
        if (this.maxXY.x > this.dataSeries.getX(this.dataSeries.size() - 1).floatValue()) {
            this.maxXY.x = this.rightBoundary;
            this.minXY.x = this.rightBoundary - domainSpan;
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt((x * x) + (y * y));
    }

    private void initiateZoomScroll() {
        this.plot.setOnTouchListener(this);
        this.plot.calculateMinMaxVals();
        this.minXY = new PointF(this.plot.getCalculatedMinX().floatValue(), this.plot.getCalculatedMinY().floatValue());
        this.maxXY = new PointF(this.plot.getCalculatedMaxX().floatValue(), this.plot.getCalculatedMaxY().floatValue());
        this.leftBoundary = this.dataSeries.getX(0).floatValue();
        this.rightBoundary = this.dataSeries.getX(this.dataSeries.size() - 1).floatValue();
    }

    private void definitions() {
        this.textRx = (TextView) findViewById(C0077R.id.textrx);
        this.textStatus = (TextView) findViewById(C0077R.id.textstatus);
        this.textDeviceName = (TextView) findViewById(C0077R.id.textdevicename);
        this.textInfo = (TextView) findViewById(C0077R.id.info);
        this.textSearchedEndpoint = (TextView) findViewById(C0077R.id.searchedendpoint);
        this.dateText = (TextView) findViewById(C0077R.id.dateText);
        this.plot = (XYPlot) findViewById(C0077R.id.data_plot);
        this.btnStart = (Button) findViewById(C0077R.id.startButton);
        this.btnCheck = (Button) findViewById(C0077R.id.check);
        this.btnStop = (Button) findViewById(C0077R.id.stopButton);
        this.btnSettings = (Button) findViewById(C0077R.id.settingButton);
        this.btnExport = (Button) findViewById(C0077R.id.csvButton);
        this.minPeakLabel = new TextLabelWidget(this.plot.getLayoutManager(), new SizeMetrics(PixelUtils.dpToPix(100.0f), SizeLayoutType.ABSOLUTE, PixelUtils.dpToPix(100.0f), SizeLayoutType.ABSOLUTE));
        this.maxPeakLabel = new TextLabelWidget(this.plot.getLayoutManager(), new SizeMetrics(PixelUtils.dpToPix(100.0f), SizeLayoutType.ABSOLUTE, PixelUtils.dpToPix(100.0f), SizeLayoutType.ABSOLUTE));
        this.minPeakLabel.getLabelPaint().setTextSize(PixelUtils.dpToPix(16.0f));
        this.minPeakLabel.getLabelPaint().setColor(-16776961);
        this.maxPeakLabel.getLabelPaint().setTextSize(PixelUtils.dpToPix(16.0f));
        this.maxPeakLabel.getLabelPaint().setColor(SupportMenu.CATEGORY_MASK);
    }

    private void initializeGraph() {
        this.plot.clear();
        setPlotStyle();
        setDomainRange();
        this.dataSeries = new SimpleXYSeries("data");
        this.dataSeriesFormat = new LineAndPointFormatter(Integer.valueOf(-16777216), Integer.valueOf(0), null, null);
        this.plot.addSeries(this.dataSeries, this.dataSeriesFormat);
        this.minSeries = new SimpleXYSeries("Min");
        this.minFormat = new LineAndPointFormatter(Integer.valueOf(0), Integer.valueOf(-16776961), null, null);
        this.plot.addSeries(this.minSeries, this.minFormat);
        this.maxSeries = new SimpleXYSeries("Max");
        this.maxFormat = new LineAndPointFormatter(Integer.valueOf(0), Integer.valueOf(SupportMenu.CATEGORY_MASK), null, null);
        this.plot.addSeries(this.maxSeries, this.maxFormat);
        this.minPeakLabel.position(0.0f, XLayoutStyle.RELATIVE_TO_CENTER, PixelUtils.dpToPix(45.0f), YLayoutStyle.ABSOLUTE_FROM_TOP, AnchorPosition.TOP_MIDDLE);
        this.minPeakLabel.pack();
        this.maxPeakLabel.position(0.0f, XLayoutStyle.RELATIVE_TO_CENTER, PixelUtils.dpToPix(65.0f), YLayoutStyle.ABSOLUTE_FROM_TOP, AnchorPosition.TOP_MIDDLE);
        this.maxPeakLabel.pack();
        this.minPeakLabel.setText(" ");
        this.maxPeakLabel.setText(" ");
    }

    private void setPlotStyle() {
        this.plot.getBackgroundPaint().setColor(Color.parseColor("#E3DDD7"));
        this.plot.getGraphWidget().getBackgroundPaint().setColor(Color.parseColor("#E3DDD7"));
        this.plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.parseColor("#80ccff"));
        this.plot.getTitleWidget().getLabelPaint().setColor(-16777216);
        this.plot.getGraphWidget().getDomainLabelPaint().setColor(-16777216);
        this.plot.getDomainLabelWidget().getLabelPaint().setColor(-16777216);
        this.plot.getGraphWidget().getRangeLabelPaint().setColor(-16777216);
        this.plot.getRangeLabelWidget().getLabelPaint().setColor(-16777216);
        this.plot.getGraphWidget().getDomainGridLinePaint().setColor(0);
        this.plot.getGraphWidget().getRangeGridLinePaint().setColor(-1);
        this.plot.getLegendWidget().setVisible(false);
        this.plot.getGraphWidget().setDomainLabelOrientation(-45.0f);
        XValueMarker XMarker = new XValueMarker(Integer.valueOf(0), " ", new YPositionMetric(PixelUtils.dpToPix(-30.0f), YLayoutStyle.ABSOLUTE_FROM_BOTTOM), -16776961, 0);
        YValueMarker YMarker = new YValueMarker(Integer.valueOf(0), " ", new XPositionMetric(PixelUtils.dpToPix(5.0f), XLayoutStyle.ABSOLUTE_FROM_RIGHT), -16776961, 0);
        this.plot.addMarker(XMarker);
        this.plot.addMarker(YMarker);
    }

    private void setDomainRange() {
        this.DOMAIN_MIN = -1000.0d;
        this.DOMAIN_MAX = 1000.0d;
        this.RANGE_MIN = (double) CURRENT_MIN.floatValue();
        this.RANGE_MAX = (double) CURRENT_MAX.floatValue();
        this.plot.setDomainBoundaries(Double.valueOf(this.DOMAIN_MIN), Double.valueOf(this.DOMAIN_MAX), BoundaryMode.AUTO);
        this.plot.setRangeBoundaries(Double.valueOf(this.RANGE_MIN), Double.valueOf(this.RANGE_MAX), BoundaryMode.FIXED);
        this.plot.setDomainValueFormat(new DecimalFormat("0"));
        this.plot.setRangeValueFormat(new DecimalFormat("0"));
        this.plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 100.0d);
        this.plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 2.0d);
        this.plot.setUserDomainOrigin(Integer.valueOf(0));
        this.plot.setUserRangeOrigin(Integer.valueOf(0));
        this.plot.setDomainLabel(getString(C0077R.string.domainLabel));
        this.plot.setRangeLabel(getString(C0077R.string.rangeLabel));
    }

    public void plotGraph(Long exID) {
        int EntrySize = this.db.getAllEntry().size();
        if (EntrySize != 0) {
            boolean tFlag = true;
            Long IniTime = Long.valueOf(0);
            for (int i = 1; i < EntrySize + 1; i++) {
                DataTable Q = this.db.getEntry(i);
                if (tFlag) {
                    IniTime = Q.dateTime;
                    tFlag = false;
                }
                this.dataSeries.addLast(Double.valueOf(((double) (Q.dateTime.longValue() - IniTime.longValue())) / 1000.0d), Q.valADC);
                this.plot.redraw();
            }
        }
    }

    protected void onDestroy() {
        releaseUsb();
        unregisterReceiver(this.mUsbReceiver);
        unregisterReceiver(this.mUsbDeviceReceiver);
        super.onDestroy();
    }

    private void connectUsb() {
        searchEndPoint();
        if (this.usbInterfaceFound != null) {
            setupUsbComm();
            this.threadUsbTx = new ThreadUsbTx(this.usbDeviceConnection, this.endpointIn, this.endpointOut);
            this.threadUsbTx.start();
        }
    }

    private void releaseUsb() {
        Toast.makeText(this, getString(C0077R.string.release_usb), 1).show();
        this.textStatus.setText(getString(C0077R.string.release_usb));
        if (this.usbDeviceConnection != null) {
            if (this.usbInterface != null) {
                this.usbDeviceConnection.releaseInterface(this.usbInterface);
                this.usbInterface = null;
            }
            this.usbDeviceConnection.close();
            this.usbDeviceConnection = null;
        }
        this.deviceFound = null;
        this.usbInterfaceFound = null;
        this.endpointIn = null;
        this.endpointOut = null;
        if (this.threadUsbTx != null) {
            this.threadUsbTx.setRunning(false);
        }
    }

    private void searchEndPoint() {
        this.textInfo.setText("");
        this.textSearchedEndpoint.setText("");
        this.usbInterfaceFound = null;
        this.endpointOut = null;
        this.endpointIn = null;
        if (this.deviceFound == null) {
            for (UsbDevice device : ((UsbManager) getSystemService("usb")).getDeviceList().values()) {
                if (device.getVendorId() == targetVendorID && device.getProductId() == 10) {
                    this.deviceFound = device;
                }
            }
        }
        if (this.deviceFound == null) {
            this.textStatus.setText("device not found");
            Toast.makeText(this, getString(C0077R.string.noDetect), 1).show();
            return;
        }
        this.textInfo.setText(new StringBuilder(String.valueOf(this.deviceFound.toString())).append("\n").append("DeviceID: ").append(this.deviceFound.getDeviceId()).append("\n").append("DeviceName: ").append(this.deviceFound.getDeviceName()).append("\n").append("DeviceClass: ").append(this.deviceFound.getDeviceClass()).append("\n").append("DeviceSubClass: ").append(this.deviceFound.getDeviceSubclass()).append("\n").append("VendorID: ").append(this.deviceFound.getVendorId()).append("\n").append("ProductID: ").append(this.deviceFound.getProductId()).append("\n").append("InterfaceCount: ").append(this.deviceFound.getInterfaceCount()).toString());
        for (int i = 0; i < this.deviceFound.getInterfaceCount(); i++) {
            UsbInterface usbif = this.deviceFound.getInterface(i);
            UsbEndpoint tOut = null;
            UsbEndpoint tIn = null;
            int tEndpointCnt = usbif.getEndpointCount();
            if (tEndpointCnt >= 2) {
                for (int j = 0; j < tEndpointCnt; j++) {
                    if (usbif.getEndpoint(j).getType() == 2) {
                        if (usbif.getEndpoint(j).getDirection() == 0) {
                            tOut = usbif.getEndpoint(j);
                        } else if (usbif.getEndpoint(j).getDirection() == 128) {
                            tIn = usbif.getEndpoint(j);
                        }
                    }
                }
                if (!(tOut == null || tIn == null)) {
                    this.usbInterfaceFound = usbif;
                    this.endpointOut = tOut;
                    this.endpointIn = tIn;
                }
            }
        }
        if (this.usbInterfaceFound == null) {
            this.textSearchedEndpoint.setText("No suitable interface found!");
        } else {
            this.textSearchedEndpoint.setText("UsbInterface found: " + this.usbInterfaceFound.toString() + "\n\n" + "Endpoint OUT: " + this.endpointOut.toString() + "\n\n" + "Endpoint IN: " + this.endpointIn.toString());
        }
    }

    private boolean setupUsbComm() {
        UsbManager manager = (UsbManager) getSystemService("usb");
        Boolean permitToRead = Boolean.valueOf(manager.hasPermission(this.deviceFound));
        if (permitToRead.booleanValue()) {
            this.usbDeviceConnection = manager.openDevice(this.deviceFound);
            if (this.usbDeviceConnection != null) {
                this.usbDeviceConnection.claimInterface(this.usbInterfaceFound, true);
                showRawDescriptors();
                int usbResult = this.usbDeviceConnection.controlTransfer(66, 34, 0, 0, null, 0, 0);
                byte[] encodingSetting = new byte[7];
                encodingSetting[0] = Byte.MIN_VALUE;
                encodingSetting[1] = (byte) 37;
                encodingSetting[6] = (byte) 8;
                usbResult = this.usbDeviceConnection.controlTransfer(66, 32, 0, 0, encodingSetting, 7, 0);
                Toast.makeText(this, getString(C0077R.string.usbConnect), 1).show();
            }
        } else {
            manager.requestPermission(this.deviceFound, this.mPermissionIntent);
            this.textStatus.setText("Permission: " + permitToRead);
        }
        return false;
    }

    private void showRawDescriptors() {
        byte[] buffer = new byte[MotionEventCompat.ACTION_MASK];
        String stringManufacturer = "";
        String stringProduct = "";
        byte[] rawDescriptors = this.usbDeviceConnection.getRawDescriptors();
        try {
            stringManufacturer = new String(buffer, 2, this.usbDeviceConnection.controlTransfer(128, 6, rawDescriptors[14] | 768, 0, buffer, MotionEventCompat.ACTION_MASK, 0) - 2, "UTF-16LE");
        } catch (UnsupportedEncodingException e) {
            this.textStatus.setText(e.toString());
        }
        try {
            stringProduct = new String(buffer, 2, this.usbDeviceConnection.controlTransfer(128, 6, rawDescriptors[15] | 768, 0, buffer, MotionEventCompat.ACTION_MASK, 0) - 2, "UTF-16LE");
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        this.textStatus.setText("Manufacturer: " + stringManufacturer + "\n" + "Product: " + stringProduct);
    }
}
