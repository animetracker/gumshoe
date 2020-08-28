package com.alexzamurca.animetrackersprint2.series.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.GET;

public class IsSeriesInDB
{
    private final String series_name;
    private final String session;
    private static final String TAG = "IsSeriesInDB";

    public IsSeriesInDB(String session, String series_name)
    {
        this.session = session;
        this.series_name = filterName(series_name);
    }

    private String filterName(String name)
    {
        name = name.toLowerCase();
        return name.replaceAll(" ", "%20");
    }

    private String getTitleFromDB()
    {
        GET get = new GET("http://192.168.0.15:2000/series/findTitle/" + session + "/" + series_name);
        return get.sendRequest();
    }

    public boolean isSeriesInDB()
    {
        String response = getTitleFromDB();
        Log.d(TAG, "isSeriesInDB: Comparing |" + filterName(response) + "| and |" + series_name + "|");
        // So if get request fails it means database is down and so we need to prevent a useless post request
        return filterName(response).equals(series_name);
    }

}
