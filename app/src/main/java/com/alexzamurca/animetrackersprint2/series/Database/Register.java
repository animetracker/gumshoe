package com.alexzamurca.animetrackersprint2.series.Database;

import android.content.Context;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;
import com.alexzamurca.animetrackersprint2.series.JSON.Construct;

import org.json.JSONObject;

public class Register
{
    private Context context;

    public Register(Context context) {
        this.context = context;
    }

    public String register(String username, String email, String password)
    {
        Construct construct = new Construct();
        JSONObject json = construct.constructRegisterJSON(username, email, password);
        String URL = "http://192.168.0.15:2000/account/";
        POST request = new POST(URL, context, json);

        return request.sendRequest();
    }
}
