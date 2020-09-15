package com.alexzamurca.animetrackersprint2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.Database.SelectTable;
import com.alexzamurca.animetrackersprint2.Database.UpdateNotificationsOn;
import com.alexzamurca.animetrackersprint2.algorithms.AdjustAirDate;
import com.alexzamurca.animetrackersprint2.algorithms.AlphabeticalSortList;
import com.alexzamurca.animetrackersprint2.algorithms.DateSortSeriesList;
import com.alexzamurca.animetrackersprint2.series.dialog.CheckConnection;
import com.alexzamurca.animetrackersprint2.series.dialog.IncorrectAirDateDialog;
import com.alexzamurca.animetrackersprint2.series.dialog.NoConnectionDialog;
import com.alexzamurca.animetrackersprint2.series.dialog.NoDatabaseDialog;
import com.alexzamurca.animetrackersprint2.series.dialog.NotificationsOffDialog;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;
import com.alexzamurca.animetrackersprint2.series.series_list.SeriesRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class ListFragment extends Fragment implements SeriesRecyclerViewAdapter.OnSeriesListener, IncorrectAirDateDialog.IncorrectAirDateListener, NotificationsOffDialog.OnResponseListener{
    private static final String TAG = "ListFragment";
    private transient FragmentActivity mContext;

    public SeriesRecyclerViewAdapter.OnSeriesListener recyclerViewListener;

    private ArrayList<Series> list = new ArrayList<>();
    private String session;

    private SeriesRecyclerViewAdapter adapter;
    private TextView emptyListTV;
    private ImageView emptyListImage;
    private LinearLayout emptyListLayout;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private View mView;
    private NavController mNavController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_series_list, container, false);
        recyclerViewListener = this;

        performNotificationButtonCheck();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Account", Context.MODE_PRIVATE);
        session = sharedPreferences.getString("session", "");

        Toolbar toolbar = mView.findViewById(R.id.series_list_toolbar_object);
        setHasOptionsMenu(true);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        emptyListTV = mView.findViewById(R.id.series_empty_list);
        emptyListImage = mView.findViewById(R.id.series_empty_list_image);
        emptyListLayout = mView.findViewById(R.id.series_empty_list_linear_layout);

        progressBar = mView.findViewById(R.id.series_progress_bar);
        progressBar.setVisibility(View.GONE);

        swipeRefreshLayout = mView.findViewById(R.id.series_swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this::checkConnectionAndInitList);

        checkConnectionAndInitList();

        FloatingActionButton addButton = mView.findViewById(R.id.series_list_floating_add_button);
        // Search button
        addButton.setOnClickListener(v ->
            changeToAddFragment()
        );

        FloatingActionButton setNotificationButton = mView.findViewById(R.id.series_list_floating_set_notification_button);
        // Search button
        setNotificationButton.setOnClickListener(v ->
                setNotificationsForAllSeriesInList()
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
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Series List", Context.MODE_PRIVATE);
            int selection = sharedPreferences.getInt("selected_sort_option_index", 5);

            popup.getMenu().getItem(selection).setChecked(true);

            setupDropDownOnClick(popup);

            popup.show();
        }
        return true;
    }

    private void performNotificationButtonCheck()
    {
        Intent activityIntent = requireActivity().getIntent();
        Log.d(TAG, "performNotificationButtonCheck: is intent from MainActivity null?:" + (activityIntent != null));

        if(activityIntent!=null)
        {
            // Try get notifications off bundle
            Bundle notificationsOffBundle = activityIntent.getBundleExtra("bundle_notifications_off");
            if(notificationsOffBundle!=null)
            {
                Log.d(TAG, "performNotificationButtonCheck: received a notification off bundle, meaning a notification's \"turn notifications off\" button was pressed");
                boolean notificationsOff = notificationsOffBundle.getBoolean("notifications_off");
                if(notificationsOff)
                {
                    Log.d(TAG, "performNotificationButtonCheck: notifications off boolean is true");
                    Series series = (Series) notificationsOffBundle.getSerializable("series");
                    doOnNotificationsOff(series);
                    notificationsOffBundle.putBoolean("notifications_off", false);
                }
            }

            // Try get notifications off bundle
            Bundle incorrectAirDateBundle = activityIntent.getBundleExtra("bundle_incorrect_air_date");
            if(incorrectAirDateBundle!=null)
            {
                Log.d(TAG, "performNotificationButtonCheck: received a incorrect air date bundle, meaning a notification's \"incorrect air date\" button was pressed");
                boolean incorrectAirDate = incorrectAirDateBundle.getBoolean("incorrect_air_date");
                if(incorrectAirDate)
                {
                    Log.d(TAG, "performNotificationButtonCheck: incorrect air date boolean is true");
                    Series series = (Series) incorrectAirDateBundle.getSerializable("series");
                    doOnSeriesError(series);
                    incorrectAirDateBundle.putBoolean("incorrect_air_date", false);
                }
            }
        }
    }

    private void checkConnectionAndInitList()
    {
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
    }

    private void setupDropDownOnClick(PopupMenu popup)
    {
        popup.setOnMenuItemClickListener(item ->
        {
            int itemIndex = findIndexOfItem(item, popup.getMenu());

            checkSelectionUncheckRest(item, popup.getMenu());

            sortListAccordingToSelection(itemIndex);

            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Series List", Context.MODE_PRIVATE);
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

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mView.findViewById(R.id.series_list_layout).getWindowToken(), 0);

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


    public void newDialogInstance()
    {
        NoConnectionDialog dialog = new NoConnectionDialog();
        Bundle args = new Bundle();
        // Making sure we do not get an IOException
        Log.d(TAG, "newDialogInstance: about to add NoConnectionDialog.TryAgainListener");
        //args.putSerializable("data", this);
        dialog.setArguments(args);
        dialog.show(mContext.getSupportFragmentManager(), "NoConnectionDialog");

    }

    private void initRecyclerView()
    {
        RecyclerView recyclerView = requireView().findViewById(R.id.series_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void initList()
    {
        progressBar.setVisibility(View.VISIBLE);

        MySQLConnection mySQLConnection = new MySQLConnection();
        mySQLConnection.execute();
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
        dialog.show(mContext.getSupportFragmentManager(), "notificationsOffDialog");
    }

    @Override
    public void onNotificationsOn(Series series)
    {
        progressBar.setVisibility(View.VISIBLE);
        UpdateNotificationsOnAsync updateNotificationsOnAsync = new UpdateNotificationsOnAsync();
        updateNotificationsOnAsync.setSelectedSeries(series);
        updateNotificationsOnAsync.execute();
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

    private void doOnSeriesError(Series series)
    {
        IncorrectAirDateDialog dialog = new IncorrectAirDateDialog();
        Bundle args = new Bundle();
        Log.d(TAG, "onErrorWrongAirDate: about to add incorrect air date listener");
        args.putSerializable("incorrectAirDateListener", ListFragment.this);
        args.putSerializable("series", series);
        dialog.setArguments(args);
        dialog.show(mContext.getSupportFragmentManager(), "incorrectAirDateDialog");
    }

    @Override
    public void OnChangeTimeZoneClick()
    {
        mNavController.navigate(R.id.action_dialog_change_time_zone);
    }

    @Override
    public void OnChangeAirDateClick(Series series)
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
        progressBar.setVisibility(View.VISIBLE);
        UpdateNotificationsOffAsync updateNotificationsOffAsync = new UpdateNotificationsOffAsync();
        updateNotificationsOffAsync.setSelectedSeries(series);
        updateNotificationsOffAsync.execute();
    }

    private void setNotificationsForAllSeriesInList()
    {
        List<Series> currentList = adapter.getList();
        for(int i = 0; i < currentList.size(); i++)
        {
            Log.d(TAG, "onSuccessfulAdd: adjusting and setting notifications for \"" + currentList.get(i).getTitle() + "\"");
            adjustAndSetNotifications(currentList.get(i));
        }
    }

    private void adjustAndSetNotifications(Series series)
    {
        String air_date = series.getAir_date();
        Log.d(TAG, "adjustAndSetNotifications: air date " + air_date);

        AdjustAirDate adjustAirDate = new AdjustAirDate(series, getContext());
        Calendar calendar = adjustAirDate.getCalendar();

        // If calendar returned it means all is good and notifications can be set
        if(calendar!=null)
        {
            NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(getContext());
            notificationAiringChannel.setNotification(series, calendar);
        }

        Log.d(TAG, "onSuccessfulAdd: set notification for \"" + series.getTitle() + "\"");
    }

    // Lesson: Don't set attributes of widgets like TextView/ImageView in the background
    public class MySQLConnection extends AsyncTask<Void, Void, Void>
    {
        private boolean wasRequestSuccessful;
        private ArrayList<Series> tempList;

        @Override
        protected Void doInBackground(Void... voids)
        {
            SelectTable selectTable = new SelectTable(session, getContext());
            tempList = selectTable.getSeriesList();
            wasRequestSuccessful = selectTable.getWasRequestSuccessful();

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
                emptyListTV.setText(" Your Series List is empty!\nAdd any airing series or\nseries soon to be aired\nby tapping the + button below.");
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

                adapter = new SeriesRecyclerViewAdapter(getContext(), list, ListFragment.this, mNavController);

                initRecyclerView();

                // Get sort state from SharedPreferences
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Series List", Context.MODE_PRIVATE);
                int selection = sharedPreferences.getInt("selected_sort_option_index", -1);
                sortListAccordingToSelection(selection);
            }
            else
            {
                NoDatabaseDialog dialog = new NoDatabaseDialog();
                dialog.show(mContext.getSupportFragmentManager(), "NoDatabaseDialog");
            }

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

            AdjustAirDate adjustAirDate = new AdjustAirDate(selectedSeries, getContext());
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
