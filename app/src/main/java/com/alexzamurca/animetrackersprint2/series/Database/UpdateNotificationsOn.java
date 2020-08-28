package com.alexzamurca.animetrackersprint2.series.Database;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;
import com.alexzamurca.animetrackersprint2.series.JSON.Construct;

import org.json.JSONObject;

public class UpdateNotificationsOn
{
    private static final String TAG = "Insert";

    String URL = "http://192.168.0.15:2000/series/updateNotificationsOn/";
    private final String session;
    private final int anilist_id;
    private final int notifications_on;
    private JSONObject json;

    public UpdateNotificationsOn(String session, int anilist_id, int notifications_on)
    {
        this.session = session;
        this.anilist_id = anilist_id;
        this.notifications_on = notifications_on;
        constructURL();
        constructJSON();
    }

    private void constructURL()
    {
        URL = URL + session + "/" + anilist_id;
    }

    private void constructJSON()
    {
        Construct jsonConstructor = new Construct();
        this.json = jsonConstructor.constructUpdateNotificationsOnJSON(notifications_on);
    }

    // 0 = successful new addition, 1: fail
    public int update()
    {
        POST request = new POST(URL, json);
        String response = request.sendRequest();
        if(response.equals("Connection Error"))return 1;
        Log.d(TAG, "insert: updated notifications_on");
        return 0;
    }
}
