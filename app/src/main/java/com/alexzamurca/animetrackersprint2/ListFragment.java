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

import com.alexzamurca.animetrackersprint2.Date.ConvertDateToCalendar;
import com.alexzamurca.animetrackersprint2.notifications.NotificationAiringChannel;
import com.alexzamurca.animetrackersprint2.series.Database.SelectTable;
import com.alexzamurca.animetrackersprint2.series.Database.UpdateNotificationsOn;
import com.alexzamurca.animetrackersprint2.series.algorithms.AlphabeticalSortList;
import com.alexzamurca.animetrackersprint2.series.algorithms.DateSortSeriesList;
import com.alexzamurca.animetrackersprint2.series.dialog.CheckConnection;
import com.alexzamurca.animetrackersprint2.series.dialog.IncorrectAirDateDialog;
import com.alexzamurca.animetrackersprint2.series.dialog.NoConnectionDialog;
import com.alexzamurca.animetrackersprint2.series.dialog.NoDatabaseDialog;
import com.alexzamurca.animetrackersprint2.series.dialog.NotificationsOffDialog;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;
import com.alexzamurca.animetrackersprint2.series.series_list.SeriesRecyclerViewAdapter;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class ListFragment extends Fragment implements NoConnectionDialog.TryAgainListener, SeriesRecyclerViewAdapter.OnSeriesListener, NoDatabaseDialog.ReportBugListener, IncorrectAirDateDialog.IncorrectAirDateListener, NotificationsOffDialog.OnResponseListener {
    private static final String TAG = "ListFragment";
    private transient FragmentActivity mContext;

    public SeriesRecyclerViewAdapter.OnSeriesListener recyclerViewListener;

    private ArrayList<Series> list = new ArrayList<>();
    private List<Series> oldList;
    private String session;

    private SeriesRecyclerViewAdapter adapter;
    private TextView emptyListTV;
    private ImageView emptyListImage;
    private LinearLayout emptyListLayout;
    private TextView loadingTV;
    private ImageView loadingImage;
    private SwipeRefreshLayout swipeRefreshLayout;

    private View mView;
    private NavController mNavController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_series_list, container, false);
        recyclerViewListener = this;

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Account", Context.MODE_PRIVATE);
        session = sharedPreferences.getString("session", "");

        Toolbar toolbar = mView.findViewById(R.id.series_list_toolbar_object);
        setHasOptionsMenu(true);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        emptyListTV = mView.findViewById(R.id.series_empty_list);
        emptyListImage = mView.findViewById(R.id.series_empty_list_image);
        emptyListLayout = mView.findViewById(R.id.series_empty_list_linear_layout);
        loadingTV = mView.findViewById(R.id.series_loading_text);
        loadingImage = mView.findViewById(R.id.series_loading_image);
        swipeRefreshLayout = mView.findViewById(R.id.series_swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this::checkConnectionAndInitList);

        checkConnectionAndInitList();

        FloatingActionButton addButton = mView.findViewById(R.id.series_list_floating_add_button);
        // Search button
        addButton.setOnClickListener(v ->
            changeToSearchFragment()
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
        oldList = new ArrayList<>();
        
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener()
        {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item)
            {
                if(list.size()!=0)
                {
                    oldList.clear();
                    oldList.addAll(adapter.getList());
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
                if(list.size()!=0)
                {
                    adapter.restoreFromList(oldList);
                    return true;
                }
                return false;
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
            int selection = sharedPreferences.getInt("selected_sort_option_index", 7);

            popup.getMenu().getItem(selection).setChecked(true);

            setupDropDownOnClick(popup);

            popup.show();
        }
        return true;
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
                return;

            // Add Date down
            case 5:
                Log.d(TAG, "setupDropDownOnClick: sort air date up clicked");
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
            public boolean onQueryTextChange(String newText)
            {
                if(!newText.isEmpty())
                {
                    //adapter.restoreFromList(oldList);
                    //adapter.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    private void changeToSearchFragment()
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
        // Show loading - (credit: http://www.lowgif.com/view.html)
        Glide.with(requireContext())
                .load(R.drawable.loading)
                .into(loadingImage);
        loadingTV.setText(R.string.loading_3_dots);

        MySQLConnection mySQLConnection = new MySQLConnection();
        mySQLConnection.execute();
    }

    @Override
    public void OnSuccessfulClick()
    {
        Toast.makeText(getContext(), "Series List has refreshed", Toast.LENGTH_SHORT).show();

        mNavController.navigate(R.id.listFragment);
    }

    @Override
    public void onSeriesClick(Series series)
    {
        showSeriesInfoFragment(series);
    }

    @Override
    public void onNotificationsOff(Series series)
    {
        NotificationsOffDialog dialog = new NotificationsOffDialog();
        // need to pass series
        Bundle args = new Bundle();
        args.putSerializable("series", series);
        Log.d(TAG, "onNotificationsOff: about to add onResponseListener");
        //args.putSerializable("onResponseListener",this);
        dialog.setArguments(args);
        dialog.show(mContext.getSupportFragmentManager(), "notificationsOffDialog");
    }

    @Override
    public void onNotificationsOn(Series series)
    {
        UpdateNotificationsOnAsync updateNotificationsOnAsync = new UpdateNotificationsOnAsync();
        updateNotificationsOnAsync.setSelectedSeries(series);
        updateNotificationsOnAsync.execute();
    }

    @Override
    public void onChangeNotificationTime(Series series)
    {
        ListFragmentDirections.ActionChangeNotificationReminder action = ListFragmentDirections.actionChangeNotificationReminder(series);
        mNavController.navigate(action);
    }

    @Override
    public void onErrorWrongAirDate(Series series)
    {
        IncorrectAirDateDialog dialog = new IncorrectAirDateDialog();
        Bundle args = new Bundle();
        Log.d(TAG, "onErrorWrongAirDate: about to add incorrect air date listener");
        //args.putSerializable("incorrectAirDateListener", ListFragment.this);
        args.putSerializable("series", series);
        dialog.setArguments(args);
        dialog.show(mContext.getSupportFragmentManager(), "incorrectAirDateDialog");
    }

    public void printList(List<Series> list)
    {
        for(Series sr:list)
        {
            Log.d(TAG, "||" + sr.getCover_image() + "|" + sr.getTitle() + "|" + sr.getAir_date() + "|" + sr.getNext_episode_number() + "||");
        }
    }

    @Override
    public void OnReportBugButtonClick()
    {
        mNavController.navigate(R.id.action_report_bug_dialog_button_clicked);
    }

    @Override
    public void OnChangeTimeZoneClick()
    {
        mNavController.navigate(R.id.action_dialog_change_time_zone);
    }

    @Override
    public void OnChangeAirDateClick(Series series)
    {
        ListFragmentDirections.ActionDialogChangeAirDate action = ListFragmentDirections.actionDialogChangeAirDate(series);
        mNavController.navigate(action);
    }

    @Override
    public void onYesClickListener(Series series)
    {
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
        boolean notificationsOn = series.getNotifications_on()==1;
        // If has an air date and notifications are not off
        if(!air_date.equals("") && notificationsOn)
        {
            ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
            Calendar calendar = convertDateToCalendar.timeZoneConvert(air_date);

            Calendar calendarNow = Calendar.getInstance(TimeZone.getDefault());
            boolean airDatePassed = calendar.before(calendarNow);

            if(!airDatePassed)
            {
                String air_date_change = series.getAir_date_change();
                String notification_change = series.getNotification_change();

                // If series has a set air date change
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
                // If series has a set notification change
                if(!notification_change.equals(""))
                {
                    String[] quantityMetricBAArray  = notification_change.split(" ");
                    int quantity = Integer.parseInt(quantityMetricBAArray[0]);
                    String metric = quantityMetricBAArray[1];
                    String beforeAfter = quantityMetricBAArray[2];


                    // Add minutes, hours, days
                    if(beforeAfter.equals("before"))
                    {
                        switch (metric)
                        {
                            case "minutes":
                                calendar.add(Calendar.MINUTE, -quantity);
                                break;
                            case "hours":
                                calendar.add(Calendar.HOUR_OF_DAY, -quantity);
                                break;
                            case "days":
                                calendar.add(Calendar.DAY_OF_MONTH, -quantity);
                                break;
                        }
                    }
                    else if(beforeAfter.equals("after"))
                    {
                        switch (metric)
                        {
                            case "minutes":
                                calendar.add(Calendar.MINUTE, +quantity);
                                break;
                            case "hours":
                                calendar.add(Calendar.HOUR_OF_DAY, +quantity);
                                break;
                            case "days":
                                calendar.add(Calendar.DAY_OF_MONTH, +quantity);
                                break;
                        }
                    }
                }

                NotificationAiringChannel notificationAiringChannel = new NotificationAiringChannel(getContext(), series, calendar);
                notificationAiringChannel.setNotification();

                Log.d(TAG, "onSuccessfulAdd: set notification for \"" + series.getTitle() + "\"");
            }
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
            SelectTable selectTable = new SelectTable(session);
            tempList = selectTable.getSeriesList();
            wasRequestSuccessful = selectTable.getWasRequestSuccessful();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            // Hide loading
            Glide.with(requireContext()).clear(loadingImage);
            loadingTV.setText("");

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
                Bundle args = new Bundle();
                Log.d(TAG, "onPostExecute: about to add report bug listener");
                //args.putSerializable("reportBugListener", ListFragment.this);
                dialog.setArguments(args);
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
            UpdateNotificationsOn updateNotificationsOn = new UpdateNotificationsOn(session, selectedSeries.getAnilist_id(), 0);
            isSuccessful = updateNotificationsOn.update() == 0;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            String title = selectedSeries.getTitle();
            if(isSuccessful)
            {
                Toast.makeText(getContext(), "You will no longer receive notifications for \"" + title +"\"!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Failed to turn notifications off for \"" + title +"\", notifications are still on.", Toast.LENGTH_LONG).show();
            }
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
            UpdateNotificationsOn updateNotificationsOn = new UpdateNotificationsOn(session, selectedSeries.getAnilist_id(), 1);
            isSuccessful = updateNotificationsOn.update() == 0;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            String title = selectedSeries.getTitle();
            if(isSuccessful)
            {
                Toast.makeText(getContext(), "You will now receive notifications for \"" + title +"\"!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "Failed to turn notifications on for \"" + title +"\", notifications are still off.", Toast.LENGTH_LONG).show();
            }
            mNavController.navigate(R.id.listFragment);
            super.onPostExecute(aVoid);
        }
    }



}
