package com.alexzamurca.animetrackersprint2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzamurca.animetrackersprint2.series.Database.SelectTable;
import com.alexzamurca.animetrackersprint2.series.dialog.CheckConnection;
import com.alexzamurca.animetrackersprint2.series.dialog.NoConnectionDialog;
import com.alexzamurca.animetrackersprint2.series.dialog.NoDatabaseDialog;
import com.alexzamurca.animetrackersprint2.series.search.SearchFragment;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;
import com.alexzamurca.animetrackersprint2.series.series_list.SeriesInfoFragment;
import com.alexzamurca.animetrackersprint2.series.series_list.SeriesRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment implements NoConnectionDialog.TryAgainListener, SeriesRecyclerViewAdapter.OnSeriesListener {
    private static final String TAG = "ListFragment";
    private ArrayList<Series> list = new ArrayList<>();
    private SeriesRecyclerViewAdapter adapter;
    private TextView emptyListTV;
    private ImageView emptyListImage;
    private LinearLayout emptyListLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_series_list, container, false);

        Log.d(TAG, "onCreate: starting");

        CheckConnection checkConnection = new CheckConnection(getContext());
        boolean isConnected = checkConnection.isConnected();
        if (isConnected)
        {
            initList();
        }
        else
        {
            newDialogInstance();
            Toast.makeText(getContext(), "Cannot connect to the internet, check internet connection!", Toast.LENGTH_SHORT).show();
        }

        Button searchButton = view.findViewById(R.id.series_search_button);
        emptyListTV = view.findViewById(R.id.series_empty_list);
        emptyListImage = view.findViewById(R.id.series_empty_list_image);
        emptyListLayout = view.findViewById(R.id.series_empty_list_linear_layout);

        initImageBitmaps();


        // Search button
        searchButton.setOnClickListener(v ->
        {
            Log.d(TAG, "onClick: Clicked search_button");

            changeToSearchFragment();
        });
        return view;
    }

    private void changeToSearchFragment()
    {
        SearchFragment searchFragment = new SearchFragment();
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, searchFragment, "SearchFragment");
        ft.addToBackStack("ListFragment");
        ft.commit();
    }

    private void showSeriesInfoFragment(Series series)
    {
        SeriesInfoFragment seriesInfoFragment = new SeriesInfoFragment();

        Bundle arguments = new Bundle();
        arguments.putSerializable("series", series);
        seriesInfoFragment.setArguments(arguments);

        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, seriesInfoFragment, "SeriesInfoFragment");
        ft.addToBackStack("ListFragment");
        ft.commit();
    }


    public void newDialogInstance()
    {
        NoConnectionDialog dialog = new NoConnectionDialog();
        Bundle args = new Bundle();
        args.putSerializable("data", this);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "NoConnectionDialog");
    }

    private void initImageBitmaps()
    {
        Log.d(TAG, "initImageBitmaps: preparing bitmaps");
    }

    private void initRecyclerView()
    {
        Log.d(TAG, "initRecyclerView: initialising");
        RecyclerView recyclerView = getView().findViewById(R.id.series_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initList()
    {
        Log.d(TAG, "initList: db connection");
        MySQLConnection mySQLConnection = new MySQLConnection();
        mySQLConnection.execute();
    }

    @Override
    public void OnSuccessfulClick()
    {
        Toast.makeText(getContext(), "Series List has refreshed", Toast.LENGTH_SHORT).show();

        FragmentTransaction tr = getFragmentManager().beginTransaction();
        tr.replace(R.id.fragment_container, new ListFragment());
        tr.commit();
    }

    @Override
    public void onSeriesClick(Series series)
    {
        Log.d(TAG, "onSeriesClick: clicked:" + series.getTitle());
        showSeriesInfoFragment(series);
    }

    public void printList(List<Series> list)
    {
        for(Series sr:list)
        {
            Log.d(TAG, "||" + sr.getCover_image() + "|" + sr.getTitle() + "|" + sr.getAir_date() + "|" + sr.getEpisode_number() + "||");
        }
    }

    // Lesson: Don't set attributes of widgets like TextView/ImageView in the background
    public class MySQLConnection extends AsyncTask<Void, Void, Void>
    {
        private boolean wasRequestSuccessful;
        private ArrayList<Series> tempList;

        @Override
        protected Void doInBackground(Void... voids)
        {
            SelectTable selectTable = new SelectTable(0);
            tempList = selectTable.getSeriesList();
            wasRequestSuccessful = selectTable.getWasRequestSuccessful();
            printList(tempList);
            Log.d(TAG, "doInBackground: Successful?:" + wasRequestSuccessful);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            // Empty List
            if(tempList.size() == 0)
            {
                emptyListTV.setText(" Your Series List is empty!\nAdd any series you're watching\nor planning to watch\nby tapping the add button.");
                emptyListLayout.setBackgroundResource(R.drawable.button);
                emptyListImage.setImageResource(R.drawable.ic_baseline_sentiment_very_dissatisfied_24);
            }
            else
            {
                list.addAll(tempList);
            }

            // Database connection dialog
            if(wasRequestSuccessful)
            {
                adapter = new SeriesRecyclerViewAdapter(getContext(), list, ListFragment.this);
                initRecyclerView();
            }
            else
            {
                NoDatabaseDialog dialog = new NoDatabaseDialog();
                dialog.show(getFragmentManager(), "NoDatabaseDialog");
            }

            super.onPostExecute(aVoid);
        }
    }

}
