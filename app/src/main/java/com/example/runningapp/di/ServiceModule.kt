package com.example.runningapp.di

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.runningapp.MainActivity
import com.example.runningapp.R
import com.example.runningapp.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.runningapp.other.Constants.NOTIFICATION_CHANNEL_ID
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {


    @ServiceScoped
    @Provides
    fun providesPendingIntent(
        @ApplicationContext context:Context
    ) =  PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT or FLAG_MUTABLE
    )




    @ServiceScoped
    @Provides
    fun providesNotificationBuilder(
        @ApplicationContext context:Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_directions_run_black_24)
        .setContentTitle("Running App")
        .setContentText("00:00:00")
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setShowWhen(false)
        .setContentIntent(pendingIntent)



    @ServiceScoped
    @Provides
    fun providesFusedLocation(
        @ApplicationContext context:Context
    )= LocationServices.getFusedLocationProviderClient(context)
}