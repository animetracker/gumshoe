package com.alexzamurca.animetrackersprint2.series.JSON;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SortFiltering
{
    private static final String TAG = "SortFiltering";
    public JSONArray filterJSON(JSONObject json)
    {
        JSONArray jsonArray = new JSONArray();
        try
        {
            jsonArray = json.getJSONObject("data").getJSONObject("Page").getJSONArray("media");
        }
        catch(JSONException e)
        {
            Log.d(TAG, "filterJSON: JSONException when trying to get the array in data/Page/media");
        }

        JSONArray array = new JSONArray();
        for(int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject dummy = new JSONObject();
            try
            {
                dummy = jsonArray.getJSONObject(i);
            }
            catch(JSONException e)
            {
                Log.d(TAG, "filterJSON: JSONException when trying to get object from array at position:" + i);
            }
            String status = "";
            try
            {
                status = dummy.getString("status");
            }
            catch(JSONException e)
            {
                Log.d(TAG, "filterJSON: JSONException when trying to get status from object");
            }
            if(status.equals("FINISHED"))
            {
                break;
            }
            if(status.equals("CANCELLED"))
            {
                continue;
            }
            else
            {
                array.put(dummy);
            }
        }
        return reverseJSONArray(array);
    }

    private JSONArray reverseJSONArray(JSONArray jsonArray)
    {
        JSONArray newJsonArray = new JSONArray();
        for (int i = jsonArray.length()-1; i>=0; i--) {
            try
            {
                newJsonArray.put(jsonArray.get(i));
            }
            catch(JSONException e)
            {
                Log.d(TAG, "reverseJSONArray: JSONException when trying to get object from array at position:" + i + "and then adding it to a new json array");
            }

        }
        return newJsonArray;
    }
}
