package com.alexzamurca.animetrackersprint2.algorithms;

import com.alexzamurca.animetrackersprint2.Date.ConvertDateToCalendar;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.Calendar;
import java.util.TimeZone;

// Get date after changes (air_date_changes, notification_change, timezone)
public class AdjustAirDate
{
    private static final String TAG = "AdjustAirDate";

    private Series series;

    public AdjustAirDate(Series series)
    {
        this.series = series;
    }

    public Calendar getCalendar()
    {
        boolean notificationsOn = series.getNotifications_on()==1;
        // If has an air date and notifications are not off
        if(!series.getAir_date().equals("") && notificationsOn)
        {
            ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
            Calendar calendar = convertDateToCalendar.timeZoneConvert(series.getAir_date());

            if(!hasAirDatePassed(calendar))
            {
                applyAirDateChange(calendar);

                applyNotificationChange(calendar);

                return calendar;
            }
        }

        return null;
    }

    private boolean hasAirDatePassed(Calendar airDateCalendar)
    {
        Calendar calendarNow = Calendar.getInstance(TimeZone.getDefault());
        return airDateCalendar.before(calendarNow);
    }

    private Calendar applyAirDateChange(Calendar calendar)
    {
        String air_date_change = series.getAir_date_change();
        // If series has a set air date change
        if(!air_date_change.equals(""))
        {
            // get sign, hours, minutes from air_date change
            String[] signHoursMinutesArray  = air_date_change.split(":");
            Character sign = air_date_change.toCharArray()[0];
            int hours = Integer.parseInt(signHoursMinutesArray[0].substring(1));
            int minutes = Integer.parseInt(signHoursMinutesArray[1]);

            if(sign.equals('+'))
            {
                calendar.add(Calendar.HOUR_OF_DAY, +hours);
                calendar.add(Calendar.MINUTE, +minutes);
            }
            else if(sign.equals('-'))
            {
                calendar.add(Calendar.HOUR_OF_DAY, -hours);
                calendar.add(Calendar.MINUTE, -minutes);
            }
        }
        return calendar;
    }

    private Calendar applyNotificationChange(Calendar calendar)
    {
        String notification_change = series.getNotification_change();
        // If series has a set notification change
        if(!notification_change.equals(""))
        {
            String[] quantityMetricBAArray  = notification_change.split(" ");
            int quantity = Integer.parseInt(quantityMetricBAArray[0]);
            String metric = quantityMetricBAArray[1];
            String beforeAfter = quantityMetricBAArray[2];


            // Add minutes, hours, days
            if(beforeAfter.equals("before"))
            {
                switch (metric)
                {
                    case "minutes":
                        calendar.add(Calendar.MINUTE, -quantity);
                        break;
                    case "hours":
                        calendar.add(Calendar.HOUR_OF_DAY, -quantity);
                        break;
                    case "days":
                        calendar.add(Calendar.DAY_OF_MONTH, -quantity);
                        break;
                }
            }
            else if(beforeAfter.equals("after"))
            {
                switch (metric)
                {
                    case "minutes":
                        calendar.add(Calendar.MINUTE, +quantity);
                        break;
                    case "hours":
                        calendar.add(Calendar.HOUR_OF_DAY, +quantity);
                        break;
                    case "days":
                        calendar.add(Calendar.DAY_OF_MONTH, +quantity);
                        break;
                }
            }
        }
        return calendar;
    }
}
