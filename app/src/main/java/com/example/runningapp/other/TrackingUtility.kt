package com.example.runningapp.other

import android.Manifest
import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions

object TrackingUtility {
    fun hasLocationPermissions(context: Context): Boolean {
        // First check foreground permissions
        val hasForegroundPermissions = EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Then check background permission for Android 10 and above
        val hasBackgroundPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            true  // Background permission not needed for Android 9 and below
        }

        return hasForegroundPermissions && hasBackgroundPermission
    }
}