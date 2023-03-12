package com.example.trendingapp.utils;

import android.util.Log;

public final class LogUtil {
    private LogUtil() {}

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;
    public static final int WTF = 8;

    public static int log(int priority, String tag, String message) {
        switch (priority) {
            case ASSERT:
                return Log.println(priority, getTag(tag), message);
            case DEBUG:
                return Log.d(getTag(tag), message);
            case ERROR:
                return Log.d(getTag(tag), message);
            case INFO:
                return Log.i(getTag(tag), message);
            case VERBOSE:
                return Log.v(getTag(tag), message);
            case WARN:
                return Log.w(getTag(tag), message);
            case WTF:
                return Log.wtf(getTag(tag), message);
            default:
                return 0;
        }
    }

    public static int log(int priority, String tag, Throwable throwable) {
        switch (priority) {
            case WARN:
                return Log.w(getTag(tag), throwable);
            case WTF:
                return Log.wtf(getTag(tag), throwable);
            default:
                return 0;
        }
    }

    public static int log(int priority, String tag, String message, Throwable throwable) {
        switch (priority) {
            case DEBUG:
                return Log.d(getTag(tag), message, throwable);
            case ERROR:
                return Log.d(getTag(tag), message, throwable);
            case INFO:
                return Log.i(getTag(tag), message, throwable);
            case VERBOSE:
                return Log.v(getTag(tag), message, throwable);
            case WARN:
                return Log.w(getTag(tag), message, throwable);
            case WTF:
                return Log.wtf(getTag(tag), message, throwable);
            default:
                return 0;
        }
    }

    private static String getTag(String tag) {
        return (tag == null || "".equals(tag)) ? ConfigUtil.LOG_TAG : tag;
    }
}
