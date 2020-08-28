package com.alexzamurca.animetrackersprint2.series.Database;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;
import com.alexzamurca.animetrackersprint2.series.JSON.Construct;

import org.json.JSONObject;

public class UpdateNotificationChange
{
    private static final String TAG = "Insert";

    String URL = "http://192.168.0.15:2000/series/updateNotificationChange/";
    private final String session;
    private final int anilist_id;
    private final String notifications_change;
    private JSONObject json;

    public UpdateNotificationChange(String session, int anilist_id, String notifications_change)
    {
        this.session = session;
        this.anilist_id = anilist_id;
        this.notifications_change = notifications_change;
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
        this.json = jsonConstructor.constructUpdateNotificationChangeJSON(notifications_change);
    }

    // 0 = successful new addition, 1: fail
    public int update()
    {
        POST request = new POST(URL, json);
        String response = request.sendRequest();
        if(response.equals("Connection Error"))return 1;
        Log.d(TAG, "insert: updated notifications_change");
        return 0;
    }
}
