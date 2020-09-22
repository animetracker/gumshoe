package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LocalListStorage
{
    private static final String TAG = "StoreListLocally";
    private SharedPreferences sharedPreferences;
    ListJSONConversion listJSONConversion;

    public LocalListStorage(Context mContext) {
        sharedPreferences = mContext.getSharedPreferences("Series List", Context.MODE_PRIVATE);
        listJSONConversion = new ListJSONConversion();
    }

    public void store(ArrayList<Series> list)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> stringSet = listJSONConversion.listToJSONSet(list);
        editor.putStringSet("list", stringSet);
        Log.d(TAG, "store: serialized series list");

        editor.apply();
    }

    public ArrayList<Series> get()
    {
        Set<String> stringSet = sharedPreferences.getStringSet("list", new HashSet<>());
        if(stringSet!=null)
        {
            Log.d(TAG, "get: stringSet is not null");
            if(stringSet.size()!=0)
            {
                Log.d(TAG, "get: stringSet not empty");

                ArrayList<Series> storedList = listJSONConversion.jsonSetToList(stringSet);
                if(storedList!=null)
                {
                    Log.d(TAG, "get: stored series List not null");
                    return storedList;
                }
                else
                {
                    Log.d(TAG, "get: stored series List null");
                }


            }
            else
            {
                Log.d(TAG, "get: stringSet is empty");
            }
        }
        else
        {
            Log.d(TAG, "get: stringSet is null");
        }
       return new ArrayList<>();
    }
}
