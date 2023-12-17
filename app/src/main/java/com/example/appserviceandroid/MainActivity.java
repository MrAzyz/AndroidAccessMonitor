package com.example.appserviceandroid;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for the main activity
        setContentView(R.layout.activity_main);

        // Check if the accessibility service is enabled
        if (!isAccessibilityServiceEnabled()) {
            // If not enabled, open the accessibility settings
            openAccessibilitySettings();
        }
    }

    // Check if the accessibility service is enabled
    private boolean isAccessibilityServiceEnabled() {
        // Define the accessibility service name
        String serviceName = "com.exemple.appserviceandroid/.MyAccessibilityService";

        // Default value for accessibilityEnabled
        int accessibilityEnabled = 0;

        try {
            // Get the accessibility enabled value from the device settings
            accessibilityEnabled = Settings.Secure.getInt(
                    getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        // Check if accessibility is enabled
        if (accessibilityEnabled == 1) {
            // Get the enabled accessibility services
            String settingValue = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            // Check if the defined service is in the list of enabled services
            return settingValue != null && settingValue.contains(serviceName);
        }

        return false;
    }

    // Open the accessibility settings
    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
}
