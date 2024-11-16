package com.example.runningapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.runningapp.db.RunningDatabase
import com.example.runningapp.other.Constants
import com.example.runningapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningapp.other.Constants.KEY_NAME
import com.example.runningapp.other.Constants.KEY_WEIGHT
import com.example.runningapp.other.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesRunningDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, RunningDatabase::class.java, Constants.RUNNING_DATABASE_NAME)
            .build()


    @Provides
    @Singleton
    fun provideDao(db:RunningDatabase) = db.dao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app:Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)


    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences) = sharedPreferences.getString(KEY_NAME, "")?: ""


    @Singleton
    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences) = sharedPreferences.getFloat(KEY_WEIGHT, 0.0f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences) = sharedPreferences.getBoolean(
        KEY_FIRST_TIME_TOGGLE, true)
}