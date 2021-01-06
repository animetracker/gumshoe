package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.localList.LocalListStorage;
import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SetAlarmsForList
{
    private static final String TAG = "SetAlarmsForList";

    private Context context;

    public SetAlarmsForList(Context context) {
        this.context = context;
    }

    public void run()
    {
        getTableAndSetAlarms();
    }

    private void getTableAndSetAlarms()
    {
        LocalListStorage localListStorage = new LocalListStorage(context);
        ArrayList<Series> list = localListStorage.get();
        setAllAlarms(list);
    }

    private void setAllAlarms(List<Series> currentList)
    {
        NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(context);
        for(int i = 0; i < currentList.size(); i++)
        {
            Series selectedSeries = currentList.get(i);

            AdjustAirDate adjustAirDate = new AdjustAirDate(selectedSeries);
            Calendar calendar = adjustAirDate.getCalendar();

            if(calendar!=null)
            {
                Log.d(TAG, "setAllAlarms: set notification for " + selectedSeries.getAir_date());
                notificationAiringChannel.setNotification(selectedSeries, calendar);
            }
        }
    }
}
