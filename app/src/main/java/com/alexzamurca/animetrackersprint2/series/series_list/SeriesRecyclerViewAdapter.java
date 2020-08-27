package com.alexzamurca.animetrackersprint2.series.series_list;

import android.content.Context;
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

import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.series.Database.Remove;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        Log.d(TAG, "SeriesRecyclerViewAdapter onBindViewHolder: called.");

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

        String air_date = "Releasing:\n" + list.get(position).getAir_date();
        String cover_image = list.get(position).getCover_image();
        int notifications_on = list.get(position).getNotifications_on();
        String notification_change = list.get(position).getNotification_change();
        String air_date_change = list.get(position).getAir_date_change();

        // Setting the image
        Glide.with(context)
                .asBitmap()
                .load(cover_image)
                .into(holder.image);

        // Setting the text views
        holder.title.setText(title);
        holder.next_episode.setText(next_episode);
        holder.air_date.setText(air_date);
        if(notifications_on==1)
        {
            holder.notifications_off.setImageResource(R.drawable.ic_notifications_on);
            holder.notifications_off.setTag("notifications_on");
            Log.d(TAG, "onBindViewHolder: " + title + " has notifications on");
        }
        else if(notifications_on==0)
        {
            holder.notifications_off.setImageResource(R.drawable.ic_notifications_off_blue);
            holder.notifications_off.setTag("notifications_off");
            Log.d(TAG, "onBindViewHolder: " + title + "has notifications off");
        }
        if(!notification_change.equals(""))
        {
            holder.change_notification_time.setImageResource(R.drawable.ic_timer_sand_blue);
        }
        if(!air_date_change.equals(""))
        {
            holder.error_wrong_air_date.setImageResource(R.drawable.ic_error_blue);
        }

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

    public void printList(List<Series> list)
    {
        Log.d(TAG, "printList: printing");
        for(Series sr:list)
        {
            Log.d(TAG, "||" + sr.getCover_image() + "|" + sr.getTitle() + "|" + sr.getAir_date() + "|" + sr.getEpisode_number() + "||");
        }
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
                    onSeriesListener.onSeriesClick(list.get(getAdapterPosition()))
            );

            notifications_off.setOnClickListener(v ->
                    {
                        Log.d(TAG, "ViewHolder: notifications_off clicked");
                        if(notifications_off.getTag()=="notifications_off")
                        {
                            onSeriesListener.onNotificationsOn(list.get(getAdapterPosition()));
                        }
                        else if(notifications_off.getTag()=="notifications_on")
                        {
                            onSeriesListener.onNotificationsOff(list.get(getAdapterPosition()));
                        }
                    }

            );

            remove.setOnClickListener(v -> {
                Log.d(TAG, "ViewHolder: remove clicked");

                RemoveAsync removeAsync = new RemoveAsync();
                removeAsync.setSelectedSeries(list.get(getAdapterPosition()));
                removeAsync.execute();

                refreshSeriesList();
            });

            change_notification_time.setOnClickListener(v ->
                {
                    Log.d(TAG, "ViewHolder: change_notification_time clicked");
                    onSeriesListener.onChangeNotificationTime(list.get(getAdapterPosition()));
                }
            );

            error_wrong_air_date.setOnClickListener(v ->
            {
                Log.d(TAG, "ViewHolder: error_wrong_air_date clicked");
                onSeriesListener.onErrorWrongAirDate(list.get(getAdapterPosition()));
            });
        }
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
            Remove remove = new Remove(0, selectedSeries.getAnilist_id());
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
