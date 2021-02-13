package com.alexzamurca.animetrackersprint2.series.add_series;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzamurca.animetrackersprint2.Date.ConvertDateToCalendar;
import com.alexzamurca.animetrackersprint2.dialog.NoConnectionDialog;
import com.alexzamurca.animetrackersprint2.localList.Insert;
import com.alexzamurca.animetrackersprint2.series.AniList.Search;
import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.series.JSON.SearchResponseToString;
import com.alexzamurca.animetrackersprint2.algorithms.CheckConnection;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class AddRecyclerViewAdapter extends RecyclerView.Adapter<AddRecyclerViewAdapter.ViewHolder>
{
    private static final String TAG = "AddRecyclerViewAdapter";

    private Search search;
    private List<SearchResult> list;
    private String series_name;
    private String session;
    private Context context;
    private RowClickListener rowClickListener;
    private TextView noSearchResultsTV;
    public String title_content;
    private ProgressBar progressBar;

    public AddRecyclerViewAdapter(List<SearchResult> list, Context context, RowClickListener rowClickListener, TextView noSearchResultsTV, ProgressBar progressBar) {
        this.list = list;
        this.context = context;
        this.rowClickListener = rowClickListener;
        this.noSearchResultsTV = noSearchResultsTV;
        this.progressBar = progressBar;
        SharedPreferences sharedPreferences = context.getSharedPreferences("Account", Context.MODE_PRIVATE);
        session = sharedPreferences.getString("session", "");
        Log.d(TAG, "AddRecyclerViewAdapter: session: " + session);
    }

    @NonNull
    @Override
    public AddRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        final String title = list.get(position).getTitle();
        String rating = "<b> Average Rating (out of 100): </b>" + list.get(position).getRating();
        String air_date = "<b> Airs on: </b>" + airDateConvert(list.get(position).getAir_date()) + "<br>" + "<i> *Subject to user location and time zone changes </i>";
        String next_episode_number = "<b> Next Episode Number: </b>" + list.get(position).getNext_episode_number();
        String romaji = "<b> Japanese Name: </b>" + list.get(position).getRomaji();
        String description = "<b> Description: </b> <br>" + list.get(position).getDescription();
        String adult_rating = "<b> Adult Series?: </b>" + list.get(position).getIsAdult();
        String start_date = "<b> Release Date: </b>" + list.get(position).getStart_date();
        String active_users = "<b> Popularity: </b>" + list.get(position).getActive_users();
        String trailer_URL = list.get(position).getTrailer_URL();
        String APIStatus = list.get(position).getStatus();
        String status = APIStatus.equals("RELEASING") ? "Airing" : "Not Yet Released";
        String image_directory = list.get(position).getImage_directory();

        // Setting the image
        Glide.with(context)
                .asBitmap()
                .load(image_directory)
                .into(holder.image);

        // Setting the text views

        holder.title.setText(title);
        Rect bounds = new Rect();
        Paint textPaint = holder.title.getPaint();
        textPaint.getTextBounds(title, 0, title.length(), bounds);
        int width = bounds.width();
        Log.d(TAG, "onBindViewHolder: " + title + " width" + width);

        // 1127 = how much text can fit on the screen with 30sp
        // 2400 = how much text can fit on the screen with 25sp
        if(width>1000 && width<=1700)
        {
            holder.title.setTextSize(25);
        }
        else if(width>1700 && width<= 2400)
        {
            holder.title.setTextSize(20);
        }
        else if(width>2400)
        {
            holder.title.setTextSize(15);
        }

        holder.average.setText(HtmlCompat.fromHtml(rating, HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.air_date.setText(HtmlCompat.fromHtml(air_date, HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.next_episode_number.setText(HtmlCompat.fromHtml(next_episode_number, HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.romaji.setText(HtmlCompat.fromHtml(romaji, HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.description.setText(HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.adult_rating.setText(HtmlCompat.fromHtml(adult_rating, HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.start_date.setText(HtmlCompat.fromHtml(start_date, HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.active_watchers.setText(HtmlCompat.fromHtml(active_users, HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.status.setText(status);
        holder.expandableLayout.setVisibility(View.GONE);

        holder.show_trailer.setOnClickListener(v ->
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(trailer_URL));
            context.startActivity(intent);
        });
    }

    public interface RowClickListener
    {
        void onSuccessfulClick(Series series);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        ImageView expand_collapse;
        TextView title;
        TextView air_date;
        TextView next_episode_number;
        TextView romaji;
        TextView average;
        TextView description;
        TextView start_date;
        TextView active_watchers;
        Button show_trailer;
        TextView adult_rating;
        TextView status;

        LinearLayout expandableLayout;
        LinearLayout moreInfoLayout;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            image = itemView.findViewById(R.id.search_row_cover_image);
            title = itemView.findViewById(R.id.search_row_title);
            expand_collapse = itemView.findViewById(R.id.search_row_expand_collapse);
            air_date = itemView.findViewById(R.id.search_row_air_date);
            next_episode_number = itemView.findViewById(R.id.search_row_next_episode_number);
            romaji = itemView.findViewById(R.id.search_row_romaji);
            average = itemView.findViewById(R.id.search_row_average);
            description = itemView.findViewById(R.id.search_row_description);
            start_date = itemView.findViewById(R.id.search_row_start_date);
            adult_rating = itemView.findViewById(R.id.search_row_adult_rating);
            active_watchers = itemView.findViewById(R.id.search_row_active_users);
            show_trailer = itemView.findViewById(R.id.search_row_trailer_button);
            status = itemView.findViewById(R.id.search_row_status);

            expandableLayout = itemView.findViewById(R.id.series_row_expandable_layout);
            moreInfoLayout = itemView.findViewById(R.id.series_row_expandable_possibly_null);
            
            expand_collapse.setOnClickListener(v -> 
            {

                if(expandableLayout.getTag().equals("notShowing"))
                {
                    expandableLayout.setVisibility(View.VISIBLE);
                    expandableLayout.setTag("showing");

                    //check status to determine whether to show more info or not
                    if(status.getText().equals("Airing"))
                    {
                        moreInfoLayout.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        moreInfoLayout.setVisibility(View.GONE);
                    }

                    expand_collapse.setImageResource(R.drawable.ic_arrow_up);
                }
                else if(expandableLayout.getTag().equals("showing"))
                {
                    expandableLayout.setVisibility(View.GONE);
                    expandableLayout.setTag("notShowing");
                    expand_collapse.setImageResource(R.drawable.ic_arrow_down);
                }
            });

            itemView.setOnClickListener(v -> 
            {

                title_content = list.get(getAdapterPosition()).getTitle();
                Log.d(TAG, "onClick: clicked on: " + title_content);

                insert(getAdapterPosition());
                Log.d(TAG, "ViewHolder: selected result is stored");

            });
        }
    }

    public void searchName(String series_name)
    {
        this.series_name = series_name;
        CheckConnection checkConnection = new CheckConnection(context);
        boolean isConnectedToInternet = checkConnection.isConnected();
        if (isConnectedToInternet)
        {
            progressBar.setVisibility(View.VISIBLE);
            noSearchResultsTV.setVisibility(View.GONE);
            AniListSearch aniListSearch = new AniListSearch();
            aniListSearch.execute();
            Log.d(TAG, "anilist search has connection");
        }
        else
        {
            Log.d(TAG, "anilist: NO INTERNET");

            NoConnectionDialog noConnectionDialog = new NoConnectionDialog();
            noConnectionDialog.show(((FragmentActivity)context).getSupportFragmentManager() , "NoConnectionDialog");
        }

    }

    public void insert(int position)
    {
        try
        {
            JSONObject json = search.getSearchArray().getJSONObject(position);
            Insert insert = new Insert(json, context);
            int request_success_rating = insert.insert();
            if(request_success_rating==0)
            {
                Toast.makeText(context, "\"" + title_content + "\" is now in your series list!", Toast.LENGTH_LONG).show();
            }
            else if(request_success_rating == 1)
            {
                Toast.makeText(context, "\"" + title_content + "\" is already in your series list!", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(context, "\"" + title_content + "\" failed to be added your series list!", Toast.LENGTH_LONG).show();
            }

            SearchResponseToString searchResponseToString = new SearchResponseToString();
            rowClickListener.onSuccessfulClick(searchResponseToString.getSeries(json));
        }
        catch(JSONException e)
        {
            Log.d(TAG, "insert: failed to get search array element");
        }
    }



    String airDateConvert(String air_date)
    {
        Log.d(TAG, "airDateConvert: airDate:" + air_date);
        if(!air_date.equals(""))
        {
            ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
            Calendar calendar = convertDateToCalendar.convert(air_date);
            String[] days = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
            String dayOfWeek = days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
            int minute = calendar.get(Calendar.MINUTE);
            String timeOfDay = calendar.get(Calendar.HOUR) + ":" + ((minute<10)?"0":"") + minute + ((calendar.get(Calendar.AM)==1)? "am":"pm");
            String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + ((minute<10)?"0":"") + minute;
            return dayOfWeek + "s at " + timeOfDay + " (or in 24-hour-time: "+ time + ")";
        }
        return "Unknown";
    }

    // Network activity is done in the background
    public class AniListSearch extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            search = new Search(series_name, context);
            search.printList(list);
            list = search.getSearchResults();
            search.printList(list);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            notifyDataSetChanged();
            if(list.size() == 0)
            {
                noSearchResultsTV.setVisibility(View.VISIBLE);
                String text = "No search results for\"" + series_name + "\"";
                noSearchResultsTV.setText(text);
            }
            progressBar.setVisibility(View.GONE);
        }
    }
}
