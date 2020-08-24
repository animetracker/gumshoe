package com.alexzamurca.animetrackersprint2.series.Database;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;
import com.alexzamurca.animetrackersprint2.series.JSON.Construct;

import org.json.JSONObject;

public class UpdateNotificationsChange
{
    private static final String TAG = "Insert";

    String URL = "http://192.168.0.15:2000/updateNotificationChange/";
    private final int user_id;
    private final int anilist_id;
    private final int notifications_change;
    private JSONObject json;

    public UpdateNotificationsChange(int user_id, int anilist_id, int notifications_change)
    {
        this.user_id = user_id;
        this.anilist_id = anilist_id;
        this.notifications_change = notifications_change;
        constructURL();
        constructJSON();
    }

    private void constructURL()
    {
        URL = URL + user_id + "/" + anilist_id;
    }

    private void constructJSON()
    {
        Construct jsonConstructor = new Construct();
        this.json = jsonConstructor.constructFormattedUpdateNotificationChangeJSON(notifications_change);
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
