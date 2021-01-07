package com.alexzamurca.animetrackersprint2.algorithms;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.alexzamurca.animetrackersprint2.localList.Remove;
import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

public class RemoveSeries
{
    private static final String TAG = "RemoveSeries";
    private Context context;

    public RemoveSeries(Context context) {
        this.context = context;
    }

    public void remove(Series series)
    {
        Remove remove = new Remove(series.getAnilist_id(), context);
        remove.remove();

        Toast.makeText(context, "\"" + series.getTitle() +"\" is no longer in your series list.", Toast.LENGTH_SHORT).show();
        // Cancel alarm
        NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(context);
        notificationAiringChannel.cancel(series);
    }

}
