package com.alexzamurca.animetrackersprint2.series.JSON;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class QueryForGraphQL
{
    private static final String TAG = "QueryForGraphQL";
    private final String series_name;

    public QueryForGraphQL(String series_name)
    {
        this.series_name = series_name;
    }

    public JSONObject get(){
        // Building query
        String query = "query ($id: Int, $page: Int, $perPage: Int, $search: String) { " +
                "Page (page: $page, perPage: $perPage) { media (id: $id, search: $search, type:ANIME, sort:STATUS_DESC) { id  coverImage{large} title{ english romaji}  status streamingEpisodes { title }  description averageScore nextAiringEpisode {  airingAt timeUntilAiring  episode}} }" +
                "}";
        // Building variables
        JSONObject variables = new JSONObject();
        try
        {
            variables.put("search", series_name);
            variables.put("page", 1);
            variables.put("perPage", 10);
        }
        catch(JSONException e)
        {
            Log.d(TAG, "get: JSONException when trying to add to variables");
        }

        // Build JSON
        JSONObject json = new JSONObject();
        try
        {
            json.put("query", query);
            json.put("variables", variables);
        }
        catch(JSONException e)
        {
            Log.d(TAG, "get: JSONException when trying to add variables and query to a json object");
        }

        return json;
    }
}
