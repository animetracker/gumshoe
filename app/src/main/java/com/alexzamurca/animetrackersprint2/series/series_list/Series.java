package com.alexzamurca.animetrackersprint2.series.series_list;

import java.io.Serializable;

public class Series implements Serializable
{
    String title;
    String cover_image;
    String air_date;
    String description;
    int anilist_id;
    int episode_number;

    public Series(String title, String cover_image, String air_date, String description, int anilist_id, int episode_number) {
        this.title = title;
        this.cover_image = cover_image;
        this.air_date = air_date;
        this.description = description;
        this.anilist_id = anilist_id;
        this.episode_number = episode_number;
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
}