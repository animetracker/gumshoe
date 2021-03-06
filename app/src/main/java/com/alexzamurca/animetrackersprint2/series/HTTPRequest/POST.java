package com.alexzamurca.animetrackersprint2.series.HTTPRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.alexzamurca.animetrackersprint2.dialog.NoConnectionDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class POST
{
    private final String url;
    private final Context context;
    private JSONObject json_to_send;
    private String response;

    private static final String TAG = "POST";

    public POST(String url, Context context) {
        this.url = url;
        this.context = context;
    }

    public POST(String url, Context context, JSONObject json_to_send)
    {
        this.url = url;
        this.context = context;
        this.json_to_send = json_to_send;
    }

    public boolean sendSimpleRequest()
    {
        Log.d(TAG, "sendSimpleRequest: to URL: " + url);
        try
        {
            // Establish connection / request
            URL url_object = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) url_object.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            urlConnection.connect();

            InputStream is;
            if (urlConnection.getResponseCode() == 200)
            {
                is = urlConnection.getInputStream();
            }
            else
            {
                is = urlConnection.getErrorStream();
            }
            response = getStringFromInputStream(is);

            return urlConnection.getResponseCode()==200;
        }
        catch(Exception e)
        {
            Log.e("CATCH", e.toString());
            connectionError();
        }

        return false;
    }

    public String getResponse()
    {
        return response;
    }

    public String sendRequest()
    {
        Log.d(TAG, "sendRequest: sent to URL: " + url);
        try
        {
            // Establish connection / request
            URL url_object = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) url_object.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);

            // Send request
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

            Log.d(TAG, "sendRequest: " + json_to_send.toString());

            writer.write(json_to_send.toString());
            writer.flush();
            writer.close();
            urlConnection.connect();

            InputStream is;

            if (urlConnection.getResponseCode() == 200)
            {
                is = urlConnection.getInputStream();
            }
            else
            {
                is = urlConnection.getErrorStream();
            }
            return getStringFromInputStream(is);
        }
        catch(Exception e)
        {
            Log.e("CATCH", e.toString());
            connectionError();
        }
        return "Response returned nothing";
    }

    private String getStringFromInputStream(InputStream is)
    {
        // Read response
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String dummy;
        try
        {
            while((dummy = reader.readLine())!=null) {
                response.append(dummy);
            }
        }
        catch(IOException e)
        {
            Log.d(TAG, "getStringFromInputStream: IOException");
        }
        catch(NullPointerException e)
        {
            Log.d(TAG, "getStringFromInputStream: NullPointerException");
        }
        return response.toString();
    }

    private void connectionError()
    {
        NoConnectionDialog noConnectionDialog = new NoConnectionDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("update_list", true);
        noConnectionDialog.setArguments(bundle);
        noConnectionDialog.show(((FragmentActivity)context).getSupportFragmentManager(), "NoConnectionDialog");
    }
}
