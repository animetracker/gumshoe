package com.alexzamurca.animetrackersprint2.series.Database;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;

public class Remove
{
    private String session;
    private int anilist_id;
    private String URL;

    public Remove(String session, int anilist_id) {
        this.session = session;
        this.anilist_id = anilist_id;
        constructURL();
    }

    private void constructURL()
    {
        URL = "http://192.168.0.15:2000/series/remove/" + session + "/" + anilist_id;
    }

    public boolean remove()
    {
        POST request = new POST(URL);
        return request.sendSimpleRequest();
    }
}
