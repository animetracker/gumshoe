package com.alexzamurca.animetrackersprint2.Database;

import android.content.Context;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.GET;

import org.json.JSONException;
import org.json.JSONObject;

public class GetSelectedIcon
{
    private static final String TAG = "GetSelectedIcon";

    private final String session;
    private Context context;

    public GetSelectedIcon(String session, Context context)
    {
        this.session = session;
        this.context = context;
    }

    private String getResponse()
    {
        GET get = new GET("https://gumshoe.digital15.net/account/" + session + "/icons/selected/", context);
        return get.sendRequest();
    }

    public int get()
    {
        String response = getResponse();
        try
        {
            JSONObject json = new JSONObject(response);
            boolean error = json.getBoolean("error");
            Log.d(TAG, "get: error?: " + error);
            if(error) return 0;

            return Integer.parseInt(json.getString("icon"));
        }
        catch(JSONException e)
        {
            Log.d(TAG, "get: ");
        }
        return 0;
    }

}
