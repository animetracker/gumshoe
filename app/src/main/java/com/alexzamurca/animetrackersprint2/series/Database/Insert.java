package com.alexzamurca.animetrackersprint2.series.Database;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;
import com.alexzamurca.animetrackersprint2.series.JSON.Construct;

import org.json.JSONException;
import org.json.JSONObject;

public class Insert
{
    private static final String TAG = "Insert";

    String URL = "http://192.168.0.27:2000/insert/";
    private final int user_id;
    private JSONObject json;

    public Insert(int user_id, JSONObject json)
    {
        this.user_id = user_id;
        constructFromUnformattedJSON(json);
    }

    private void constructFromUnformattedJSON(JSONObject json)
    {
        Construct jsonConstructor = new Construct(user_id, json);
        this.json = jsonConstructor.constructFormattedJSON();
    }

    // 0 = successful new addition, 1 = already in list, 2: fail
    public int insert() {
        IsSeriesInDB isSeriesInDB;
        try
        {
            isSeriesInDB = new IsSeriesInDB(user_id , json.getString("title"));
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
