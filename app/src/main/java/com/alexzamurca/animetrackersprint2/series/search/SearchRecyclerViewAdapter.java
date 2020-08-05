package com.alexzamurca.animetrackersprint2.series.search;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzamurca.animetrackersprint2.series.AniList.Search;
import com.alexzamurca.animetrackersprint2.series.Database.Insert;
import com.alexzamurca.animetrackersprint2.ListFragment;
import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.series.dialog.CheckConnection;
import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.util.List;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>
{
    private static final String TAG = "RecyclerViewAdapter";

    private Search search;
    private List<SearchResult> list;
    private String series_name;
    private Context context;
    private RowClickListener rowClickListener;
    private TextView noSearchResultsTV;
    private View searchActivityView;
    public String title_content;
    private NavController navController;

    public SearchRecyclerViewAdapter(Context context, List<SearchResult> list, RowClickListener rowClickListener, TextView noSearchResultsTV, View searchActivityView, NavController navController)
    {
        this.list = list;
        this.context = context;
        this.rowClickListener  = rowClickListener;
        this.noSearchResultsTV = noSearchResultsTV;
        this.searchActivityView = searchActivityView;
        this.navController = navController;
    }

    @NonNull
    @Override
    public SearchRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        final String title = list.get(position).getTitle();
        String rating = list.get(position).getRating();
        String description = list.get(position).getDescription();
        String next_episode = list.get(position).getNext_episode();
        String status = list.get(position).getStatus();
        String image_directory = list.get(position).getImage_directory();

        // Setting the image
        Glide.with(context)
                .asBitmap()
                .load(image_directory)
                .into(holder.image);

        // Setting the text views
        holder.title.setText(title);
        holder.rating.setText(rating);
        holder.description.setText(description);
        holder.next_episode.setText(next_episode);
        holder.status.setText(status);
    }

    public interface RowClickListener
    {
        void onFailedClick();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;
        TextView title;
        TextView rating;
        TextView description;
        TextView next_episode;
        TextView status;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            rating = itemView.findViewById(R.id.rating);
            description = itemView.findViewById(R.id.description);
            next_episode = itemView.findViewById(R.id.nextEpisode);
            status = itemView.findViewById(R.id.status);



            itemView.setOnClickListener(v -> {

                title_content = list.get(getAdapterPosition()).getTitle();
                Log.d(TAG, "onClick: clicked on: " + title_content);

                CheckConnection checkConnection = new CheckConnection(context);
                boolean isConnectedToInternet = checkConnection.isConnected();
                if (isConnectedToInternet)
                {
                    insert(getAdapterPosition());

                    Log.d(TAG, "ViewHolder: selected result is stored");

                    navController.navigate(R.id.action_selected_search_result);
                    /*
                    //Go back to previous fragment
                    FragmentTransaction tr = fragmentManager.beginTransaction();
                    tr.replace(R.id.fragment_container, new ListFragment());
                    tr.commit();
                     */
                }
                else
                {
                    Log.d(TAG, "ViewHolder: NO INTERNET");
                    rowClickListener.onFailedClick();
                    Toast.makeText(context, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void searchName(String series_name)
    {
        this.series_name = series_name;
        Connection connection = new Connection();
        connection.execute();
    }

    public void insert(int position)
    {
        DatabaseInsert databaseInsert = new DatabaseInsert();
        databaseInsert.setAdapter_position(position);
        databaseInsert.execute();
    }


    public void hideKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchActivityView.getWindowToken(), 0);
    }


    // Network activity is done in the background
    public class Connection extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {
            search = new Search(series_name);
            search.printList(list);
            list = search.getSearchResults();
            Log.d(TAG, "doInBackground: Changed List");
            search.printList(list);
            noSearchResultsTV.setText("");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
            Log.d("OnPostExecute", "Data set is changed");
            if(list.size() == 0)
            {
                noSearchResultsTV.setText("No search results for\"" + series_name + "\"");
            }
            else
            {
                hideKeyboard();
            }
        }
    }

    // Network activity is done in the background
    public class DatabaseInsert extends AsyncTask<Void, Void, Void> {

        private int adapter_position;
        private int request_success_rating;

        public void setAdapter_position(int adapter_position)
        {
            this.adapter_position = adapter_position;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //HARD CODED USER ID
                Insert insert = new Insert(0, search.getSearchArray().getJSONObject(adapter_position));
                Log.d(TAG, "doInBackground: search ARRAY: \n\n\n" + search.getSearchArray().getJSONObject(adapter_position).toString(4) + "\n\n\n");
                request_success_rating = insert.insert();
            } catch (JSONException e) {
                Log.d(TAG, "DatabaseInsert: doInBackground: JSONException");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(request_success_rating == 0)
            {
                Toast.makeText(context, "\"" + title_content + "\" is now in your series list!", Toast.LENGTH_SHORT).show();
            }
            if(request_success_rating == 1)
            {
                Toast.makeText(context, "\"" + title_content + "\" is already in your series list!", Toast.LENGTH_SHORT).show();
            }
            if(request_success_rating == 2)
            {
                Toast.makeText(context, "\"" + title_content + "\" failed to be added your series list!", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aVoid);
        }
    }
}
