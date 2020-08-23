package com.alexzamurca.animetrackersprint2.series.Database;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.GET;

public class IsSeriesInDB
{
    private final int user_id;
    private final String series_name;
    private static final String TAG = "IsSeriesInDB";

    public IsSeriesInDB(int user_id, String series_name)
    {
        this.user_id = user_id;
        this.series_name = filterName(series_name);
    }

    private String filterName(String name)
    {
        name = name.toLowerCase();
        return name.replaceAll(" ", "%20");
    }

    private String getTitleFromDB()
    {
        GET get = new GET("http://192.168.0.27/findTitle/" + user_id + "/" + series_name);
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
