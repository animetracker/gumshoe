package com.alexzamurca.animetrackersprint2.series.AniList;

import android.content.Context;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.JSON.SortFiltering;
import com.alexzamurca.animetrackersprint2.series.add_series.SearchResult;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Search
{
    private JSONArray search_array;

    GraphQLRequest graphQL;

    public Search(String series_name, Context context)
    {
        graphQL = new GraphQLRequest(context);
        SortFiltering sf = new SortFiltering();
        search_array = sf.filterJSON(graphQL.getSearchJSONResponse(series_name));
    }

    public void printList(List<SearchResult> list)
    {
        for(SearchResult sr:list)
        {
            Log.d("printList", "||" + sr.getImage_directory() + "|" + sr.getTitle() + "|" + sr.getStatus() + "|" + sr.getIsAdult() + "|" + sr.getStart_date() + "|"+ sr.getActive_users() + "|"+ sr.getRating() + "|"+ sr.getSynonyms().toString() + "|"+ sr.getTrailer_URL() + "|"+ sr.getDescription()  + "||");
        }
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
                boolean isAdult = false;
                int active_users = -1;
                String start_date;
                ArrayList<String> synonyms = new ArrayList<>();
                String trailer_URL = "";
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
                    isAdult = search_array.getJSONObject(i).getBoolean("isAdult");
                }
                catch(JSONException e)
                {
                    Log.e("getList" , "Can't get isAdult");
                }

                try
                {
                    active_users = search_array.getJSONObject(i).getInt("popularity");
                }
                catch(JSONException e)
                {
                    Log.e("getList" , "Can't get popularity");
                }

                int day = -1, month = -1, year = -1;
                try
                {
                    day = search_array.getJSONObject(i).getJSONObject("startDate").getInt("day");
                }
                catch(JSONException e)
                {
                    Log.e("getList" , "Can't get startDate day");
                }

                try
                {
                    month = search_array.getJSONObject(i).getJSONObject("startDate").getInt("month");
                }
                catch(JSONException e)
                {
                    Log.e("getList" , "Can't get startDate month");
                }

                try
                {
                    year = search_array.getJSONObject(i).getJSONObject("startDate").getInt("year");
                }
                catch(JSONException e)
                {
                    Log.e("getList" , "Can't get startDate year");
                }

                if(year!=-1)
                {
                    if(month!=-1)
                    {
                        if(day!=-1)
                        {
                            start_date = day+ "/" + month + "/" + year;
                        }
                        else
                        {
                            start_date = month + "/" + year;
                        }
                    }
                    else
                    {
                        start_date = Integer.toString(year);
                    }
                }
                else
                {
                    start_date = "Unknown";
                }

                try
                {
                    JSONArray array = search_array.getJSONObject(i).getJSONArray("synonyms");
                    for(int j = 0; j<array.length() ; j++)
                    {
                        synonyms.add(array.getString(j));
                    }
                }
                catch(JSONException e)
                {
                    Log.e("getList" , "Can't get synonyms");
                }

                try
                {
                    String video_provider = search_array.getJSONObject(i).getJSONObject("trailer").getString("site");
                    String id =  search_array.getJSONObject(i).getJSONObject("trailer").getString("id");
                    if(video_provider.equals("youtube"))
                    {
                        trailer_URL = "https://www.youtube.com/watch?v=" + id;
                    }
                    else if(video_provider.equals("dailymotion"))
                    {
                        trailer_URL = "https://www.dailymotion.com/video/" + id;
                    }
                }
                catch(JSONException e)
                {
                    Log.e("getList" , "Can't get trailerURL");
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

                list.add(new SearchResult(title, rating, description, isAdult, active_users, start_date, synonyms, trailer_URL, status, image_directory));
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
