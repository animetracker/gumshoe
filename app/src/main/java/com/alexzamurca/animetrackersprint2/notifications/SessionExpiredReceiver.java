package com.alexzamurca.animetrackersprint2.notifications;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.algorithms.AppGround;
import com.alexzamurca.animetrackersprint2.algorithms.CancelAllAlarms;
import com.alexzamurca.animetrackersprint2.login.LoginActivity;

public class SessionExpiredReceiver extends BroadcastReceiver
{
    private static final String TAG = "SessionExpiredReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d(TAG, "onReceive: received");

        SharedPreferences sharedPreferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("logged_in", false);
        editor.putString("session", "");
        editor.putBoolean("has_session_expired", true);
        editor.apply();

        AppGround appGround = new AppGround();
        boolean inForeground = appGround.isAppOnForeground(context);
        if(inForeground)
        {
            Log.d(TAG, "onReceive: in Foreground");
            openLoginActivity(context);
        }
        else
        {
            Log.d(TAG, "onReceive: in Background");
            SessionExpiredNotification sessionExpiredNotification = new SessionExpiredNotification(context);
            sessionExpiredNotification.showNotification();
        }
    }

    private void openLoginActivity(Context context)
    {
            Log.d(TAG, "openLoginActivity: opening LoginActivity from SessionExpiredReceiver because the session expired and app is in foreground");
            Intent intent = new Intent(context, LoginActivity.class);
            // makes sure you cant press back to get to the list screen
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            ((Activity)context).finish();

    }
}
