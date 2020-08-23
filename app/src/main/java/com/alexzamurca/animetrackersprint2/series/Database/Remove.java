package com.alexzamurca.animetrackersprint2.series.Database;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;

public class Remove
{
    private int user_id;
    private int anilist_id;
    private String URL;

    public Remove(int user_id, int anilist_id) {
        this.user_id = user_id;
        this.anilist_id = anilist_id;
        constructURL();
    }

    private void constructURL()
    {
        URL = "http://192.168.0.27/remove/" + user_id + "/" + anilist_id + "/";
    }

    public boolean remove()
    {
        POST request = new POST(URL);
        return request.sendSimpleRequest();
    }
}
