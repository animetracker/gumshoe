package com.alexzamurca.animetrackersprint2.localList;

import android.content.Context;

import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.ArrayList;

public class UpdateNotificationsOn
{
    private final int anilist_id;
    private final int notifications_on;
    private Context context;

    public UpdateNotificationsOn(int anilist_id, int notifications_on, Context context) {
        this.anilist_id = anilist_id;
        this.notifications_on = notifications_on;
        this.context = context;
    }

    public void update()
    {
        // Get list
        LocalListStorage localListStorage = new LocalListStorage(context);
        ArrayList<Series> list = localListStorage.get();

        // Find Series index in list
        int index = findSeriesIndex(list);

        if(index>=0)
        {
            // Remove Series from list object
            list.get(index).setNotifications_on(notifications_on);

            // Update local list with updated list object
            localListStorage.store(list);
        }
    }

    private int findSeriesIndex(ArrayList<Series> list)
    {
        for(int i = 0; i < list.size(); i++)
        {
            Series series = list.get(i);
            if(series.getAnilist_id() == anilist_id) return i;
        }
        return -1;
    }
}
