package com.alexzamurca.animetrackersprint2.Database;

import android.content.Context;

import com.alexzamurca.animetrackersprint2.series.HTTPRequest.POST;
import com.alexzamurca.animetrackersprint2.series.JSON.Construct;

import org.json.JSONObject;

public class AddIcon
{
    private final String URL = "https://gumshoe.digital15.net/account/icons/add/";

    private final String session;
    private final String icon;
    private JSONObject json;
    private Context context;

    public AddIcon(String session, String icon, Context context)
    {
        this.session = session;
        this.icon = icon;
        this.context = context;

        constructJSON();
    }

    private void constructJSON()
    {
        Construct jsonConstructor = new Construct();
        this.json = jsonConstructor.constructAddIconJSON(session, icon);
    }

    public int add()
    {
        POST request = new POST(URL, context, json);
        String response = request.sendRequest();

        if(response.equals("Connection Error"))return 1;
        return 0;
    }
}
