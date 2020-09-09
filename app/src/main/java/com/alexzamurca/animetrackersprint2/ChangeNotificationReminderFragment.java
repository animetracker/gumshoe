package com.alexzamurca.animetrackersprint2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alexzamurca.animetrackersprint2.Date.ConvertDateToCalendar;
import com.alexzamurca.animetrackersprint2.algorithms.ResetAlarmForSeries;
import com.alexzamurca.animetrackersprint2.series.Database.UpdateNotificationChange;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;
import com.alexzamurca.animetrackersprint2.series.series_list.SeriesRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Objects;

public class ChangeNotificationReminderFragment extends Fragment
{
    private static final String TAG = "ChangeNotificationReminderFragment";

    private Series series;

    private NavController navController;
    private TextView newChangeTV;
    private FloatingActionButton saveButton;
    private LinearLayout errorLayout;
    private LinearLayout minutesErrorLayout;
    private LinearLayout hoursErrorLayout;
    private LinearLayout daysErrorLayout;


    private int quantity = 0;
    private String metric = "minutes";
    private String beforeAfter = "before";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_change_notification_reminder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initSeries();
        setVariables();
        navController = Navigation.findNavController(view);
        Toast.makeText(getContext(), "Any changes made will not change the air date shown in the series list screen.", Toast.LENGTH_LONG).show();
        setupToolbar(view);
        setupErrorLayouts(view);
        setupSaveButton(view);
        setupSpinners(view);
        setupEditText(view);
        setupTextViews(view);
        setupHiddenLayoutAndButtons(view);
    }

    private void setupToolbar(View view)
    {
        // identify toolbar object
        Toolbar toolbar = view.findViewById(R.id.change_notification_reminder_toolbar_object);

        // Set toolbar and add home (back button)
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if(activity.getSupportActionBar()!=null)
        {
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    private void setVariables()
    {
        String notification__change = series.getNotification_change();
        if(!notification__change.equals(""))
        {
            String[] quantityMetricBAArray  = notification__change.split(" ");
            int quantityValue = Integer.parseInt(quantityMetricBAArray[0]);
            String metricValue = quantityMetricBAArray[1];
            String before_after = quantityMetricBAArray[2];

            quantity = quantityValue;
            metric = metricValue;
            beforeAfter = before_after;
        }
    }

    private void setupSpinners(View view)
    {
        setupMetricSpinner(view);
        setupBeforeAfterSpinner(view);
    }

    private void setupMetricSpinner(View view)
    {
        Spinner spinner = view.findViewById(R.id.change_notification_reminder_metric_spinner);

        switch (metric)
        {
            case "minutes":
                spinner.setSelection(0);
                break;
            case "hours":
                spinner.setSelection(1);
                break;
            case "days":
                spinner.setSelection(2);
                break;
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectionString = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: metric selected:" + selectionString);

                metric = selectionString;
                errorLayout.setVisibility(View.GONE);
                minutesErrorLayout.setVisibility(View.GONE);
                hoursErrorLayout.setVisibility(View.GONE);
                daysErrorLayout.setVisibility(View.GONE);

                if(metric.equals("minutes") && quantity > 59)
                {
                    errorLayout.setVisibility(View.VISIBLE);
                    minutesErrorLayout.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.GONE);
                }
                else if(metric.equals("hours") && quantity>23)
                {
                    errorLayout.setVisibility(View.VISIBLE);
                    hoursErrorLayout.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.GONE);
                }
                else if(metric.equals("days") && quantity>6)
                {
                    errorLayout.setVisibility(View.VISIBLE);
                    daysErrorLayout.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.GONE);
                }
                updateNewAirDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void setupBeforeAfterSpinner(View view)
    {
        Spinner spinner = view.findViewById(R.id.change_notification_reminder_before_after_spinner);

        if(beforeAfter.equals("before")) spinner.setSelection(0);
        if(beforeAfter.equals("after")) spinner.setSelection(1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectionString = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: before/after selected:" + selectionString);

                beforeAfter = selectionString;
                updateNewAirDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void setupEditText(View view)
    {
        EditText quantityET = view.findViewById(R.id.change_notification_reminder_quantity);
        if(quantity!=0) quantityET.setText(Integer.toString(quantity), TextView.BufferType.NORMAL);
        quantityET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                errorLayout.setVisibility(View.GONE);
                minutesErrorLayout.setVisibility(View.GONE);
                hoursErrorLayout.setVisibility(View.GONE);
                daysErrorLayout.setVisibility(View.GONE);

                // if the edit text is not empty (cant convert nothing to integer)
                if(!s.toString().equals(""))
                {
                    if(metric.equals("minutes") && Integer.parseInt(s.toString())>59)
                    {
                        errorLayout.setVisibility(View.VISIBLE);
                        minutesErrorLayout.setVisibility(View.VISIBLE);
                        saveButton.setVisibility(View.GONE);
                    }
                    else if(metric.equals("hours") && Integer.parseInt(s.toString())>23)
                    {
                        errorLayout.setVisibility(View.VISIBLE);
                        hoursErrorLayout.setVisibility(View.VISIBLE);
                        saveButton.setVisibility(View.GONE);
                    }
                    else if(metric.equals("days") && Integer.parseInt(s.toString())>6)
                    {
                        errorLayout.setVisibility(View.VISIBLE);
                        daysErrorLayout.setVisibility(View.VISIBLE);
                        saveButton.setVisibility(View.GONE);
                    }
                    else
                    {
                        quantity = Integer.parseInt(s.toString());
                        saveButton.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    saveButton.setVisibility(View.GONE);
                }

                updateNewAirDate();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupSaveButton(View view)
    {
        saveButton = view.findViewById(R.id.change_notification_reminder_save_button);
        saveButton.setVisibility(View.GONE);
        saveButton.setOnClickListener(v ->
            {
                if(hasNotificationReminderChanged())
                {
                    updateNotificationChangeDB();
                    ResetAlarmForSeries resetAlarmForSeries = new ResetAlarmForSeries(getContext());
                    resetAlarmForSeries.reset(series);
                }
            }
        );
    }

    private boolean hasNotificationReminderChanged()
    {
        String oldNotificationChange = series.getNotification_change();
        String newNotificationChange = quantity + " " + metric + " " + beforeAfter;
        return !oldNotificationChange.equals(newNotificationChange);
    }

    private void setupTextViews(View view)
    {
        String oldTitle = "Current Notification Change for\n\"" + series.getTitle() + "\":";
        TextView changeTitle = view.findViewById(R.id.change_notification_reminder_change_title);
        changeTitle.setText(oldTitle);

        String newTitle = "New Notification Change for\n\"" + series.getTitle() + "\":";
        TextView newChangeTitle = view.findViewById(R.id.change_notification_reminder_change_new_title);
        newChangeTitle.setText(newTitle);

        String change = series.getNotification_change();
        Log.d(TAG, "setupTextViews: change " + change);
        String changeText;
        if(change.equals(""))
        {
            changeText = "There is currently no notification set for this series, you will be notified the moment the series airs!";
        }
        else
        {
            changeText = change;
        }
        TextView changeTV = view.findViewById(R.id.change_notification_reminder_change);
        changeTV.setText(changeText);

        newChangeTV = view.findViewById(R.id.change_notification_reminder_new_change);
    }

    private String getChangedAirDate()
    {
        String air_date = series.getAir_date();
        String air_date_change = series.getAir_date_change();
        String newAirDate;
        // Account for the fact that there may not be any change
        if(!air_date_change.equals(""))
        {
            ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
            Calendar calendar = convertDateToCalendar.timeZoneConvert(air_date);

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

            newAirDate = convertDateToCalendar.reverseConvert(calendar);
        }
        else
        {
            newAirDate = air_date;
        }

        return newAirDate;
    }

    private void setupHiddenLayoutAndButtons(View view)
    {
        // Identify layout that we need to hide
        LinearLayout hiddenLayout = view.findViewById(R.id.change_notification_reminder_hidden_layout);
        hiddenLayout.setVisibility(View.GONE);

        Button noButton = view.findViewById(R.id.change_notification_reminder_button_no);
        noButton.setOnClickListener(v -> navController.navigateUp());

        Button yesButton = view.findViewById(R.id.change_notification_reminder_button_yes);
        yesButton.setOnClickListener(v ->
                {
                    hiddenLayout.setVisibility(View.VISIBLE);
                    yesButton.setBackgroundResource(R.drawable.greyish_white_with_stroke);
                    noButton.setVisibility(View.GONE);
                    saveButton.setVisibility(View.VISIBLE);
                }
        );
    }

    private void setupErrorLayouts(View view)
    {
        errorLayout = view.findViewById(R.id.change_notification_reminder_error_layout);
        errorLayout.setVisibility(View.GONE);

        minutesErrorLayout = view.findViewById(R.id.minutes_warning);
        minutesErrorLayout.setVisibility(View.GONE);

        hoursErrorLayout = view.findViewById(R.id.hours_warning);
        hoursErrorLayout.setVisibility(View.GONE);

        daysErrorLayout = view.findViewById(R.id.days_warning);
        daysErrorLayout.setVisibility(View.GONE);
    }

    private void initSeries()
    {
        assert getArguments() != null;
        ChangeNotificationReminderFragmentArgs args = ChangeNotificationReminderFragmentArgs.fromBundle(getArguments());
        series = args.getSeries();
    }

    private void updateNewAirDate()
    {
        ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
        // Get date in calendar form (so we can change it)
        Calendar calendar = convertDateToCalendar.timeZoneConvert(getChangedAirDate());

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

        // Update text
        String newDate = convertDateToCalendar.reverseConvert(calendar);
        String notificationChange = quantity + " " + metric + " " + beforeAfter + "\nMeaning you will be notified on: " + newDate;
        newChangeTV.setText(notificationChange);

    }

    private void updateNotificationChangeDB()
    {
        UpdateNotificationReminderAsync updateNotificationReminderAsync = new UpdateNotificationReminderAsync();
        updateNotificationReminderAsync.execute();
    }

    private class UpdateNotificationReminderAsync extends AsyncTask<Void, Void, Void>
    {
        private boolean isSuccessful;

        @Override
        protected Void doInBackground(Void... voids)
        {
            String change;
            if(quantity==0) change = "";
            else
            {
                change = quantity + " " + metric + " " + beforeAfter;
            }

            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Account", Context.MODE_PRIVATE);
            String session = sharedPreferences.getString("session", "");

            UpdateNotificationChange updateNotificationChange = new UpdateNotificationChange(session, series.getAnilist_id(), change, getContext());
            isSuccessful = updateNotificationChange.update() == 0;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            String title = series.getTitle();
            if(isSuccessful)
            {
                if(quantity==0)
                {
                    Toast.makeText(getContext(), "You have changed to be reminded the moment a " + "\"" + title +"\" episode releases!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getContext(), "You have changed to be reminded\n" + quantity + " " + metric + " " + beforeAfter +  "\n\"" + title +"\"'s air date!", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getContext(), "Failed to change notification reminder for \"" + title +"\", report this bug.", Toast.LENGTH_LONG).show();
            }
            navController.navigateUp();
            super.onPostExecute(aVoid);
        }
    }
}