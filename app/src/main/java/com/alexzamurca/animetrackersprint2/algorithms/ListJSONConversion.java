package com.alexzamurca.animetrackersprint2.algorithms;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ListJSONConversion
{
    private static final String TAG = "ListJSONConversion";
    
    public Set<String> listToJSONSet(ArrayList<Series> list)
    {
        if(list.size()==0) return new HashSet<>();
        else
        {
            Set<String> set = new HashSet<>();
            for(Series series: list)
            {
                String stringSeriesJSON = seriesToJSON(series);
                set.add(stringSeriesJSON);
            }
            return set;
        }
    }

    private String seriesToJSON(Series series)
    {
        JSONObject json = new JSONObject();
        
        // TITLE
        try
        {
            json.put("title", series.getTitle());
        }
        catch (JSONException e)
        {
            Log.d(TAG, "seriesToJSON: JSONException when putting title");
        }

        // COVER_IMAGE
        try
        {
            json.put("cover_image", series.getCover_image());
        }
        catch (JSONException e)
        {
            Log.d(TAG, "seriesToJSON: JSONException when putting cover_image");
        }

        // AIR_DATE
        try
        {
            json.put("air_date", series.getAir_date());
        }
        catch (JSONException e)
        {
            Log.d(TAG, "seriesToJSON: JSONException when putting air_date");
        }

        // DESCRIPTION
        try
        {
            json.put("description", series.getDescription());
        }
        catch (JSONException e)
        {
            Log.d(TAG, "seriesToJSON: JSONException when putting description");
        }

        // STATUS
        try
        {
            json.put("status", series.getStatus());
        }
        catch (JSONException e)
        {
            Log.d(TAG, "seriesToJSON: JSONException when putting status");
        }

        // NOTIFICATION_CHANGE
        try
        {
            json.put("notification_change", series.getNotification_change());
        }
        catch (JSONException e)
        {
            Log.d(TAG, "seriesToJSON: JSONException when putting notification_change");
        }

        // AIR_DATE_CHANGE
        try
        {
            json.put("air_date_change", series.getAir_date_change());
        }
        catch (JSONException e)
        {
            Log.d(TAG, "seriesToJSON: JSONException when putting air_date_change");
        }

        // ANILIST_ID
        try
        {
            json.put("anilist_id", series.getAnilist_id());
        }
        catch (JSONException e)
        {
            Log.d(TAG, "seriesToJSON: JSONException when putting anilist_id");
        }

        // NEXT_EPISODE_NUMBER
        try
        {
            json.put("next_episode_number", series.getNext_episode_number());
        }
        catch (JSONException e)
        {
            Log.d(TAG, "seriesToJSON: JSONException when putting next_episode_number");
        }

        // NOTIFICATIONS_ON
        try
        {
            json.put("notifications_on", series.getNotifications_on());
        }
        catch (JSONException e)
        {
            Log.d(TAG, "seriesToJSON: JSONException when putting notifications_on");
        }
        
        return json.toString();
    }

    public ArrayList<Series> jsonSetToList(Set<String> set)
    {
        if(set.size()==0) return new ArrayList<>();
        else
        {
            ArrayList<Series> list = new ArrayList<>();
            for(String string : set)
            {
                Series series = jsonToSeries(string);
                list.add(series);
            }
            return list;
        }
    }
    
    private Series jsonToSeries(String string)
    {
        JSONObject json = null;

        String title="", cover_image="", air_date="", description="", status="", notification_change="", air_date_change="";
        int anilist_id=0, next_episode_number=0, notifications_on=0;
        
        try
        {
            json = new JSONObject(string);
        }
        catch(JSONException e)
        {
            Log.d(TAG, "jsonToSeries: JSONException when trying to convert json to string");
        }

        if(json!=null)
        {

            // TITLE
            try
            {
                title = json.getString("title");
            }
            catch (JSONException e)
            {
                Log.d(TAG, "jsonToSeries: JSONException when getting title");
            }

            // COVER_IMAGE
            try
            {
                cover_image = json.getString("cover_image");
            }
            catch (JSONException e)
            {
                Log.d(TAG, "jsonToSeries: JSONException when getting cover_image");
            }

            // AIR_DATE
            try
            {
                air_date = json.getString("air_date");
            }
            catch (JSONException e)
            {
                Log.d(TAG, "jsonToSeries: JSONException when getting air_date");
            }

            // DESCRIPTION
            try
            {
                description = json.getString("description");
            }
            catch (JSONException e)
            {
                Log.d(TAG, "jsonToSeries: JSONException when getting description");
            }

            // STATUS
            try
            {
                status = json.getString("status");
            }
            catch (JSONException e)
            {
                Log.d(TAG, "jsonToSeries: JSONException when getting status");
            }

            // NOTIFICATION_CHANGE
            try
            {
                notification_change = json.getString("notification_change");
            }
            catch (JSONException e)
            {
                Log.d(TAG, "jsonToSeries: JSONException when getting notification_change");
            }

            // AIR_DATE_CHANGE
            try
            {
                air_date_change = json.getString("air_date_change");
            }
            catch (JSONException e)
            {
                Log.d(TAG, "jsonToSeries: JSONException when getting air_date_change");
            }

            // ANILIST_ID
            try
            {
                anilist_id = json.getInt("anilist_id");
            }
            catch (JSONException e)
            {
                Log.d(TAG, "jsonToSeries: JSONException when getting anilist_id");
            }

            // NEXT_EPISODE_NUMBER
            try
            {
                next_episode_number = json.getInt("next_episode_number");
            }
            catch (JSONException e)
            {
                Log.d(TAG, "jsonToSeries: JSONException when getting next_episode_number");
            }

            // NOTIFICATIONS_ON
            try
            {
                notifications_on = json.getInt("notifications_on");
            }
            catch (JSONException e)
            {
                Log.d(TAG, "jsonToSeries: JSONException when getting notifications_on");
            }
            
        }
        else 
        {
            Log.d(TAG, "jsonToSeries: json is null");
        }
        
        return new Series(title, cover_image, air_date, description, status, notification_change, air_date_change, anilist_id, next_episode_number, notifications_on);
    }
}
