package com.alexzamurca.animetrackersprint2.series.AniList;

import android.content.Context;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;
import com.alexzamurca.animetrackersprint2.series.JSON.QueryForGraphQL;

import org.json.JSONException;
import org.json.JSONObject;

// This class makes a http POST request to the graphql api anilist
public class GraphQLRequest
{
    private static final String TAG = "GraphQLRequest";

    private Context context;

    public GraphQLRequest(Context context) {
        this.context = context;
    }

    public JSONObject getSearchJSONResponse(String name_of_anime) {
        QueryForGraphQL queryBuilder = new QueryForGraphQL();
        POST post = new POST("https://graphql.anilist.co",context,queryBuilder.getSearchQuery(name_of_anime));
        try
        {
            String response = post.sendRequest();
            Log.d(TAG, "getSearchJSONResponse: response:" + response);
            return new JSONObject(response);
        }
        catch(JSONException e)
        { Log.d(TAG, "getSearchJSONResponse: JSONException when trying to get Search response");}
        return null;
    }

    public JSONObject getInfoJSONResponse(int anilist_id)
    {
        QueryForGraphQL queryBuilder = new QueryForGraphQL();
        POST post = new POST("https://graphql.anilist.co", context, queryBuilder.getInfoQuery(anilist_id));
        try
        {
            String response = post.sendRequest();
            return new JSONObject(response);
        }
        catch(JSONException e)
        {Log.d(TAG, "getInfoJSONResponse: JSONException when trying to get info response");}
        return null;
    }
}

