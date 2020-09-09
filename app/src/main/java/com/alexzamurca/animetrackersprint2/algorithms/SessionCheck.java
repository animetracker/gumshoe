package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.alexzamurca.animetrackersprint2.LoginActivity;
import com.alexzamurca.animetrackersprint2.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class SessionCheck
{
    private static final String TAG = "SessionCheck";

    private String responseString;
    private Context context;

    public SessionCheck(String responseString, Context context) {
        this.responseString = responseString;
        this.context = context;
    }

    private boolean hasSessionExpired()
    {
        boolean hasSessionExpired;
        if(responseString.equals(""))
        {
            hasSessionExpired = true;
        }
        else
        {
            // try get a JSON
            try
            {
                JSONObject response = new JSONObject(responseString);
                hasSessionExpired = response.getBoolean("error");
            }
            catch(JSONException e)
            {
                Log.d(TAG, "doInBackground: trying to get response from get request to check session has not returned a json with error (JSONException)");
                hasSessionExpired = false;
            }
        }
        return hasSessionExpired;
    }

    private void openLoginActivity() {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public void check()
    {
        if(hasSessionExpired())
        {
            Log.d(TAG, "check: failed session check");
            Toast.makeText(context, "Your session has expired, you need to re-login!", Toast.LENGTH_LONG).show();
            openLoginActivity();
        }
    }
}
