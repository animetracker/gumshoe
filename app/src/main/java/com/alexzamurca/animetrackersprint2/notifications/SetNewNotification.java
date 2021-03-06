package com.alexzamurca.animetrackersprint2.notifications;

import android.content.Context;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.Date.ConvertDateToCalendar;

import com.alexzamurca.animetrackersprint2.localList.LocalListStorage;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;
import java.util.Calendar;
import java.util.List;

public class SetNewNotification
{
    private static final String TAG = "SetNewNotification";

    private Context context;
    private Series series;
    private Series newSeries;
    private Calendar airDateAfterChangesCalendar = null;

    private List<Series> list;

    public SetNewNotification(Context context, Series series)
    {
        this.context = context;
        this.series = series;
    }

    private void constructCalendar()
    {
        newSeries = findSeriesInList();
        if(newSeries!=null)airDateAfterChangesCalendar =  adjustAirDate(newSeries);
        else
        {
            Log.d(TAG, "constructCalendar: haven't found series " + series.getTitle() + " in series list");
        }
    }

    private void setList()
    {
        LocalListStorage localListStorage = new LocalListStorage(context);
        list = localListStorage.get();

        constructCalendar();
        if(airDateAfterChangesCalendar != null && newSeries!=null)
        {
            Log.d(TAG, "onPostExecute: calendar, listener and newSeries are not null, so setting new notification for \"" + newSeries.getTitle() + "\"");
            NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(context);
            notificationAiringChannel.setNotification(newSeries, airDateAfterChangesCalendar);
        }
        else
        {
            Log.d(TAG, "setList: airDateChangesCalendar is null");
        }
    }

    private Series findSeriesInList()
    {
        for(int i = 0; i < list.size(); i++)
        {
            if( series.getAnilist_id() == list.get(i).getAnilist_id() )
            {
                return list.get(i);
            }
        }
        return null;
    }

    public void setNotification()
    {
        setList();
    }

    private Calendar adjustAirDate(Series series)
    {
        String air_date = series.getAir_date();
        boolean notificationsOn = series.getNotifications_on()==1;
        // If has an air date and notifications are not off
        if(!air_date.equals("") && notificationsOn)
        {
            ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
            Calendar calendar = convertDateToCalendar.convert(air_date);
            Log.d(TAG, "adjustAirDate: calendar before changes for " + series.getTitle() + " is " + convertDateToCalendar.reverseConvert(calendar));

            String air_date_change = series.getAir_date_change();
            String notification_change = series.getNotification_change();

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
            Log.d(TAG, "adjustAirDate: calendar after changes for " + series.getTitle() + " is " + convertDateToCalendar.reverseConvert(calendar));
            return calendar;

        }
        return null;
    }
}
