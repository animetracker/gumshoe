package com.alexzamurca.animetrackersprint2.series.series_list;

import java.io.Serializable;


public class Series implements Serializable
{
    String title, cover_image, air_date, description, notification_change, air_date_change;;
    int anilist_id, episode_number;

    int notifications_on;

    public Series(String title, String cover_image, String air_date, String description, int anilist_id, int episode_number, String notification_change, String air_date_change, int notifications_on) {
        this.title = title;
        this.cover_image = cover_image;
        this.air_date = air_date;
        this.description = description;
        this.anilist_id = anilist_id;
        this.episode_number = episode_number;
        this.notification_change = notification_change;
        this.air_date_change = air_date_change;
        this.notifications_on = notifications_on;
    }

    public int getEpisode_number() {
        return episode_number;
    }

    public String getTitle() {
        return title;
    }

    public String getCover_image() {
        return cover_image;
    }

    public String getAir_date() {
        return air_date;
    }

    public String getDescription() {
        return description;
    }

    public int getAnilist_id() {
        return anilist_id;
    }

    public String getNotification_change()
    {
        return notification_change;
    }

    public String getAir_date_change() {
        return air_date_change;
    }

    public int getNotifications_on() {
        return notifications_on;
    }

    public void setAir_date_change(String air_date_change) {
        this.air_date_change = air_date_change;
    }

    public void setEpisode_number(int episode_number) {
        this.episode_number = episode_number;
    }
}
