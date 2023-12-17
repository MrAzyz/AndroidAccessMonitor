package com.example.appserviceandroid;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class BackendDataSender extends AsyncTask<String, Void, Void> {

    private Context context;

    // Constructor to initialize the context
    public BackendDataSender(Context context) {
        this.context = context;
    }

    // AsyncTask method for background data sending
    @Override
    protected Void doInBackground(String... params) {
        try {
            // Extracting parameters
            String packageName = params[0];
            String currentTime = params[1];
            String formattedScreenOnTime = params[2];

            // Backend server URL
            String backendUrl = "your_backend_url";

            // Creating HTTP connection
            HttpURLConnection urlConnection = createUrlConnection(backendUrl);

            // Building JSON input string
            String jsonInputString = buildJsonInputString(packageName, currentTime, formattedScreenOnTime);

            // Sending data to the backend
            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Getting the HTTP response code
            int responseCode = urlConnection.getResponseCode();
            Log.d("MyAccessibilityService", "Backend Server Response Code: " + responseCode);

            // Disconnecting the connection
            urlConnection.disconnect();
        } catch (Exception e) {
            // Logging any errors that occur during the process
            Log.e("MyAccessibilityService", "Error sending data to the backend", e);
        }
        return null;
    }

    // Helper method to create a URL connection
    private HttpURLConnection createUrlConnection(String backendUrl) throws Exception {
        URL url = new URL(backendUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setDoOutput(true);
        return urlConnection;
    }

    // Helper method to build JSON input string
    private String buildJsonInputString(String packageName, String currentTime, String formattedScreenOnTime) {
        // Getting the device ID
        String deviceId = getDeviceId(context);

        // Formatting the JSON input string
        return String.format(Locale.getDefault(),
                "{\"deviceId\": \"%s\", \"packageName\": \"%s\", \"currentTime\": \"%s\", \"screenOnTime\": \"%s\"}",
                deviceId, packageName, currentTime, formattedScreenOnTime);
    }

    // Helper method to get the device ID
    private String getDeviceId(Context context) {
        String deviceId = "";

        // Retrieving the device ID based on the Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            deviceId = Build.BOARD + Build.BRAND + Build.DEVICE + Build.DISPLAY + Build.HOST
                    + Build.ID + Build.MANUFACTURER + Build.MODEL + Build.PRODUCT + Build.TAGS
                    + Build.TYPE + Build.USER;
        }

        return deviceId;
    }
}
