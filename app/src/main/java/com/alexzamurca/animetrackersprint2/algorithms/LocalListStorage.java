package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import org.apache.pig.impl.util.ObjectSerializer;

import java.io.IOException;
import java.util.ArrayList;

public class LocalListStorage
{
    private static final String TAG = "StoreListLocally";
    private SharedPreferences sharedPreferences;

    public LocalListStorage(Context mContext) {
        sharedPreferences = mContext.getSharedPreferences("Series List", Context.MODE_PRIVATE);
    }

    public void store(ArrayList<Series> list)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try
        {
            editor.putString("list", ObjectSerializer.serialize(list));
            Log.d(TAG, "store: serialized series list");
        }
        catch(IOException e)
        {
            Log.d(TAG, "store: IOException when trying to serialize list: " + e.toString());
        }
        editor.apply();
    }

    public ArrayList<Series> get()
    {
        String serializedList = sharedPreferences.getString("list", "");
        if(serializedList!=null)
        {
            Log.d(TAG, "get: serializedList not null");
            if(!serializedList.equals(""))
            {
                Log.d(TAG, "get: we got a list back");

                try
                {
                    ArrayList<Series> storedList = (ArrayList<Series>) ObjectSerializer.deserialize(serializedList);
                    if(storedList!=null)
                    {
                        Log.d(TAG, "get: deserialize worked");
                        return storedList;
                    }
                    else
                    {
                        Log.d(TAG, "get: deserialize didn't work");
                    }

                }
                catch(IOException e)
                {
                    Log.d(TAG, "get: IOException when trying to get list: " + e.toString());
                }
                catch(ClassCastException f)
                {
                    Log.d(TAG, "get: ClassNotFoundException when trying to get list: " + f.toString());
                }

            }
            else
            {
                Log.d(TAG, "get: we didn't get a list back");
            }
        }
        else
        {
            Log.d(TAG, "get: serializedlist null");
        }
       return new ArrayList<>();
    }
}
