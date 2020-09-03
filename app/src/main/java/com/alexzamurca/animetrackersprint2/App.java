package com.alexzamurca.animetrackersprint2;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class App extends Application
{
    public static final String SERIES_AIRING_REMINDER_ID = "series_airing_reminder";
    public static final String SETTINGS_ID = "settings";

    @Override
    public void onCreate()
    {
        super.onCreate();

        // Set Up notification channels
        createNotificationChannels();

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
}
