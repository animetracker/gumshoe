package com.alexzamurca.animetrackersprint2.series.series_list;

import java.io.Serializable;


public class Series implements Serializable
{
    String title, cover_image, air_date, description, status, notification_change, air_date_change;
    int anilist_id, next_episode_number, notifications_on;

    public Series(String title, String cover_image, String air_date, String description, String status, String notification_change, String air_date_change, int anilist_id, int next_episode_number, int notifications_on) {
        this.title = title;
        this.cover_image = cover_image;
        this.air_date = air_date;
        this.description = description;
        this.status = status;
        this.notification_change = notification_change;
        this.air_date_change = air_date_change;
        this.anilist_id = anilist_id;
        this.next_episode_number = next_episode_number;
        this.notifications_on = notifications_on;
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

    public String getStatus() {
        return status;
    }

    public String getNotification_change() {
        return notification_change;
    }

    public String getAir_date_change() {
        return air_date_change;
    }

    public int getAnilist_id() {
        return anilist_id;
    }

    public int getNext_episode_number() {
        return next_episode_number;
    }

    public int getNotifications_on() {
        return notifications_on;
    }

    public void setNext_episode_number(int next_episode_number) {
        this.next_episode_number = next_episode_number;
    }

    public void setNotification_change(String notification_change) {
        this.notification_change = notification_change;
    }

    public void setAir_date_change(String air_date_change) {
        this.air_date_change = air_date_change;
    }

    public void setAir_date(String air_date) {
        this.air_date = air_date;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
