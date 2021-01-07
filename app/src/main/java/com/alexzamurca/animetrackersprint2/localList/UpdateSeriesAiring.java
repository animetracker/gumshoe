package com.alexzamurca.animetrackersprint2.localList;

import android.content.Context;

import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.util.ArrayList;

public class UpdateSeriesAiring
{
    private final int anilist_id;
    private final int next_episode_number;
    private final String air_date;
    private final String status;
    private Context context;

    public UpdateSeriesAiring(int anilist_id, int next_episode_number, String air_date, String status, Context context) {
        this.anilist_id = anilist_id;
        this.next_episode_number = next_episode_number;
        this.air_date = air_date;
        this.status = status;
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
            // Update
            list.get(index).setNext_episode_number(next_episode_number);
            list.get(index).setAir_date(air_date);
            list.get(index).setStatus(status);

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
