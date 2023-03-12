package com.example.trendingapp.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class DeviceUtil {
    /**
     * The constant NOT_AVAILABLE.
     */
    public static final String NOT_AVAILABLE = "N/A";
    private static final String PREFIX_ANDROID_ID = "A";
    private static final String PREFIX_IMEI = "I";

    /**
     * Gets sim serial number.
     *
     * @param context the context
     * @return the sim serial number
     */
    @SuppressLint("HardwareIds")
    public static String getSimSerialNumber(Context context) {
        String serialNumber = NOT_AVAILABLE;

        try {
            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                serialNumber = telephonyManager != null ? telephonyManager.getSimSerialNumber() : null;
            }
            return serialNumber == null || TextUtils.isEmpty(serialNumber) ? NOT_AVAILABLE : serialNumber;
        } catch (Exception e) {
            return NOT_AVAILABLE;
        }
    }

    /**
     * Gets device id.
     *
     * @param c the c
     * @return the device id
     */
    public static String getDeviceId(Context c) {
        String imei;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            imei = NOT_AVAILABLE;
        } else {
            imei = getImei(c);
        }
        String deviceID;
        if (imei == null || imei.equals(NOT_AVAILABLE)) {
            deviceID = PREFIX_ANDROID_ID + getAndroidId(c);
        } else {
            deviceID = PREFIX_IMEI + imei;
        }
        return deviceID;
    }


    /**
     * Gets imei.
     *
     * @param context the context
     * @return the imei
     */
    @SuppressLint("HardwareIds")
    public static String getImei(Context context) {
        String imei = null;
        if (context != null) {
            int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    imei = telephonyManager.getDeviceId();
                }
            }
        }
        if (imei == null || TextUtils.isEmpty(imei)) {
            imei = NOT_AVAILABLE;
        }
        return imei;
    }

    /**
     * Gets android id.
     *
     * @param context the context
     * @return the android id
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        if (context != null && context.getContentResolver() != null) {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            return NOT_AVAILABLE;
        }
    }

    /**
     * Checking the Internet connect whether it is connected or not.
     *
     * @param context use for Interface to global information about an application environment.
     * @return true if Internet connection available else return false.
     */
    public static boolean hasInternetConnection(Context context) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connManager != null ? connManager.getActiveNetworkInfo() : null;

            if (info == null || !info.isAvailable() || !info.isConnected()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets ip address.
     *
     * @param useIPv4 the use i pv 4
     * @return the ip address
     */
    public static String getIPAddress(boolean useIPv4, String defaultIP) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = sAddr.indexOf(':') < 0; // InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port
                                // suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LogUtil.log(LogUtil.ERROR, ex.getMessage(), ex);
        }
        return defaultIP;
    }

    /**
     * Send sms boolean.
     *
     * @param context        the ctx
     * @param simID          the sim id
     * @param toNum          the to num
     * @param centerNum      the center num
     * @param smsText        the sms text
     * @param sentIntent     the sent intent
     * @param deliveryIntent the delivery intent
     * @return the boolean
     */
    @SuppressLint("PrivateApi")
    public static boolean sendSMS(Context context, int simID, String toNum, String centerNum, String smsText, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        String name;

        try {
            if (simID == 0) {
                name = "isms";
                // for model : "Philips T939" name = "isms0"
            } else if (simID == 1) {
                name = "isms2";
            } else {
                throw new Exception("can not get service which for sim '" + simID + "', only 0,1 accepted as values");
            }

            Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            method.setAccessible(true);
            Object param = method.invoke(null, name);

            method = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            Object stubObj = method.invoke(null, param);
            if (Build.VERSION.SDK_INT < 18) {
                method = stubObj.getClass().getMethod("sendText", String.class, String.class, String.class, PendingIntent.class, PendingIntent.class);
                method.invoke(stubObj, toNum, centerNum, smsText, sentIntent, deliveryIntent);
            } else {
                method = stubObj.getClass().getMethod("sendText", String.class, String.class, String.class, String.class, PendingIntent.class, PendingIntent.class);
                method.invoke(stubObj, context.getPackageName(), toNum, centerNum, smsText, sentIntent, deliveryIntent);
            }

            return true;
        } catch (ClassNotFoundException e) {
            LogUtil.log(LogUtil.ERROR, "apipas", "ClassNotFoundException:" + e.getMessage());
        } catch (NoSuchMethodException e) {
            LogUtil.log(LogUtil.ERROR, "apipas", "NoSuchMethodException:" + e.getMessage());
        } catch (InvocationTargetException e) {
            LogUtil.log(LogUtil.ERROR, "apipas", "InvocationTargetException:" + e.getMessage());
        } catch (IllegalAccessException e) {
            LogUtil.log(LogUtil.ERROR, "apipas", "IllegalAccessException:" + e.getMessage());
        } catch (Exception e) {
            LogUtil.log(LogUtil.ERROR, "apipas", "Exception:" + e.getMessage());
        }
        return false;
    }

    /**
     * Gets mac address.
     *
     * @param context the context
     * @return the mac address
     */
    @SuppressLint("HardwareIds")
    public static String getMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager != null ? wifiManager.getConnectionInfo() : null;
        String mac = null;
        if (wInfo != null) {
            mac = wInfo.getMacAddress();
        }
        return mac;
    }

    /**
     * Gets density dpi.
     *
     * @param context the context
     * @return the density dpi
     */
    public static String getDensityDPI(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        if (density >= 4.0) {
            return "xxxhdpi";
        }
        if (density >= 3.0) {
            return "xxhdpi";
        }
        if (density >= 2.0) {
            return "xhdpi";
        }
        if (density >= 1.5) {
            return "hdpi";
        }
        if (density >= 1.0) {
            return "mdpi";
        }
        return "mdpi";
    }

    /**
     * Gets device model.
     *
     * @return the device model
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * Gets device os.
     *
     * @return the device os
     */
    public static String getDeviceOS() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }


    /**
     * Gets location.
     *
     * @param context the context
     * @return the location
     */
    public static String getLocation(Context context) {
        String latitude = SharedPreferenceUtil.getStringSharedPreference(context, SharedPreferenceUtil.LOCATION_LATITUDE);
        String longitude = SharedPreferenceUtil.getStringSharedPreference(context, SharedPreferenceUtil.LOCATION_LONGITUDE);
        if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
            return latitude + "," + longitude;
        }
        return "N/A";
    }


}
