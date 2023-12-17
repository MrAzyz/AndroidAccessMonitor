package com.example.appserviceandroid;

import android.accessibilityservice.AccessibilityService;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyAccessibilityService extends AccessibilityService {

    // Instance of BackendDataSender to send data to the backend
    private BackendDataSender backendDataSender;

    // Timestamp when the service started
    private long serviceStartTime;

    // Called when the accessibility service is connected
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        // Initialize BackendDataSender and record the service start time
        backendDataSender = new BackendDataSender(this);
        serviceStartTime = SystemClock.elapsedRealtime();
    }

    // Called when an accessibility event occurs
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Check if the event is a window state change
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            // Handle the window state change event
            handleWindowStateChange(event);
        }
    }

    // Called when the accessibility service is interrupted
    @Override
    public void onInterrupt() {
        // No action for interruption
    }

    // Handle window state change event
    private void handleWindowStateChange(AccessibilityEvent event) {
        // Extract information from the event
        String packageName = String.valueOf(event.getPackageName());
        String currentTime = getCurrentTime();
        String appName = getApplicationName(packageName);
        long screenOnTime = getScreenOnTime();
        String formattedScreenOnTime = formatMillisToHHMMSS(screenOnTime);

        // Build a log message
        String message = buildLogMessage(appName, currentTime, formattedScreenOnTime);
        Log.d("MyAccessibilityService", message);

        // Send data to the backend
        sendDataToBackend(appName, currentTime, formattedScreenOnTime);
    }

    // Build a log message
    private String buildLogMessage(String appName, String currentTime, String formattedScreenOnTime) {
        return appName + " at " + currentTime + ", Screen On Time: " + formattedScreenOnTime;
    }

    // Get the application name from the package name
    private String getApplicationName(String packageName) {
        PackageManager packageManager = getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return packageName;
        }
    }

    // Calculate screen on time
    private long getScreenOnTime() {
        return SystemClock.elapsedRealtime() - serviceStartTime;
    }

    // Get the current time in HH:mm format
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Send data to the backend using BackendDataSender
    private void sendDataToBackend(String packageName, String currentTime, String formattedScreenOnTime) {
        backendDataSender.execute(packageName, currentTime, formattedScreenOnTime);
    }

    // Format milliseconds to HH:mm:ss
    private String formatMillisToHHMMSS(long millis) {
        long hours = millis / 3600000;
        long minutes = (millis % 3600000) / 60000;
        long seconds = (millis % 60000) / 1000;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }
}
