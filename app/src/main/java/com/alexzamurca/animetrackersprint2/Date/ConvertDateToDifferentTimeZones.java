package com.alexzamurca.animetrackersprint2.Date;

import java.util.HashMap;

public class ConvertDateToDifferentTimeZones
{
    private final String source_timezone;
    private final String destination_timezone;

    HashMap<String, Double> zoneToChangeInUST;

    public ConvertDateToDifferentTimeZones(String source_timezone, String destination_timezone)
    {
        this.source_timezone = source_timezone;
        this.destination_timezone = destination_timezone;

        initHashMap();
    }

    private void initHashMap()
    {
        zoneToChangeInUST = new HashMap<>();
        // Add time zones and their GMT difference
        zoneToChangeInUST.put("JST", 9.0);
        zoneToChangeInUST.put("UTC", 0.0);
    }

    private int[] changeToDestinationTimeZone(int hour, int minutes)
    {
        int[] storage = new int[]{hour,minutes};

        double source_change = zoneToChangeInUST.get(source_timezone);
        double destination_change = zoneToChangeInUST.get(destination_timezone);

        double difference = destination_change - source_change;
        double minutes_indication = (int) difference - difference;

        // MINUTES WORK
        minutes+=(int)minutes_indication*60;
        if(minutes >= 60)
        {
            hour++;
            minutes = 60 - minutes;
        }

        // HOUR WORK
        if(destination_change > source_change)
        {
            // We know we gotta add
            hour += destination_change - source_change;
        }
        else if(destination_change < source_change)
        {
            // We know we gotta deduct
            hour += destination_change - source_change;
        }

        storage[0] = hour;
        storage[1] = minutes;
        return storage;
    }

    private boolean isHourSingleDigit(String hour)
    {
        int length1 = hour.length();
        // do this to be robust against single number hours
        hour= hour.replace(" ", "");
        int length2 = hour.length();
        return (length1 == length2);
    }

    public String getConvertedDate(String source_date)
    {
        boolean isSingleNumberHour = false;
        // Get hour of date
        String hour_str = source_date.substring(source_date.indexOf(" ")+1, source_date.indexOf(":"));
        int minutes = Integer.parseInt(source_date.substring(source_date.indexOf(":")+1));
        int hour = Integer.parseInt(hour_str);

        // BIG PROBLEM: ALGORITHM DOES NOT INTERPRET NEXT OR PREVIOUS DAYS
        // ANOTHER PROBLEM: ALGORITHM DOESNT KNOW IF TO GO FORWARD OR BACKWARDS DEPENDING ON TIME ZONE DIFFERENCE
        hour = changeToDestinationTimeZone(hour, minutes)[0];
        minutes = changeToDestinationTimeZone(hour, minutes)[1];

        // Dumb thing i have to do as a result of using single digit hour
        String before_space = source_date.substring(0,source_date.indexOf(":")-2);
        String after_space = hour + ":";
        if(minutes < 10) after_space += "0";
        after_space += minutes;
        if(isHourSingleDigit(hour_str))
        {
            return before_space + " " + after_space;
        }
        else
        {
            return before_space + after_space;
        }
    }
}
