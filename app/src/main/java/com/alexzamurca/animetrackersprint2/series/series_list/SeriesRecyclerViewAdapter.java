package com.alexzamurca.animetrackersprint2.series.series_list;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzamurca.animetrackersprint2.ListFragment;
import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.series.Database.Remove;
import com.bumptech.glide.Glide;

import java.util.List;

public class SeriesRecyclerViewAdapter extends RecyclerView.Adapter<SeriesRecyclerViewAdapter.ViewHolder>
{
    private static final String TAG = "SeriesRecyclerViewAdapter";

    private List<Series> list;
    private Context context;
    private OnSeriesListener onSeriesListener;
    private FragmentManager fragmentManager;

    public SeriesRecyclerViewAdapter(Context context, List<Series> list, OnSeriesListener onSeriesListener, FragmentManager fragmentManager)
    {
        this.list = list;
        this.context = context;
        this.onSeriesListener = onSeriesListener;
        this.fragmentManager = fragmentManager;
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


        String air_date = "Coming out on:\n" + list.get(position).getAir_date();
        String cover_image = list.get(position).getCover_image();

        // Setting the image
        Glide.with(context)
                .asBitmap()
                .load(cover_image)
                .into(holder.image);

        // Setting the text views
        holder.title.setText(title);
        holder.next_episode.setText(next_episode);
        holder.air_date.setText(air_date);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView title;
        TextView next_episode;
        TextView air_date;
        ImageView more_info;

        public ViewHolder(View itemView)
        {
            super(itemView);
            image = itemView.findViewById(R.id.coverImage_series);
            title = itemView.findViewById(R.id.title_series);
            next_episode = itemView.findViewById(R.id.nextEpisode_series);
            air_date = itemView.findViewById(R.id.airDate_series);
            more_info = itemView.findViewById(R.id.series_more_info_image);

            itemView.setOnClickListener(v ->
                    onSeriesListener.onSeriesClick(list.get(getAdapterPosition()))
            );

            more_info.setOnClickListener(v ->
            {
                Toast.makeText(context, "You want more info for \""+ list.get(getAdapterPosition()).getTitle() +"\"!", Toast.LENGTH_SHORT).show();

                PopupMenu popup = new PopupMenu(context, v);

                popup.getMenuInflater().inflate(R.menu.series_more_info_dropdown, popup.getMenu());

                setupDropDownOnClick(popup, list.get(getAdapterPosition()));

                popup.show();
            });
        }
    }

    private void refreshSeriesList()
    {
        fragmentManager.beginTransaction().replace(R.id.fragment_container, new ListFragment(), "ListFragment").commit();
    }

    private void setupDropDownOnClick(PopupMenu popup, Series selectedSeries)
    {
        String title = selectedSeries.title;
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getTitle().toString())
            {
                case "Change Color":
                    Toast.makeText(context, "You want to change color of \"" + title +"\"", Toast.LENGTH_SHORT).show();
                    break;

                case "Change Alert Delay":
                    Toast.makeText(context, "You want to change alert delay of \"" + title +"\"", Toast.LENGTH_SHORT).show();
                    break;

                case "Hide":
                    Toast.makeText(context, "You want to hide \"" + title +"\"", Toast.LENGTH_SHORT).show();
                    break;

                case "Remove":
                    RemoveAsync removeAsync = new RemoveAsync();
                    removeAsync.setSelectedSeries(selectedSeries);
                    removeAsync.execute();
                    break;
            }
            refreshSeriesList();
            return true;
        });
    }


    public interface OnSeriesListener
    {
        void onSeriesClick(Series series);
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
