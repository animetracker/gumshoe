package com.alexzamurca.animetrackersprint2.Database;

import android.content.Context;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.algorithms.SessionCheck;
import com.alexzamurca.animetrackersprint2.series.HTTPRequest.GET;

public class IsSeriesInDB
{
    private final String series_name;
    private final String session;
    private static final String TAG = "IsSeriesInDB";
    private Context context;

    public IsSeriesInDB(String session, String series_name, Context context)
    {
        this.session = session;
        this.series_name = filterName(series_name);
        this.context = context;
    }

    private String filterName(String name)
    {
        name = name.toLowerCase();
        return name.replaceAll(" ", "%20");
    }

    private String getTitleFromDB()
    {
        GET get = new GET("https://gumshoe.digital15.net/series/findTitle/" + session + "/" + series_name, context);
        return get.sendRequest();
    }

    public boolean isSeriesInDB()
    {
        String response = getTitleFromDB();

        SessionCheck sessionCheck = new SessionCheck(response, context);
        sessionCheck.check();

        Log.d(TAG, "isSeriesInDB: Comparing |" + filterName(response) + "| and |" + series_name + "|");
        // So if get request fails it means database is down and so we need to prevent a useless post request
        return filterName(response).equals(series_name);
    }

}
