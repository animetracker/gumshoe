package com.alexzamurca.animetrackersprint2.series.AniList;

import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.JSON.SortFiltering;
import com.alexzamurca.animetrackersprint2.series.add_series.SearchResult;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Search
{
    private final String series_name;
    private JSONArray search_array;

    GraphQLRequest graphQL;

    public Search(String series_name)
    {
        this.series_name = series_name;
        graphQL = new GraphQLRequest(series_name);
        SortFiltering sf = new SortFiltering();
        search_array = sf.filterJSON(graphQL.getJSONResponse());
    }

    public void printList(List<SearchResult> list)
    {
        for(SearchResult sr:list)
        {
            Log.d("printList", "||" + sr.getImage_directory() + "|" + sr.getTitle() + "|" + sr.getRating() + "|" + sr.getDescription() + "|" + sr.getNext_episode() + "|"+ sr.getStatus() + "||");
        }
    }

    public String getStringSearchResult(SearchResult sr)
    {
            return  "||" + sr.getImage_directory() + "|" + sr.getTitle() + "|" + sr.getRating() + "|" + sr.getDescription() + "|" + sr.getNext_episode() + "|"+ sr.getStatus() + "||";
    }

    public JSONArray getSearchArray()
    {
        return search_array;
    }

    private List<SearchResult> getList()
    {
        List<SearchResult> list = new ArrayList<>();
        try
        {
            for(int i = 0; i < search_array.length(); i++)
            {
                String title;
                String rating;
                String description = "";
                String next_episode;
                String status = "";
                String image_directory = "";
                try
                {
                    String dummy_title = search_array.getJSONObject(i).getJSONObject("title").getString("english");

                    if(dummy_title.equals("null"))
                    {
                        title = search_array.getJSONObject(i).getJSONObject("title").getString("romaji");
                    }
                    else
                    {
                        title = dummy_title;
                    }
                }
                catch(JSONException e)
                {
                    Log.e("getList" , "Couldn't use english title so using romaji instead");
                    title = search_array.getJSONObject(i).getJSONObject("title").getString("romaji");
                    Log.e("getList" , "Romaji worked!");
                }
                try
                {
                    rating = Integer.toString(search_array.getJSONObject(i).getInt("averageScore"));
                }
                catch(JSONException e)
                {
                    Log.e("getList" , "Can't get rating");
                    rating = "N/A";
                }

                try
                {
                    description = search_array.getJSONObject(i).getString("description");
                }
                catch(JSONException e)
                {
                    Log.e("getList" , "Can't get description");
                }

                try
                {
                    next_episode = Integer.toString(search_array.getJSONObject(i).getJSONObject("nextAiringEpisode").getInt("episode"));
                }
                catch(JSONException e)
                {
                    Log.e("getList" , "Can't get next_episode");
                    next_episode = "N/A";
                }

                try
                {
                    status = search_array.getJSONObject(i).getString("status");
                }
                catch(JSONException e)
                {
                    Log.e("getList" , "Can't get status");
                }

                try
                {
                    image_directory = search_array.getJSONObject(i).getJSONObject("coverImage").getString("large");
                }
                catch(JSONException e)
                {
                    Log.e("getList" , "Can't get image_directory");
                }

                list.add(new SearchResult(title, rating, description, next_episode, status, image_directory));
            }
        }
        catch(JSONException e)
        {
            Log.e("getList" , "JSONExpection lol");
        }
        return list;
    }

    public List<SearchResult> getSearchResults()
    {
        return getList();
    }
}
