package com.alexzamurca.animetrackersprint2;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

public class App extends Application implements LifecycleObserver
{
    private static final String TAG = "App";
    
    public static final String SERIES_AIRING_REMINDER_ID = "series_airing_reminder";
    public static final String SETTINGS_ID = "settings";

    @Override
    public void onCreate()
    {
        super.onCreate();

        // Set Up notification channels
        createNotificationChannels();

        mInstance = this;

        // addObserver
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

    }

    private void createNotificationChannels()
    {
        // Define Channels
        NotificationChannel seriesAiringReminderChannel = new NotificationChannel
                (
                        SERIES_AIRING_REMINDER_ID,
                        "Series_Airing_Channel",
                        NotificationManager.IMPORTANCE_HIGH
                );
        seriesAiringReminderChannel.setDescription("Channel to notify about airing series releasing");

        NotificationChannel settingsChannel = new NotificationChannel
                (
                        SETTINGS_ID,
                        "Settings_Channel",
                        NotificationManager.IMPORTANCE_HIGH
                );
        settingsChannel.setDescription("Channel to notify about any settings issues");


        // Create them using notificationManager
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(seriesAiringReminderChannel);
        notificationManager.createNotificationChannel(settingsChannel);
    }


    ///////////////////////////////////////////////
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        Log.d(TAG, "Foreground");
        isAppInBackground(false);
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Log.d(TAG, "Pause");
        isAppInBackground(false);
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        Log.d(TAG, "Background");
        isAppInBackground(true);
    }
///////////////////////////////////////////////



    // Adding some callbacks for test and log
    public interface ValueChangeListener {
        void onChanged(Boolean value);
    }
    private ValueChangeListener visibilityChangeListener;
    public void setOnVisibilityChangeListener(ValueChangeListener listener) {
        this.visibilityChangeListener = listener;
    }
    private void isAppInBackground(Boolean isBackground) {
        if (null != visibilityChangeListener) {
            visibilityChangeListener.onChanged(isBackground);
        }
    }
    private static App mInstance;
    public static App getInstance() {
        return mInstance;
    }
}
