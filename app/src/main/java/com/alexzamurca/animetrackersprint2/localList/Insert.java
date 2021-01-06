package com.alexzamurca.animetrackersprint2.localList;

import android.content.Context;

import com.alexzamurca.animetrackersprint2.series.JSON.Construct;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Insert
{
    private final JSONObject json;
    private final Context context;

    public Insert(JSONObject json, Context context) {
        this.json = json;
        this.context = context;
    }

    public int insert()
    {
        IsSeriesInList isSeriesInList;
        try
        {
            isSeriesInList = new IsSeriesInList(json.getInt("id"), context);
            if(!isSeriesInList.isSeriesInList())
            {
                LocalListStorage localListStorage = new LocalListStorage(context);
                ArrayList<Series> list = localListStorage.get();

                Construct construct = new Construct();
                Series series = construct.constructSeriesFromInsertJSON(json);
                if(series!=null)
                {
                    list.add(series);
                    localListStorage.store(list);
                    return 0;
                }
                else return 2;
            }
            else
            {
                return 1;
            }
        }
        catch (JSONException e)
        {
            return 2;
        }
    }
}
