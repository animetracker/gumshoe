package com.alexzamurca.animetrackersprint2.series.AniList;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;
import com.alexzamurca.animetrackersprint2.series.JSON.QueryForGraphQL;

import org.json.JSONException;
import org.json.JSONObject;

// This class makes a http POST request to the graphql api anilist
public class GraphQLRequest
{
    private static final String TAG = "GraphQLRequest";
    private final String name_of_anime;

    public GraphQLRequest(String name_of_anime)
    {
        this.name_of_anime = name_of_anime;
    }

    // New way - what will actually happen when you search in app
    public JSONObject getJSONResponse() {
        QueryForGraphQL queryBuilder = new QueryForGraphQL(name_of_anime);
        POST post = new POST("https://graphql.anilist.co",queryBuilder.get());
        try
        {
            Log.println(Log.INFO,"graphQLRequest", post.getResponse());
            return new JSONObject(post.getResponse());
        }
        catch(JSONException e)
        {Log.println(Log.ERROR,"graphQLRequest", "Got a JSON Exception");}
        return null;
    }
}

