package com.alexzamurca.animetrackersprint2.series.series_list;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzamurca.animetrackersprint2.Date.ConvertDateToCalendar;
import com.alexzamurca.animetrackersprint2.ListFragment;
import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.series.Database.Remove;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

public class SeriesRecyclerViewAdapter extends RecyclerView.Adapter<SeriesRecyclerViewAdapter.ViewHolder> implements Filterable
{
    private static final String TAG = "SeriesRecyclerViewAdapter";

    private List<Series> list;
    private Context context;
    private OnSeriesListener onSeriesListener;
    private NavController navController;

    public SeriesRecyclerViewAdapter(Context context, List<Series> list, OnSeriesListener onSeriesListener, NavController navController)
    {
        this.list = list;
        this.context = context;
        this.onSeriesListener = onSeriesListener;
        this.navController = navController;
    }

    @NonNull
    @Override
    public SeriesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.series_row, parent, false);
        return new SeriesRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesRecyclerViewAdapter.ViewHolder holder, int position)
    {

        final String title = list.get(position).getTitle();
        String next_episode = "Episode:\n";
        int next_episode_number = list.get(position).getEpisode_number();
        if(next_episode_number < 0)
        {
            next_episode += "N/A";
        }
        else
        {
            next_episode += Integer.toString(next_episode_number);
        }

        String air_date = list.get(position).getAir_date();
        String cover_image = list.get(position).getCover_image();
        int notifications_on = list.get(position).getNotifications_on();
        String notification_change = list.get(position).getNotification_change();
        String air_date_change = list.get(position).getAir_date_change();

        // Setting the cover image
        Glide.with(context)
                .asBitmap()
                .load(cover_image)
                .into(holder.image);

        // Setting the text views
        holder.title.setText(title);
        holder.next_episode.setText(next_episode);
        showAirDate(holder, air_date, air_date_change);
        sortNotificationStateColours(holder, notifications_on);
        sortNotificationReminderAndErrorColours(holder, notification_change, air_date_change);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Series> filteredList = new ArrayList<>();

            if(constraint.toString().isEmpty())
            {
                filteredList.addAll(list);
            }
            else
            {
                for(Series series:list)
                {
                    if(series.getTitle().toLowerCase().contains(constraint.toString().toLowerCase()))
                    {
                        filteredList.add(series);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            list.clear();
            list.addAll((Collection<? extends Series>) results.values);
            notifyDataSetChanged();
        }
    };

    public List<Series> getList()
    {
        return list;
    }

    public void restoreFromList(List<Series> oldList)
    {
        list = oldList;
        notifyDataSetChanged();
    }

    private void sortNotificationStateColours(SeriesRecyclerViewAdapter.ViewHolder holder, int notifications_on)
    {
        if(notifications_on==1)
        {
            holder.notifications_off.setImageResource(R.drawable.ic_notifications_on);
            holder.notifications_off.setTag("notifications_on");
        }
        else if(notifications_on==0)
        {
            holder.notifications_off.setImageResource(R.drawable.ic_notifications_off_blue);
            holder.notifications_off.setTag("notifications_off");
        }
    }

    private void sortNotificationReminderAndErrorColours(SeriesRecyclerViewAdapter.ViewHolder holder, String notification_change, String air_date_change)
    {
        if(!notification_change.equals(""))
        {
            holder.change_notification_time.setImageResource(R.drawable.ic_timer_sand_blue);
        }
        if(!air_date_change.equals(""))
        {
            holder.error_wrong_air_date.setImageResource(R.drawable.ic_error_blue);
        }
    }

    private void showAirDate(SeriesRecyclerViewAdapter.ViewHolder holder, String air_date, String air_date_change)
    {
        String newAirDate;
        if(!air_date.equals(""))
        {
            ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
            Calendar calendar = convertDateToCalendar.timeZoneConvert(air_date);
            if(calendar != null)
            {
                calendar.setTimeZone(TimeZone.getDefault());

                // Account for the fact that there may not be any change
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

                newAirDate = convertDateToCalendar.reverseConvert(calendar);
            }
            else
            {
                newAirDate = "Unknown Date";
            }
        }
        else
        {
            newAirDate = "Unknown Date";
        }

        newAirDate = "Releasing:\n" + newAirDate;

        holder.air_date.setText(newAirDate);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView title;
        TextView next_episode;
        TextView air_date;
        ImageView notifications_off;
        ImageView remove;
        ImageView change_notification_time;
        ImageView error_wrong_air_date;

        public ViewHolder(View itemView)
        {
            super(itemView);
            image = itemView.findViewById(R.id.coverImage_series);
            title = itemView.findViewById(R.id.title_series);
            next_episode = itemView.findViewById(R.id.nextEpisode_series);
            air_date = itemView.findViewById(R.id.airDate_series);
            notifications_off = itemView.findViewById(R.id.notification_off_series);
            remove = itemView.findViewById(R.id.remove_series);
            change_notification_time = itemView.findViewById(R.id.change_notification_reminder_series);
            error_wrong_air_date = itemView.findViewById(R.id.error_wrong_air_date_series);

            itemView.setOnClickListener(v ->
                    {
                        ListFragment.isAppRunning = true;
                        Log.d(TAG, "ViewHolder: onSeriesClick");
                        onSeriesListener.onSeriesClick(list.get(getAdapterPosition()));
                    }
            );

            notifications_off.setOnClickListener(v ->
                    {
                        ListFragment.isAppRunning = true;
                        if(notifications_off.getTag()=="notifications_off")
                        {
                            Log.d(TAG, "ViewHolder: onNotificationsOn");
                            onSeriesListener.onNotificationsOn(list.get(getAdapterPosition()));
                        }
                        else if(notifications_off.getTag()=="notifications_on")
                        {
                            onSeriesListener.onNotificationsOff(list.get(getAdapterPosition()));
                        }
                    }
            );

            remove.setOnClickListener(v ->
                    {
                        ListFragment.isAppRunning = true;
                        removeSeries(list.get(getAdapterPosition()));
                    }
            );

            change_notification_time.setOnClickListener(v ->
                    {
                        ListFragment.isAppRunning = true;
                        Log.d(TAG, "ViewHolder: onChangeNotificationTime");
                        onSeriesListener.onChangeNotificationTime(list.get(getAdapterPosition()));
                    }
            );

            error_wrong_air_date.setOnClickListener(v ->
                    {
                        ListFragment.isAppRunning = true;
                        Log.d(TAG, "ViewHolder: onErrorWrongAirDate");
                        onSeriesListener.onErrorWrongAirDate(list.get(getAdapterPosition()));
                    }
            );
        }
    }

    public void removeSeries(Series series)
    {
        RemoveAsync removeAsync = new RemoveAsync();
        removeAsync.setSelectedSeries(series);
        removeAsync.execute();

        refreshSeriesList();
    }

    private void refreshSeriesList()
    {
        navController.navigate(R.id.listFragment);
    }


    public interface OnSeriesListener
    {
        void onSeriesClick(Series series);
        void onNotificationsOff(Series series);
        void onNotificationsOn(Series series);
        void onChangeNotificationTime(Series series);
        void onErrorWrongAirDate(Series series);
    }

    private class RemoveAsync extends AsyncTask<Void, Void, Void>
    {
        private boolean isSeriesRemoved;
        private Series selectedSeries;
        
        public void setSelectedSeries(Series selectedSeries)
        {
            this.selectedSeries = selectedSeries;
        }
        
        @Override
        protected Void doInBackground(Void... voids)
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
            String session = sharedPreferences.getString("session", "");
            Remove remove = new Remove(session, selectedSeries.getAnilist_id());
            isSeriesRemoved = remove.remove();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) 
        {
            String title = selectedSeries.getTitle();
            if(isSeriesRemoved)
            {
                Toast.makeText(context, "\"" + title +"\" is no longer in your series list.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "Failed to remove \"" + title +"\", it is still in your series list.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aVoid);
        }
    }
    
}
