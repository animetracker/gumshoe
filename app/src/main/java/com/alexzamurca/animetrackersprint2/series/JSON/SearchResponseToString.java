package com.alexzamurca.animetrackersprint2.series.JSON;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.Date.ConvertMillisToDate;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import org.json.JSONException;
import org.json.JSONObject;

public class SearchResponseToString
{
    private static final String TAG = "SearchResponseToString";

    public Series getSeries(JSONObject unformattedJson)
    {
        String title;
        int next_episode_number;
        String air_date;
        String cover_image;
        String description;
        int anilist_id;
        int notifications_on = 1;
        String notification_change;
        String air_date_change;
        String status;
        try
        {
            try
            {
                title = unformattedJson.getJSONObject("title").getString("english");
                if (title.equals("null"))
                {
                    title =  unformattedJson.getJSONObject("title").getString("romaji");
                }
            }
            catch(JSONException e)
            {
                title = unformattedJson.getJSONObject("title").getString("romaji");
            }

            anilist_id =  unformattedJson.getInt("id");

            try
            {
                next_episode_number =  unformattedJson.getJSONObject("nextAiringEpisode").getInt("episode");
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
                    Log.d(TAG, "getString: JSONException when trying to get current episode through alternative route");
                }
                catch(NumberFormatException n)
                {
                    Log.d(TAG, "getString: NumberFormatException when trying to convert string to number");
                }
                next_episode_number = episode_number;
            }

            status = unformattedJson.getString("status");
            air_date = "";
            try
            {
                ConvertMillisToDate convertMillisToDate = new ConvertMillisToDate(unformattedJson.getJSONObject("nextAiringEpisode").getInt("airingAt"));
                air_date = convertMillisToDate.getDate();
            }
            catch(JSONException j)
            {
                Log.d(TAG, "constructFormattedJSON: JSONException when trying to get air date from JSON");

            }

            cover_image = unformattedJson.getJSONObject("coverImage").getString("large");


            description = unformattedJson.getString("description");

            notification_change = "";
            air_date_change = "";

            return new Series(title, cover_image, air_date, description, status, notification_change, air_date_change, anilist_id, next_episode_number, notifications_on);
        }
        catch(JSONException e)
        {
            Log.d(TAG, "getString: JSONException whole");
        }

        return null;
    }
}
