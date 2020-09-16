package com.alexzamurca.animetrackersprint2.series.add_series;

import java.io.Serializable;

public class SearchResult implements Serializable
{
    private String title;
    private String air_date;
    private int next_episode_number;
    private String romaji;
    private String rating;
    private String description;
    private boolean isAdult;
    private int active_users;
    private String start_date;
    private String trailer_URL;
    private String status;
    private String image_directory;

    public SearchResult(String title, String air_date, int next_episode_number, String romaji, String rating, String description, boolean isAdult, int active_users, String start_date, String trailer_URL, String status, String image_directory) {
        this.title = title;
        this.air_date = air_date;
        this.next_episode_number = next_episode_number;
        this.romaji = romaji;
        this.rating = rating;
        this.description = description;
        this.isAdult = isAdult;
        this.active_users = active_users;
        this.start_date = start_date;
        this.trailer_URL = trailer_URL;
        this.status = status;
        this.image_directory = image_directory;
    }

    public String getTitle() {
        return title;
    }

    public String getRating() {
        return rating;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getImage_directory() {
        return image_directory;
    }

    public boolean getIsAdult() {
        return isAdult;
    }

    public int getActive_users() {
        return active_users;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getTrailer_URL() {
        return trailer_URL;
    }

    public String getAir_date() {
        return air_date;
    }

    public int getNext_episode_number() {
        return next_episode_number;
    }

    public String getRomaji() {
        return romaji;
    }
}
