package com.alexzamurca.animetrackersprint2.series.Date;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertDateToMillis
{
    private static final String TAG = "ConvertDateToMillis";


    public long getDate(String stringDate)
    {
        if(stringDate.equals(""))
        {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy H:mm");
            try
            {
                Date date = sdf.parse(stringDate);
                return date.getTime();
            }
            catch(ParseException e)
            {
                Log.d(TAG, "getDate: ParseException when converting date to millis");
                return -1;
            }
        }
        return -1;
    }
}
