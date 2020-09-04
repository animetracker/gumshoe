package com.alexzamurca.animetrackersprint2.series.AniList;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.Date.ConvertMillisToDate;

import org.json.JSONException;
import org.json.JSONObject;

public class GetSeriesInfo
{
    private static final String TAG = "GetSeriesInfo";

    private int anilist_id;
    private int episode_number = -1;
    private String air_date = "";
    private String status = "";

    private JSONObject response;

    public GetSeriesInfo(int anilist_id)
    {
        this.anilist_id = anilist_id;
    }

    public void sendRequest()
    {
        GraphQLRequest graphQLRequest = new GraphQLRequest();
        try
        {
            response = graphQLRequest.getInfoJSONResponse(anilist_id).getJSONObject("data").getJSONObject("Media");
        }
        catch(JSONException e)
        {
            Log.d(TAG, "sendRequest: JSONException when trying to get response");
        }

        // Getting episode number
        try
        {
            episode_number =  response.getJSONObject("nextAiringEpisode").getInt("episode");
        }
        catch(JSONException e)
        {
            String current_episode;

            try
            {
                current_episode = response.getJSONArray("streamingEpisodes").getJSONObject(0).getString("title");
                episode_number  = Integer.parseInt(current_episode.substring(current_episode.indexOf(" ")+1, current_episode.indexOf("-")-1)) + 1;
            }
            catch(JSONException j)
            {
                Log.d(TAG, "sendRequest: JSONException when trying to get current episode through alternative route");
            }
            catch(NumberFormatException n)
            {
                Log.d(TAG, "sendRequest: NumberFormatException when trying to convert string to number");
            }


        }

        // Getting air date
        try
        {
            ConvertMillisToDate convertMillisToDate = new ConvertMillisToDate(response.getJSONObject("nextAiringEpisode").getInt("airingAt"));
            air_date = convertMillisToDate.getDate();
        }
        catch(JSONException j)
        {
            Log.d(TAG, "sendRequest: JSONException when trying to get air date from JSON");
        }

        // Getting status
        try
        {
            status = response.getString("status");
        }
        catch(JSONException e)
        {
            Log.d(TAG, "sendRequest: JSONException when trying to get status");
        }


    }

    public String getAir_date()
    {
        return air_date;
    }

    public int getEpisode_number()
    {
        return episode_number;
    }

    public String getStatus() {
        return status;
    }
}
