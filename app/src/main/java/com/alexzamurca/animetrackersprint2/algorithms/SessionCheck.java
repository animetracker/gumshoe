package com.alexzamurca.animetrackersprint2.algorithms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.dialog.CheckConnection;
import com.alexzamurca.animetrackersprint2.login.LoginActivity;
import com.alexzamurca.animetrackersprint2.notifications.SessionExpiredNotification;

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

        SharedPreferences sharedPreferences = context.getSharedPreferences("App", Context.MODE_PRIVATE);
        boolean connection_error = sharedPreferences.getBoolean("db_connect_problem", false);
        if(!connection_error)
        {
            Log.d(TAG, "openLoginActivity: opening LoginActivity from SessionCheck because the session check failed");
            Intent intent = new Intent(context, LoginActivity.class);
            // makes sure you cant press back to get to the list screen
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            ((Activity)context).finish();
        }
    }

    public void check()
    {
        if(hasSessionExpired())
        {
            CheckConnection checkConnection = new CheckConnection(context);
            boolean isConnectedToInternet = checkConnection.isConnected();
            if (isConnectedToInternet)
            {
                AppGround appGround = new AppGround();
                boolean isAppOnForeground = appGround.isAppOnForeground(context);

                if(isAppOnForeground)
                {
                    Log.d(TAG, "session check has expired while having internet and app being in foreground");

                    SharedPreferences sharedPreferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putBoolean("logged_in", false);
                    editor.putString("session", "");
                    editor.putBoolean("has_session_expired", true);
                    editor.apply();

                    openLoginActivity();
                }
                else
                {
                    SessionExpiredNotification sessionExpiredNotification = new SessionExpiredNotification(context);
                    sessionExpiredNotification.showNotification();
                }
            }
            else
            {
                Log.d(TAG, "getting table: NO INTERNET");


            }
            Log.d(TAG, "check: failed session check");
        }
    }
}
