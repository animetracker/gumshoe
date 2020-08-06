package com.alexzamurca.animetrackersprint2;

import android.content.Context;
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
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
    private FragmentActivity mContext;

    private ArrayList<Series> list = new ArrayList<>();
    private SeriesRecyclerViewAdapter adapter;
    private TextView emptyListTV;
    private ImageView emptyListImage;
    private LinearLayout emptyListLayout;
    
    private View mView;
    private NavController mNavController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_series_list, container, false);

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

        Button searchButton = mView.findViewById(R.id.series_search_button);
        emptyListTV = mView.findViewById(R.id.series_empty_list);
        emptyListImage = mView.findViewById(R.id.series_empty_list_image);
        emptyListLayout = mView.findViewById(R.id.series_empty_list_linear_layout);

        initImageBitmaps();


        // Search button
        searchButton.setOnClickListener(v ->
        {
            Log.d(TAG, "onClick: Clicked search_button");

            changeToSearchFragment();
        });
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNavController = Navigation.findNavController(view);
    }

    private void changeToSearchFragment()
    {
        mNavController.navigate(R.id.action_adding_new_series);
        /*
        SearchFragment searchFragment = new SearchFragment();
        final FragmentTransaction ft = mContext.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, searchFragment, "SearchFragment");
        ft.addToBackStack("ListFragment");
        ft.commit();
         */
    }



    @Override
    public void onAttach(@NonNull Context context)
    {
        mContext = (FragmentActivity)context;
        super.onAttach(context);
    }

    private void showSeriesInfoFragment(Series series)
    {
        Bundle arguments = new Bundle();
        arguments.putSerializable("series", series);

        mNavController.navigate(R.id.action_showing_series_info, arguments);

        /*
        SeriesInfoFragment seriesInfoFragment = new SeriesInfoFragment();
        seriesInfoFragment.setArguments(arguments);
        final FragmentTransaction ft = mContext.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, seriesInfoFragment, "SeriesInfoFragment");
        ft.addToBackStack("ListFragment");
        ft.commit();

         */
    }


    public void newDialogInstance()
    {
        NoConnectionDialog dialog = new NoConnectionDialog();
        Bundle args = new Bundle();
        args.putSerializable("data", this);
        dialog.setArguments(args);
        dialog.show(mContext.getSupportFragmentManager(), "NoConnectionDialog");
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

        mNavController.navigate(R.id.listFragment);

        /*
        FragmentTransaction tr = mContext.getSupportFragmentManager().beginTransaction();
        tr.replace(R.id.fragment_container, new ListFragment());
        tr.commit();
         */
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
                list.clear();
                list.addAll(tempList);
            }

            // Database connection dialog
            if(wasRequestSuccessful)
            {
                adapter = new SeriesRecyclerViewAdapter(getContext(), list, ListFragment.this, Navigation.findNavController(mView));
                initRecyclerView();
            }
            else
            {
                NoDatabaseDialog dialog = new NoDatabaseDialog();
                dialog.show(mContext.getSupportFragmentManager(), "NoDatabaseDialog");
            }

            super.onPostExecute(aVoid);
        }
    }

}
