package com.androidplot.util;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParserException;

public abstract class Configurator {
    protected static final String CFG_ELEMENT_NAME = "config";
    private static final String TAG = Configurator.class.getName();

    protected static int parseResId(Context ctx, String prefix, String value) {
        String[] split = value.split("/");
        if (split.length <= 1 || !split[0].equalsIgnoreCase(prefix)) {
            throw new IllegalArgumentException();
        }
        String pack = split[0].replace("@", "");
        return ctx.getResources().getIdentifier(split[1], pack, ctx.getPackageName());
    }

    protected static int parseIntAttr(Context ctx, String value) {
        try {
            return ctx.getResources().getColor(parseResId(ctx, "@color", value));
        } catch (IllegalArgumentException e) {
            try {
                return Color.parseColor(value);
            } catch (IllegalArgumentException e2) {
                return Integer.parseInt(value);
            }
        }
    }

    protected static float parseFloatAttr(Context ctx, String value) {
        try {
            return ctx.getResources().getDimension(parseResId(ctx, "@dimen", value));
        } catch (IllegalArgumentException e) {
            try {
                return PixelUtils.stringToDimension(value);
            } catch (Exception e2) {
                return Float.parseFloat(value);
            }
        }
    }

    protected static String parseStringAttr(Context ctx, String value) {
        try {
            value = ctx.getResources().getString(parseResId(ctx, "@string", value));
        } catch (IllegalArgumentException e) {
        }
        return value;
    }

    protected static Method getSetter(Class clazz, String fieldId) throws NoSuchMethodException {
        String methodName = "set" + fieldId;
        for (Method method : clazz.getMethods()) {
            if (method.getName().equalsIgnoreCase(methodName)) {
                return method;
            }
        }
        throw new NoSuchMethodException("No such public method (case insensitive): " + methodName + " in " + clazz);
    }

    protected static Method getGetter(Class clazz, String fieldId) throws NoSuchMethodException {
        Log.d(TAG, "Attempting to find getter for " + fieldId + " in class " + clazz.getName());
        return clazz.getMethod("get" + fieldId.substring(0, 1).toUpperCase() + fieldId.substring(1, fieldId.length()), new Class[0]);
    }

    protected static Object getObjectContaining(Object obj, String path) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (obj == null) {
            throw new NullPointerException("Attempt to call getObjectContaining(Object obj, String path) on a null Object instance.  Path was: " + path);
        }
        Log.d(TAG, "Looking up object containing: " + path);
        int separatorIndex = path.indexOf(".");
        if (separatorIndex <= 0) {
            return obj;
        }
        String lhs = path.substring(0, separatorIndex);
        String rhs = path.substring(separatorIndex + 1, path.length());
        Method m = getGetter(obj.getClass(), lhs);
        if (m == null) {
            throw new NullPointerException("No getter found for field: " + lhs + " within " + obj.getClass());
        }
        Log.d(TAG, "Invoking " + m.getName() + " on instance of " + obj.getClass().getName());
        return getObjectContaining(m.invoke(obj, new Object[0]), rhs);
    }

    private static Object[] inflateParams(Context ctx, Class[] params, String[] vals) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object[] out = new Object[params.length];
        int i = 0;
        for (Class param : params) {
            if (Enum.class.isAssignableFrom(param)) {
                out[i] = param.getMethod("valueOf", new Class[]{String.class}).invoke(null, new Object[]{vals[i].toUpperCase()});
            } else if (param.equals(Float.TYPE)) {
                out[i] = Float.valueOf(parseFloatAttr(ctx, vals[i]));
            } else if (param.equals(Integer.TYPE)) {
                out[i] = Integer.valueOf(parseIntAttr(ctx, vals[i]));
            } else if (param.equals(Boolean.TYPE)) {
                out[i] = Boolean.valueOf(vals[i]);
            } else if (param.equals(String.class)) {
                out[i] = parseStringAttr(ctx, vals[i]);
            } else {
                throw new IllegalArgumentException("Error inflating XML: Setter requires param of unsupported type: " + param);
            }
            i++;
        }
        return out;
    }

    public static void configure(Context ctx, Object obj, int xmlFileId) {
        XmlResourceParser xrp = ctx.getResources().getXml(xmlFileId);
        try {
            HashMap params = new HashMap();
            while (xrp.getEventType() != 1) {
                xrp.next();
                String name = xrp.getName();
                if (xrp.getEventType() == 2) {
                    if (name.equalsIgnoreCase(CFG_ELEMENT_NAME)) {
                        for (int i = 0; i < xrp.getAttributeCount(); i++) {
                            params.put(xrp.getAttributeName(i), xrp.getAttributeValue(i));
                        }
                    }
                    configure(ctx, obj, params);
                }
            }
            configure(ctx, obj, params);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } finally {
            xrp.close();
        }
    }

    public static void configure(Context ctx, Object obj, HashMap<String, String> params) {
        for (String key : params.keySet()) {
            try {
                configure(ctx, obj, key, (String) params.get(key));
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (NoSuchMethodException e3) {
                Log.w(TAG, "Error inflating XML: Setter for field \"" + key + "\" does not exist. ");
                e3.printStackTrace();
            }
        }
    }

    protected static void configure(Context ctx, Object obj, String key, String value) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Object o = getObjectContaining(obj, key);
        if (o != null) {
            String fieldId;
            int idx = key.lastIndexOf(".");
            if (idx > 0) {
                fieldId = key.substring(idx + 1, key.length());
            } else {
                fieldId = key;
            }
            Method m = getSetter(o.getClass(), fieldId);
            Class[] paramTypes = m.getParameterTypes();
            if (paramTypes.length >= 1) {
                String[] paramStrs = value.split("\\|");
                if (paramStrs.length == paramTypes.length) {
                    Object[] oa = inflateParams(ctx, paramTypes, paramStrs);
                    Log.d(TAG, "Invoking " + m.getName() + " with arg(s) " + argArrToString(oa));
                    m.invoke(o, oa);
                    return;
                }
                throw new IllegalArgumentException("Error inflating XML: Unexpected number of argments passed to \"" + m.getName() + "\".  Expected: " + paramTypes.length + " Got: " + paramStrs.length);
            }
            throw new IllegalArgumentException("Error inflating XML: no setter method found for param \"" + fieldId + "\".");
        }
    }

    protected static String argArrToString(Object[] args) {
        String out = "";
        for (Object obj : args) {
            out = out + (obj == null ? out + "[null] " : "[" + obj.getClass() + ": " + obj + "] ");
        }
        return out;
    }
}
