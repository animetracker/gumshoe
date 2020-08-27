package com.alexzamurca.animetrackersprint2.Date;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ConvertDateToCalendar
{
    private static final String TAG = "ConvertDateToCalendar";

    public Calendar convert(String dateInFormat)
    {
        Calendar calendar = Calendar.getInstance();
        if(!dateInFormat.equals(""))
        {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy H:mm", Locale.getDefault());
            try
            {
                Date date = sdf.parse(dateInFormat);
                assert date != null;
                calendar.setTime(date);
                return calendar;
            }
            catch(ParseException e)
            {
                Log.d(TAG, "getDate: ParseException when converting Date type to Calendar type");
                return null;
            }
        }
        return null;
    }

    public String reverseConvert(Calendar calendar)
    {
        if(calendar!=null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy H:mm", Locale.getDefault());
            Date date = calendar.getTime();
            return sdf.format(date);
        }
        return "error";
    }

}
