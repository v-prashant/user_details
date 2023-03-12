package com.example.trendingapp.utils;

/**
 * Created by prashant verma .
 */

public final class ConfigUtil {
    private ConfigUtil() {}

    // Constants for Log
    public static final String LOG_TAG = "IDFCMicroATMv3";

    // Constants for SharedPreferences
    public static final String SHARED_PREFERENCES_FILE_NAME = "mugs_v3.xml";
    public static final String SHARED_PREFERENCES_PASSWORD = "S&YUT^AYGUHD";

    // Networking Constants
    public static final int CONNECTION_TIMEOUT_IN_MINS = 1;
    public static final int READ_TIMEOUT_IN_MINS = 1;
    public static final int WRITE_TIMEOUT_IN_MINS = 1;
    public static final int API_RETRY_THRESHOLD = 2;
}