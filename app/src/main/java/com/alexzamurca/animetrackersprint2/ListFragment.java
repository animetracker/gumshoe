package com.alexzamurca.animetrackersprint2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alexzamurca.animetrackersprint2.algorithms.AppGround;
import com.alexzamurca.animetrackersprint2.algorithms.CheckConnection;
import com.alexzamurca.animetrackersprint2.algorithms.LocalListStorage;
import com.alexzamurca.animetrackersprint2.dialog.NoConnectionDialog;
import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.Database.SelectTable;
import com.alexzamurca.animetrackersprint2.Database.UpdateNotificationsOn;
import com.alexzamurca.animetrackersprint2.algorithms.AdjustAirDate;
import com.alexzamurca.animetrackersprint2.algorithms.AlphabeticalSortList;
import com.alexzamurca.animetrackersprint2.algorithms.DateSortSeriesList;
import com.alexzamurca.animetrackersprint2.dialog.NotificationsOffDialog;
import com.alexzamurca.animetrackersprint2.notifications.UpdateFailedNotification;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;
import com.alexzamurca.animetrackersprint2.series.series_list.SeriesRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ListFragment extends Fragment implements SeriesRecyclerViewAdapter.OnSeriesListener, NotificationsOffDialog.OnResponseListener{
    private static final String TAG = "ListFragment";
    private transient FragmentActivity mContext;

    public SeriesRecyclerViewAdapter.OnSeriesListener recyclerViewListener;

    private ArrayList<Series> list = new ArrayList<>();
    private String session;

    private SeriesRecyclerViewAdapter adapter;
    private LinearLayout emptyListLayout;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private View mView;
    private NavController mNavController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_series_list, container, false);
        recyclerViewListener = this;

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Account", Context.MODE_PRIVATE);
        session = sharedPreferences.getString("session", "");
        Log.d(TAG, "onCreateView: session: " + session);

        Toolbar toolbar = mView.findViewById(R.id.series_list_toolbar_object);
        setHasOptionsMenu(true);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        TextView emptyListTV = mView.findViewById(R.id.series_empty_list);
        ImageView emptyListImage = mView.findViewById(R.id.series_empty_list_image);
        emptyListLayout = mView.findViewById(R.id.series_empty_list_linear_layout);
        emptyListTV.setText(" Your Series List is empty!\nAdd any airing series or\nseries soon to be aired\nby tapping the + button below.");
        emptyListLayout.setBackgroundResource(R.drawable.button);
        emptyListImage.setImageResource(R.drawable.ic_baseline_sentiment_very_dissatisfied_24);
        emptyListLayout.setVisibility(View.GONE);

        progressBar = mView.findViewById(R.id.series_progress_bar);

        recyclerView = mView.findViewById(R.id.series_recycler_view);

        swipeRefreshLayout = mView.findViewById(R.id.series_swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this::initList);

        initList();

        FloatingActionButton addButton = mView.findViewById(R.id.series_list_floating_add_button);
        // Search button
        addButton.setOnClickListener(v ->
            changeToAddFragment()
        );

        return mView;
    }

    @Override
    public void onAttach(@NonNull Context context)
    {
        mContext = (FragmentActivity)context;
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
        hideKeyboard();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.series_list_toolbar_menu, menu);

        MenuItem item = menu.findItem(R.id.series_list_toolbar_search);

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener()
        {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item)
            {
                if(list.size()!=0)
                {
                    return true;
                }
                else
                {
                    Toast.makeText(getContext(), "Can't search when list is empty!", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item)
            {
                mNavController.navigate(R.id.listFragment);
                return true;
            }
        });
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("search the series list");
        searchView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.greyishWhite));
        manageSearchView(searchView);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.series_list_toolbar_sort)
        {
            PopupMenu popup = new PopupMenu(getContext(), mView.findViewById(R.id.series_list_toolbar_sort));

            popup.getMenuInflater().inflate(R.menu.series_sort_dropdown, popup.getMenu());

            // Get sort state from SharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Series List", Context.MODE_PRIVATE);
            int selection = sharedPreferences.getInt("selected_sort_option_index", 5);

            popup.getMenu().getItem(selection).setChecked(true);

            setupDropDownOnClick(popup);

            popup.show();
        }
        return true;
    }

    private void hideKeyboard()
    {
        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mView.findViewById(R.id.series_list_layout).getWindowToken(), 0);
    }

    private void setupDropDownOnClick(PopupMenu popup)
    {
        popup.setOnMenuItemClickListener(item ->
        {
            int itemIndex = findIndexOfItem(item, popup.getMenu());

            checkSelectionUncheckRest(item, popup.getMenu());

            sortListAccordingToSelection(itemIndex);

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Series List", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("selected_sort_option_index", itemIndex);

            editor.apply();

            mNavController.navigate(R.id.listFragment);

            return true;
        });
    }

    private int findIndexOfItem(MenuItem item, Menu menu)
    {
        for(int i = 0; i < menu.size(); i++)
        {
            if(menu.getItem(i).getItemId() == item.getItemId()) return i;
        }
        return -1;
    }

    private void checkSelectionUncheckRest(MenuItem item, Menu menu)
    {
        // Set all items to unchecked
        for(int i = 0; i < menu.size(); i++)
        {
            menu.getItem(i).setChecked(false);
        }

        // Then check the selected item
        item.setChecked(true);


    }

    private void sortListAccordingToSelection(int selection)
    {
        List<Series> listFromAdapter = adapter.getList();
        AlphabeticalSortList alphabeticalSortList = new AlphabeticalSortList(listFromAdapter);
        DateSortSeriesList dateSortSeriesList = new DateSortSeriesList(listFromAdapter);
        switch (selection)
        {
            // No selection
            case -1:
                return;

            // A-Z
            case 0:
                Log.d(TAG, "setupDropDownOnClick: sort A-Z clicked");
                List<Series> sortedList = alphabeticalSortList.sortAlphabetically();
                adapter.restoreFromList(sortedList);
                return;

            // Z-A
            case 1:
                Log.d(TAG, "setupDropDownOnClick: sort Z-A clicked");
                sortedList = alphabeticalSortList.sortReverseAlphabetically();
                adapter.restoreFromList(sortedList);
                return;

            // Latest
            case 2:
                Log.d(TAG, "setupDropDownOnClick: sort Latest clicked");
                sortedList = dateSortSeriesList.sortMostRecent();
                adapter.restoreFromList(sortedList);
                return;

            // Oldest
            case 3:
                Log.d(TAG, "setupDropDownOnClick: sort Oldest clicked");
                sortedList = dateSortSeriesList.sortLeastRecent();
                adapter.restoreFromList(sortedList);
                return;

                // Add Date up
            case 4:
                Log.d(TAG, "setupDropDownOnClick: sort air date up clicked");
                Collections.reverse(listFromAdapter);
                adapter.restoreFromList(listFromAdapter);
                return;

            // Add Date down
            case 5:
                Log.d(TAG, "setupDropDownOnClick: sort air date down clicked");
        }
    }

    private void manageSearchView(SearchView searchView)
    {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            // After SearchView closes
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                adapter.getFilter().filter(query);

                hideKeyboard();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void changeToAddFragment()
    {
        mNavController.navigate(R.id.action_adding_new_series);
    }

    private void showSeriesInfoFragment(Series series)
    {
        Bundle arguments = new Bundle();
        arguments.putSerializable("series", series);

        mNavController.navigate(R.id.action_showing_series_info, arguments);
    }

    private void initRecyclerView()
    {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initList()
    {
        CheckConnection checkConnection = new CheckConnection(requireContext());
        boolean isConnectedToInternet = checkConnection.isConnected();
        if (isConnectedToInternet)
        {
            progressBar.setVisibility(View.VISIBLE);

            GettingTableAsync gettingTableAsync = new GettingTableAsync();
            gettingTableAsync.execute();
            Log.d(TAG, "getting table has connection");
        }
        else
        {
            Log.d(TAG, "getting table: NO INTERNET");

            AppGround appGround = new AppGround();
            boolean isAppOnForeground = appGround.isAppOnForeground(requireContext());

            if(isAppOnForeground)
            {
                Log.d(TAG, "getTable request app in foreground");
                NoConnectionDialog noConnectionDialog = new NoConnectionDialog();
                noConnectionDialog.show(mContext.getSupportFragmentManager() , "NoConnectionDialog");
            }
            else
            {
                Log.d(TAG, "getTable request app in background");
                UpdateFailedNotification updateFailedNotification = new UpdateFailedNotification(requireContext());
                updateFailedNotification.showNotification();

                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("App", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean("need_to_update_db", true);
                Log.d(TAG, "initList: app set to need_to_update_db mode");
                editor.apply();
            }
        }

    }

    @Override
    public void onSeriesClick(Series series)
    {
        showSeriesInfoFragment(series);
    }

    @Override
    public void onNotificationsOff(Series series)
    {
        doOnNotificationsOff(series);
    }

    private void doOnNotificationsOff(Series series)
    {
        NotificationsOffDialog dialog = new NotificationsOffDialog();
        // need to pass series
        Bundle args = new Bundle();
        args.putSerializable("series", series);
        Log.d(TAG, "onNotificationsOff: about to add onResponseListener");
        args.putSerializable("onResponseListener",this);
        dialog.setArguments(args);
        dialog.show(mContext.getSupportFragmentManager(), "NotificationsOffDialog");
    }

    @Override
    public void onNotificationsOn(Series series)
    {
        CheckConnection checkConnection = new CheckConnection(requireContext());
        boolean isConnectedToInternet = checkConnection.isConnected();
        if (isConnectedToInternet)
        {
            progressBar.setVisibility(View.VISIBLE);
            UpdateNotificationsOnAsync updateNotificationsOnAsync = new UpdateNotificationsOnAsync();
            updateNotificationsOnAsync.setSelectedSeries(series);
            updateNotificationsOnAsync.execute();
            Log.d(TAG, "update notifications on has connection");
        }
        else
        {
            Log.d(TAG, "update notifications on: NO INTERNET");

            AppGround appGround = new AppGround();
            boolean isAppOnForeground = appGround.isAppOnForeground(requireContext());

            if(isAppOnForeground)
            {
                Log.d(TAG, "getTable request app in foreground");
                NoConnectionDialog noConnectionDialog = new NoConnectionDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean("update_db", true);
                noConnectionDialog.setArguments(bundle);
                noConnectionDialog.show(mContext.getSupportFragmentManager() , "NoConnectionDialog");
            }
            else
            {
                Log.d(TAG, "update notifications on request app in background");
                UpdateFailedNotification updateFailedNotification = new UpdateFailedNotification(requireContext());
                updateFailedNotification.showNotification();

                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("App", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean("need_to_update_db", true);
                Log.d(TAG, "OnNotificationsOn: app set to need_to_update_db mode");
                editor.apply();
            }
        }

    }

    @Override
    public void onChangeNotificationTime(Series series)
    {
        // If series has an air date
        if(!series.getAir_date().equals(""))
        {
            ListFragmentDirections.ActionChangeNotificationReminder action = ListFragmentDirections.actionChangeNotificationReminder(series);
            mNavController.navigate(action);
        }
        else
        {
            Toast.makeText(getContext(), "You cannot change notification reminder time for series with unknown air date!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onErrorWrongAirDate(Series series)
    {
       doOnSeriesError(series);
    }

    void doOnSeriesError(Series series)
    {
        // If series has an air date
        if(!series.getAir_date().equals(""))
        {
            ListFragmentDirections.ActionDialogChangeAirDate action = ListFragmentDirections.actionDialogChangeAirDate(series);
            mNavController.navigate(action);
        }
        else
        {
            Toast.makeText(getContext(), "You cannot change the air date for series with unknown air date!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onYesClickListener(Series series)
    {
        CheckConnection checkConnection = new CheckConnection(requireContext());
        boolean isConnectedToInternet = checkConnection.isConnected();
        if (isConnectedToInternet)
        {
            progressBar.setVisibility(View.VISIBLE);
            UpdateNotificationsOffAsync updateNotificationsOffAsync = new UpdateNotificationsOffAsync();
            updateNotificationsOffAsync.setSelectedSeries(series);
            updateNotificationsOffAsync.execute();
            Log.d(TAG, "update notifications off has internet");
        }
        else
        {
            Log.d(TAG, "updateNotificationsOff: NO INTERNET");

            AppGround appGround = new AppGround();
            boolean isAppOnForeground = appGround.isAppOnForeground(requireContext());

            if(isAppOnForeground)
            {
                Log.d(TAG, "updateNotificationsOff request app in foreground");
                NoConnectionDialog noConnectionDialog = new NoConnectionDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean("update_db", true);
                noConnectionDialog.setArguments(bundle);
                noConnectionDialog.show(mContext.getSupportFragmentManager() , "NoConnectionDialog");
            }
            else
            {
                Log.d(TAG, "updateNotificationsOff request app in background");
                UpdateFailedNotification updateFailedNotification = new UpdateFailedNotification(requireContext());
                updateFailedNotification.showNotification();

                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("App", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean("need_to_update_db", true);
                Log.d(TAG, "OnYesClickListener: app set to need_to_update_db mode");
                editor.apply();
            }
        }

    }

    // Lesson: Don't set attributes of widgets like TextView/ImageView in the background
    public class GettingTableAsync extends AsyncTask<Void, Void, Void>
    {
        private ArrayList<Series> tempList;
        private SelectTable selectTable;

        @Override
        protected Void doInBackground(Void... voids)
        {
            selectTable = new SelectTable(session, getContext());
            tempList = selectTable.getSeriesList();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            // Hide loading
            progressBar.setVisibility(View.GONE);

            // Stop refreshing (need this in case swipe refresh is used)
            swipeRefreshLayout.setRefreshing(false);

            // Empty List
            if(tempList.size() == 0)
            {
                emptyListLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.GONE);
            }
            else
            {
                emptyListLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                list.clear();
                list.addAll(tempList);

                LocalListStorage localListStorage = new LocalListStorage(requireContext());
                localListStorage.store(list);
            }


            adapter = new SeriesRecyclerViewAdapter(requireContext(), list, ListFragment.this, mNavController);

            initRecyclerView();

            // Get sort state from SharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("Series List", Context.MODE_PRIVATE);
            int selection = sharedPreferences.getInt("selected_sort_option_index", 5);
            sortListAccordingToSelection(selection);

            super.onPostExecute(aVoid);
        }
    }

    private class UpdateNotificationsOffAsync extends AsyncTask<Void, Void, Void>
    {
        private boolean isSuccessful;
        private Series selectedSeries;

        public void setSelectedSeries(Series selectedSeries)
        {
            this.selectedSeries = selectedSeries;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            Log.d(TAG, "doInBackground: UpdateNotificationsOffAsync: session: " + session);
            UpdateNotificationsOn updateNotificationsOn = new UpdateNotificationsOn(session, selectedSeries.getAnilist_id(), 0, getContext());
            isSuccessful = updateNotificationsOn.update() == 0;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            progressBar.setVisibility(View.GONE);
            String title = selectedSeries.getTitle();
            if(isSuccessful)
            {
                Toast.makeText(getContext(), "You will no longer receive notifications for \"" + title +"\"!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Failed to turn notifications off for \"" + title +"\", notifications are still on.", Toast.LENGTH_LONG).show();
            }

            NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(getContext());
            notificationAiringChannel.cancel(selectedSeries);

            mNavController.navigate(R.id.listFragment);
            super.onPostExecute(aVoid);
        }
    }

    private class UpdateNotificationsOnAsync extends AsyncTask<Void, Void, Void>
    {
        private boolean isSuccessful;
        private Series selectedSeries;

        public void setSelectedSeries(Series selectedSeries)
        {
            this.selectedSeries = selectedSeries;
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            Log.d(TAG, "doInBackground: UpdateNotificationsOnAsync: session: " + session);
            UpdateNotificationsOn updateNotificationsOn = new UpdateNotificationsOn(session, selectedSeries.getAnilist_id(), 1, getContext());
            isSuccessful = updateNotificationsOn.update() == 0;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            progressBar.setVisibility(View.GONE);
            String title = selectedSeries.getTitle();
            if(isSuccessful)
            {
                Toast.makeText(getContext(), "You will now receive notifications for \"" + title +"\"!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Failed to turn notifications on for \"" + title +"\", notifications are still off.", Toast.LENGTH_LONG).show();
            }

            AdjustAirDate adjustAirDate = new AdjustAirDate(selectedSeries);
            Calendar calendar = adjustAirDate.getCalendar();

            if(calendar!=null)
            {
                NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(getContext());
                notificationAiringChannel.setNotification(selectedSeries, calendar);
            }

            mNavController.navigate(R.id.listFragment);
            super.onPostExecute(aVoid);
        }
    }



}
