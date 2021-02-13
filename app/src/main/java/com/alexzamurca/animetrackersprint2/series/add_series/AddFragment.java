package com.alexzamurca.animetrackersprint2.series.add_series;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.algorithms.AdjustAirDate;
import com.alexzamurca.animetrackersprint2.algorithms.CheckConnection;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;

public class AddFragment extends Fragment implements  AddRecyclerViewAdapter.RowClickListener {

    private static final String TAG = "SearchActivity";
    private NavController navController;

    private ArrayList<SearchResult> list = new ArrayList<>();
    private AddRecyclerViewAdapter adapter;
    private EditText editText;
    private View globalView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        globalView = inflater.inflate(R.layout.fragment_add, container, false);
        Log.d(TAG, "onCreate: started");

        Toolbar toolbar = globalView.findViewById(R.id.add_series_toolbar_object);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if(activity.getSupportActionBar()!=null)
        {
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        return globalView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        editText = view.findViewById(R.id.search_edit_text);
        progressBar = view.findViewById(R.id.add_series_progress_bar);
        progressBar.setVisibility(View.GONE);
        initImageBitmaps();
        initRecyclerView();

        editText.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
            {
                searchProcess();
            }
            return false;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void searchProcess()
    {
        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(globalView.findViewById(R.id.search_layout).getWindowToken(), 0);

        CheckConnection checkConnection = new CheckConnection(getContext());
        boolean isConnected = checkConnection.isConnected();
        if (isConnected)
        {
            String seriesName = editText.getText().toString();
            Snackbar.make(globalView, "Searching for \"" + seriesName + "\"", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            adapter.searchName(seriesName);
        }
        else
        {
            Snackbar.make(globalView, "Cannot connect to the internet, check internet connection!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void initImageBitmaps()
    {
        Log.d(TAG, "initImageBitmaps: preparing bitmaps");
    }

    private void initRecyclerView()
    {
        Log.d(TAG, "initRecyclerView: initialising");
        RecyclerView recyclerView = globalView.findViewById(R.id.search_recycler_view);
        adapter = new AddRecyclerViewAdapter(list, getContext(), this, globalView.findViewById(R.id.no_search_results_text), progressBar);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onSuccessfulClick(Series series)
    {
        AdjustAirDate adjustAirDate = new AdjustAirDate(series);
        Calendar calendar = adjustAirDate.getCalendar();
        if(calendar!=null)
        {
            NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(getContext());
            notificationAiringChannel.setNotification(series, calendar);
        }

        navController.navigate(R.id.listFragment);
    }
}
