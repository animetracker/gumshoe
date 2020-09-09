package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.alexzamurca.animetrackersprint2.login.LoginActivity;

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
        // try get a JSON
        try
        {
            JSONObject response = new JSONObject(responseString);
            Log.d(TAG, "hasSessionExpired: response:" + response.toString(4));
            return response.getBoolean("error");
        }
        catch(JSONException e)
        {
            Log.d(TAG, "hasSessionExpired: trying to get response from get request to check session has not returned a json with error (JSONException)");
            return false;
        }

    }

    private void openLoginActivity() {
        Log.d(TAG, "openLoginActivity: opening LoginActivity from SessionCheck because the session check failed");
        Intent intent = new Intent(context, LoginActivity.class);
        // makes sure you cant press back to get to the list screen
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void check()
    {
        if(hasSessionExpired())
        {
            Log.d(TAG, "check: failed session check");
            //Toast.makeText(context, "Your session has expired, you need to re-login!", Toast.LENGTH_LONG).show();
            openLoginActivity();
        }
    }
}
