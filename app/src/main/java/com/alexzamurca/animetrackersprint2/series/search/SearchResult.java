package com.alexzamurca.animetrackersprint2.series.search;

import java.io.Serializable;

public class SearchResult implements Serializable
{
    private String title;
    private String rating;
    private String description;
    private String next_episode;
    private String status;
    private String image_directory;


    public SearchResult(String title, String rating, String description, String next_episode, String status, String image_directory) {
        this.title = title;
        this.rating = rating;
        this.description = description;
        this.next_episode = next_episode;
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

    public String getNext_episode() {
        return next_episode;
    }

    public String getStatus() {
        return status;
    }

    public String getImage_directory() {
        return image_directory;
    }
}
