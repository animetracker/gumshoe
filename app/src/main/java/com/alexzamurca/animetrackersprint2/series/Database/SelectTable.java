package com.alexzamurca.animetrackersprint2.series.Database;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.GET;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class SelectTable
{
    private static final String TAG = "SelectTable";
    String URL = "http://192.168.0.15:2000/list/";

    private final int userId;
    private boolean wasRequestSuccessful;

    public SelectTable(int userId)
    {
        this.userId = userId;
    }

    public boolean getWasRequestSuccessful()
    {
        return wasRequestSuccessful;
    }

    public ArrayList<Series> getSeriesList() {

        ArrayList<Series> seriesList = new ArrayList<>();

        //GET request
        GET get = new GET(URL + userId);
        JSONArray jsonResponse;
        try
        {
            jsonResponse = new JSONArray(get.sendRequest());
            Log.d(TAG, "getSeriesList: jsonresponse: \n" + jsonResponse.toString(4) + "\n\n\n");
        }
        catch(JSONException e)
        {
            Log.d(TAG, "getTitles: JSONException when trying to form a JSON from get request response!");
            wasRequestSuccessful = false;
            return seriesList;
        }
        
        for(int i = 0; i < jsonResponse.length(); i++)
        {
            String title = "";
            int next_episode_number = 0;
            String air_date ="";
            String cover_image = "";


            try {
                title = jsonResponse.getJSONObject(i).getString("title");
            } catch (JSONException e) {
                Log.d(TAG, "getTitles: JSONException when trying to get title");
            }

            try {
                next_episode_number = jsonResponse.getJSONObject(i).getInt("next_episode_number");
            } catch (JSONException e) {
                Log.d(TAG, "getTitles: JSONException when trying to get next_episode_number");
            }

            try {
                air_date = jsonResponse.getJSONObject(i).getString("air_date");
                if(air_date.equals(""))
                {
                    air_date = "Unknown Date";
                }
            } catch (JSONException e) {
                Log.d(TAG, "getTitles: JSONException when trying to get air_date");
            }

            try
            {
                cover_image = jsonResponse.getJSONObject(i).getString("cover_image");
            }
            catch(JSONException e)
            {
                Log.d(TAG, "getSearchResults: JSONException when trying to get cover_image");
            }

            seriesList.add(new Series(title, cover_image, air_date, next_episode_number));
        }
        wasRequestSuccessful = true;
        return seriesList;
    }
}
