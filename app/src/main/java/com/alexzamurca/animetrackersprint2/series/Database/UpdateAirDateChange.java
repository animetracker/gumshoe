package com.alexzamurca.animetrackersprint2.series.Database;

import android.content.Context;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.algorithms.SessionCheck;
import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;
import com.alexzamurca.animetrackersprint2.series.JSON.Construct;

import org.json.JSONObject;

public class UpdateAirDateChange
{
    private static final String TAG = "Insert";

    String URL = "http://192.168.0.15:2000/series/updateAirDateChange/";
    private final String session;
    private final int anilist_id;
    private final String air_date_change;
    private JSONObject json;
    private Context context;

    public UpdateAirDateChange(String session, int anilist_id, String air_date_change, Context context)
    {
        this.session = session;
        this.anilist_id = anilist_id;
        this.air_date_change = air_date_change;
        this.context = context;
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
        this.json = jsonConstructor.constructUpdateAirDateChangeJSON(air_date_change);
    }

    // 0 = successful new addition, 1: fail
    public int update()
    {
        POST request = new POST(URL, context, json);
        String response = request.sendRequest();

        SessionCheck sessionCheck = new SessionCheck(response, context);
        sessionCheck.check();

        if(response.equals("Connection Error"))return 1;
        Log.d(TAG, "insert: updated air_date_change");
        return 0;
    }
}
