package com.alexzamurca.animetrackersprint2.series.HTTPRequest;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class POST
{
    private final String url;
    private JSONObject json_to_send;

    private static final String TAG = "POST";

    public POST(String url, JSONObject json_to_send)
    {
        this.url = url;
        this.json_to_send = json_to_send;
    }

    public POST(String url)
    {
        this.url = url;
    }

    public boolean sendSimpleRequest()
    {
        try
        {
            // Establish connection / request
            URL url_object = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) url_object.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            urlConnection.connect();
            return urlConnection.getResponseCode()==200;
        }
        catch(Exception e)
        {Log.e("CATCH", e.toString());}
        return false;
    }

    public String sendRequest()
    {
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
        catch(ConnectException c)
        {
            return "Connection Error";
        }
        catch(Exception e)
        {Log.e("CATCH", e.toString());}
        return "Response returned nothing";
    }

    private String getStringFromInputStream(InputStream is)
    {
        // Read response
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        }
        catch(UnsupportedEncodingException e)
        {
            Log.d(TAG, "getStringFromInputStream: UnsupportedEncodingException");
        }
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

}