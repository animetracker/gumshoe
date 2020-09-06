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
    String URL = "http://192.168.0.15:2000/series/list/";

    private final String session;
    private boolean wasRequestSuccessful;

    public SelectTable(String session)
    {
        this.session = session;
    }

    public boolean getWasRequestSuccessful()
    {
        return wasRequestSuccessful;
    }

    public ArrayList<Series> getSeriesList() {

        ArrayList<Series> seriesList = new ArrayList<>();

        //GET request
        GET get = new GET(URL + session);
        JSONArray jsonResponse;
        try
        {
            String s = get.sendRequest();
            Log.d(TAG, "getSeriesList: " + s);
            jsonResponse = new JSONArray(s);
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
            String description = "";
            String status = "";
            int anilist_id = -1;
            int notifications_on = 1;
            String notification_change = "";
            String air_date_change = "";


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

            try {
                description = jsonResponse.getJSONObject(i).getString("description");
            } catch (JSONException e) {
                Log.d(TAG, "getTitles: JSONException when trying to get description");
            }

            try {
                status = jsonResponse.getJSONObject(i).getString("status");
            } catch (JSONException e) {
                Log.d(TAG, "getTitles: JSONException when trying to get status");
            }

            try {
                anilist_id = jsonResponse.getJSONObject(i).getInt("anilist_id");
            } catch (JSONException e) {
                Log.d(TAG, "getTitles: JSONException when trying to get anilist_id");
            }

            try {
                notification_change = jsonResponse.getJSONObject(i).getString("notification_change");
            } catch (JSONException e) {
                Log.d(TAG, "getTitles: JSONException when trying to get notification_change");
            }

            try {
                air_date_change = jsonResponse.getJSONObject(i).getString("air_date_change");
            } catch (JSONException e) {
                Log.d(TAG, "getTitles: JSONException when trying to get air_date_change");
            }

            try {
                notifications_on = jsonResponse.getJSONObject(i).getInt("notifications_on");
            } catch (JSONException e) {
                Log.d(TAG, "getTitles: JSONException when trying to get notifications_on");
            }

            seriesList.add(new Series(title, cover_image, air_date, description, status, notification_change, air_date_change, anilist_id , next_episode_number  ,notifications_on));
        }
        wasRequestSuccessful = true;
        return seriesList;
    }

}
