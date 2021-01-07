package com.alexzamurca.animetrackersprint2.localList;

import android.content.Context;

import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.ArrayList;

public class UpdateAirDateChange
{
    private final int anilist_id;
    private final String air_date_change;
    private Context context;

    public UpdateAirDateChange(int anilist_id, String air_date_change, Context context) {
        this.anilist_id = anilist_id;
        this.air_date_change = air_date_change;
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
            // Update the air date change
            list.get(index).setAir_date_change(air_date_change);

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
