package com.alexzamurca.animetrackersprint2.series.Database;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;
import com.alexzamurca.animetrackersprint2.series.JSON.Construct;

import org.json.JSONObject;

public class UpdateAirDateChange
{
    private static final String TAG = "Insert";

    String URL = "http://192.168.0.15:2000/updateAirDateChange/";
    private final int user_id;
    private final int anilist_id;
    private final String air_date_change;
    private JSONObject json;

    public UpdateAirDateChange(int user_id, int anilist_id, String air_date_change)
    {
        this.user_id = user_id;
        this.anilist_id = anilist_id;
        this.air_date_change = air_date_change;
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
        this.json = jsonConstructor.constructFormattedUpdateAirDateChangeJSON(air_date_change);
    }

    // 0 = successful new addition, 1: fail
    public int update()
    {
        POST request = new POST(URL, json);
        String response = request.sendRequest();
        if(response.equals("Connection Error"))return 1;
        Log.d(TAG, "insert: updated air_date_change");
        return 0;
    }
}
