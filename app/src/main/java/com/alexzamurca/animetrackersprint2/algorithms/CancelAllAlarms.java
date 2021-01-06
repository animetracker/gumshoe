package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.util.Log;

import com.alexzamurca.animetrackersprint2.localList.LocalListStorage;
import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.ArrayList;
import java.util.List;

public class CancelAllAlarms
{
    private static final String TAG = "CancelAllAlarms";
    
    private Context context;

    public CancelAllAlarms(Context context) {
        this.context = context;
    }

    public void run()
    {
        getTableAndCancelAlarms();
    }

    private void getTableAndCancelAlarms()
    {
        LocalListStorage localListStorage = new LocalListStorage(context);
        ArrayList<Series> list = localListStorage.get();
        cancelAllAlarms(list);
    }

    private void cancelAllAlarms(List<Series> currentList)
    {
        NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(context);
        for(int i = 0; i < currentList.size(); i++)
        {
            notificationAiringChannel.cancel(currentList.get(i));
            Log.d(TAG, "cancelAllAlarms: cancelled alarm for " + currentList.get(i).getTitle());
        }
    }
}
