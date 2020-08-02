package com.alexzamurca.animetrackersprint2.series.Date;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ConvertMillisToDate
{
    private final int date_in_epoch_seconds;

    public ConvertMillisToDate(int date_in_epoch_seconds)
    {
        this.date_in_epoch_seconds = date_in_epoch_seconds;
    }

    public String getDate()
    {
        if(date_in_epoch_seconds!=-1)
        {
            LocalDateTime dateTime = LocalDateTime.ofEpochSecond(date_in_epoch_seconds, 0, ZoneOffset.UTC);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy H:mm", Locale.ENGLISH);
            return dateTime.format(formatter);
        }
        return "";
    }
}
