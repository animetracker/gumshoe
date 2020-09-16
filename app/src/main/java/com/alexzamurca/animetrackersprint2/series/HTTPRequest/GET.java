package com.alexzamurca.animetrackersprint2.series.HTTPRequest;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.alexzamurca.animetrackersprint2.series.dialog.NoDatabaseDialog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GET
{
    private final String url;
    private Context context;
    private static final String TAG = "GET";

    public GET(String url, Context context) {
        this.url = url;
        this.context = context;
    }

    // Send request with header
    public String sendRequest()
    {
        try
        {
            // Establish connection / request
            URL url_object = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) url_object.openConnection();
            urlConnection.setRequestMethod("GET");

            int responseCode = urlConnection.getResponseCode();
            System.out.println("Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // connection ok
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            }
        }
        catch(Exception e)
        {
            Log.d(TAG, "sendRequest: " + e.toString());

            SharedPreferences sharedPreferences = context.getSharedPreferences("App", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("db_connect_problem", true);
            editor.apply();

            NoDatabaseDialog dialog = new NoDatabaseDialog();
            dialog.show(((FragmentActivity)context).getSupportFragmentManager(), "NoDatabaseDialog");
        }
        Log.d(TAG, "sendRequest: Response returned nothing");
        return "Response returned nothing";
    }

}
