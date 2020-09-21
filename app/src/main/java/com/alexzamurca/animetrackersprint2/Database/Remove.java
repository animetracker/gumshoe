package com.alexzamurca.animetrackersprint2.Database;

import android.content.Context;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;

public class Remove
{
    private String session;
    private int anilist_id;
    private Context context;
    private String URL;

    public Remove(String session, int anilist_id, Context context) {
        this.session = session;
        this.anilist_id = anilist_id;
        this.context = context;
        constructURL();
    }

    private void constructURL()
    {
        URL = "https://gumshoe.digital15.net/series/remove/" + session + "/" + anilist_id;
    }

    public boolean remove()
    {
        POST request = new POST(URL, context);
        return request.sendSimpleRequest();
    }
}
