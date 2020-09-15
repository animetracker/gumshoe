package com.alexzamurca.animetrackersprint2.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ConvertDateToCalendar
{
    private static final String TAG = "ConvertDateToCalendar";

    public Calendar timeZoneConvert(Context context, String dateInFormat)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar = adjustLocalTimeZoneChanges(context, calendar);
        if(!dateInFormat.equals(""))
        {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy H:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
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

    private Calendar adjustLocalTimeZoneChanges(Context context, Calendar calendar)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("time_zone", Context.MODE_PRIVATE);

        boolean negative_sign = sharedPreferences.getBoolean("is_sign_negative", false);
        int hours_change = sharedPreferences.getInt("hours_to_change", 0);
        int minutes_change = sharedPreferences.getInt("minutes_to_change", 0);

        // Add hours, minutes
        if(negative_sign)
        {
            calendar.add(Calendar.HOUR_OF_DAY, -hours_change);
            calendar.add(Calendar.MINUTE, -minutes_change);
        }
        else
        {
            calendar.add(Calendar.HOUR_OF_DAY, +hours_change);
            calendar.add(Calendar.MINUTE, +minutes_change);
        }

        return calendar;
    }

    public Calendar noTimeZoneConvert(String dateInFormat)
    {
        Calendar calendar = Calendar.getInstance();
        if(!dateInFormat.equals(""))
        {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy H:mm");
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

    public String timeZoneReverseConvert(Context context, Calendar calendar)
    {
        if(calendar!=null)
        {
            calendar = adjustLocalTimeZoneChanges(context, calendar);
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy H:mm", Locale.getDefault());
            Date date = calendar.getTime();
            return sdf.format(date);
        }
        return "error";
    }

    public String noTimeZoneReverseConvert(Calendar calendar)
    {
        if(calendar!=null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy H:mm");
            Date date = calendar.getTime();
            return sdf.format(date);
        }
        return "error";
    }

}
