package com.alexzamurca.animetrackersprint2.series.Database;

import android.content.Context;

import com.alexzamurca.animetrackersprint2.algorithms.SessionCheck;
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
        URL = "http://192.168.0.15:2000/series/remove/" + session + "/" + anilist_id;
    }

    public boolean remove()
    {
        POST request = new POST(URL, context);
        boolean isRequest200 = request.sendSimpleRequest();

        String response = request.getResponse();
        SessionCheck sessionCheck = new SessionCheck(response, context);
        sessionCheck.check();

        return isRequest200;
    }
}
