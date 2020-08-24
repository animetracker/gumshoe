package com.alexzamurca.animetrackersprint2.series.Database;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;
import com.alexzamurca.animetrackersprint2.series.JSON.Construct;

import org.json.JSONObject;

public class UpdateSeriesAiring
{
    private static final String TAG = "Insert";

    String URL = "http://192.168.0.15:2000/updateAiringSeries/";
    private final int user_id;
    private final int anilist_id;
    int next_episode_number;
    String air_date;
    String status;
    private JSONObject json;

    public UpdateSeriesAiring(int user_id, int anilist_id, int next_episode_number, String air_date, String status) {
        this.user_id = user_id;
        this.anilist_id = anilist_id;
        this.next_episode_number = next_episode_number;
        this.air_date = air_date;
        this.status = status;

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
        this.json = jsonConstructor.constructFormattedUpdateSeriesAiringJSON(next_episode_number, air_date, status);
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
