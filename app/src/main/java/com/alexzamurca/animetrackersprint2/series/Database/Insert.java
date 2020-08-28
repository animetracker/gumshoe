package com.alexzamurca.animetrackersprint2.series.Database;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;
import com.alexzamurca.animetrackersprint2.series.JSON.Construct;

import org.json.JSONException;
import org.json.JSONObject;

public class Insert
{
    private static final String TAG = "Insert";

    String URL;
    private JSONObject json;
    private String session;

    public Insert(JSONObject json, String session)
    {
        this.session = session;
        constructFromUnformattedJSON(json);
        constructURL();
    }

    private void constructFromUnformattedJSON(JSONObject json)
    {
        Construct jsonConstructor = new Construct();
        this.json = jsonConstructor.constructFormattedInsertJSON(json);
    }

    private void constructURL()
    {
        URL = "http://192.168.0.15:2000/series/insert/" + session;
    }

    // 0 = successful new addition, 1 = already in list, 2: fail
    public int insert() {
        IsSeriesInDB isSeriesInDB;
        try
        {
            isSeriesInDB = new IsSeriesInDB(session ,json.getString("title"));
        }
        catch (JSONException e)
        {
            Log.d(TAG, "insert: error getting title");
            return 2;
        }

        if(!isSeriesInDB.isSeriesInDB())
        {
            POST request = new POST(URL, json);
            String response = request.sendRequest();
            if(response.equals("Connection Error"))return 2;
            Log.d(TAG, "insert: Selected anime is now in your list!");
            return 0;
        }
        else
        {
            Log.d(TAG, "insert: Selected anime is already in your list!");
            return 1;
        }
    }
}
