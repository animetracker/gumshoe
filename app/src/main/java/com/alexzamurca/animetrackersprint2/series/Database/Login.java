package com.alexzamurca.animetrackersprint2.series.Database;

import android.content.Context;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;
import com.alexzamurca.animetrackersprint2.series.JSON.Construct;

import org.json.JSONObject;

public class Login
{
    private Context context;

    public Login(Context context) {
        this.context = context;
    }

    public String login(String email, String password)
    {
        Construct construct = new Construct();
        JSONObject json = construct.constructLoginJSON(email, password);
        String URL = "http://192.168.0.15:2000/login/";
        POST request = new POST(URL, context, json);

        return request.sendRequest();
    }
}
