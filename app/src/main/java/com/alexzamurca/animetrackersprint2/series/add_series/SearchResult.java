package com.alexzamurca.animetrackersprint2.series.add_series;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchResult implements Serializable
{
    private String title;
    private String rating;
    private String description;
    private boolean isAdult;
    private int active_users;
    private String start_date;
    private ArrayList<String> synonyms;
    private String trailer_URL;
    private String status;
    private String image_directory;

    public SearchResult(String title, String rating, String description, boolean isAdult, int active_users, String start_date, ArrayList<String> synonyms, String trailer_URL, String status, String image_directory) {
        this.title = title;
        this.rating = rating;
        this.description = description;
        this.isAdult = isAdult;
        this.active_users = active_users;
        this.start_date = start_date;
        this.synonyms = synonyms;
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

    public ArrayList<String> getSynonyms() {
        return synonyms;
    }

    public String getTrailer_URL() {
        return trailer_URL;
    }
}
