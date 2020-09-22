package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class CheckConnection
{
    private Context context;

    public CheckConnection(Context context) {
        this.context = context;
    }

    private boolean hasNetworkConnection()
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockAddress = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockAddress, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) { return false; }
    }

    public boolean isConnected()
    {
        return hasNetworkConnection();
    }

}
