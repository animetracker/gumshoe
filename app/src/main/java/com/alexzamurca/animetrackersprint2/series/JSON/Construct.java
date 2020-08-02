package com.alexzamurca.animetrackersprint2.series.JSON;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.Date.ConvertMillisToDate;

import org.json.JSONException;
import org.json.JSONObject;


public class Construct 
{
    private static final String TAG = "Construct";
    private int user_id;
    JSONObject unformattedJson;

    public Construct(int user_id, JSONObject unformattedJson)
    {
        this.user_id = user_id;
        this.unformattedJson = unformattedJson;
    }

    public JSONObject constructFormattedJSON()
    {
        JSONObject json = new JSONObject();
        try
        {
            json.put("user_id", user_id);
            try
            {
                String title = unformattedJson.getJSONObject("title").getString("english");
                Log.d(TAG, "constructFormattedJSON: English title: " + title);
                if (title.equals("null"))
                {
                    Log.d(TAG, "constructFormattedJSON: english title unavailable so using romaji title instead!");
                    title =  unformattedJson.getJSONObject("title").getString("romaji");
                }
                json.put("title", title);
            }
            catch(JSONException e)
            {
                Log.d(TAG, "constructFormattedJSON: english title unavailable so using romaji title instead!");
                json.put("title", unformattedJson.getJSONObject("title").getString("romaji"));
            }

            json.put("AniList_id", unformattedJson.getInt("id"));

            try
            {
                json.put("next_episode_number", unformattedJson.getJSONObject("nextAiringEpisode").getInt("episode"));
            }
            catch(JSONException e)
            {
                String current_episode;
                int episode_number = -1;
                try
                {
                    current_episode = unformattedJson.getJSONArray("streamingEpisodes").getJSONObject(0).getString("title");
                    episode_number  = Integer.parseInt(current_episode.substring(current_episode.indexOf(" ")+1, current_episode.indexOf("-")-1)) + 1;
                }
                catch(JSONException j)
                {
                    Log.d(TAG, "constructFormattedJSON: JSONException when trying to get current episode through alternative route");
                }
                catch(NumberFormatException n)
                {
                    Log.d(TAG, "constructFormattedJSON: NumberFormatException when trying to convert string to number");
                }
                json.put("next_episode_number", episode_number);
                Log.d(TAG, "constructFormattedJSON: Couldn't get episode number through usual route so we try the hacky way instead");
            }

            json.put("status", unformattedJson.getString("status"));
            String air_date = "";
            try
            {
                ConvertMillisToDate convertMillisToDate = new ConvertMillisToDate(unformattedJson.getJSONObject("nextAiringEpisode").getInt("airingAt"));
                air_date = convertMillisToDate.getDate();
            }
            catch(JSONException j)
            {
                Log.d(TAG, "constructFormattedJSON: JSONException when trying to get air date from JSON");

            }
            json.put("air_date", air_date);

            json.put("cover_image", unformattedJson.getJSONObject("coverImage").getString("large"));

        }
        catch(JSONException e)
        {
            Log.d(TAG, "construct: JSONException");
        }

        return json;
    }
}
