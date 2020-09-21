package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

public class CheckConnection
{
    private static final String TAG = "CheckConnection";
    private Context context;

    public CheckConnection(Context context) {
        this.context = context;
    }

    private boolean hasNetworkConnection()
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e)
        {
            Log.d(TAG, "isOnline: " + e.toString());
        }

        return false;
    }

    public boolean isConnected()
    {
        return isOnline() && hasNetworkConnection();
    }

}
