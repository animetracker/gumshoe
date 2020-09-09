package com.alexzamurca.animetrackersprint2.series.Database;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;
import com.alexzamurca.animetrackersprint2.series.JSON.Construct;

import org.json.JSONObject;

public class Register
{
    public String register(String username, String email, String password)
    {
        Construct construct = new Construct();
        JSONObject json = construct.constructRegisterJSON(username, email, password);
        String URL = "http://192.168.0.15:2000/account/";
        POST request = new POST(URL, json);

        return request.sendRequest();
    }
}
