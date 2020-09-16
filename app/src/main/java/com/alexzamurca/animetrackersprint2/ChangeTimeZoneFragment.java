package com.alexzamurca.animetrackersprint2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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

import com.alexzamurca.animetrackersprint2.algorithms.ResetAlarmForSeries;
import com.alexzamurca.animetrackersprint2.Database.SelectTable;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ChangeTimeZoneFragment extends Fragment
{
    private static final String TAG = "ChangeTimeZoneFragment";

    private boolean isSignNegative = false;
    private int hours_to_change = 0;
    private int minutes_to_change = 0;

    private Handler oldHandler;
    private Handler newHandler;
    private Runnable oldRunnable;
    private Runnable newRunnable;

    private NavController navController;
    private TextView timeZoneTV;
    private TextView timeTV;
    private TextView newTimeTV;
    private FloatingActionButton saveButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_change_time_zone, container, false);

        // identify toolbar object
        Toolbar toolbar = view.findViewById(R.id.change_time_zone_toolbar_object);

        // Set toolbar and add home (back button)
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if(activity.getSupportActionBar()!=null)
        {
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        // Set text views and button
        newTimeTV = view.findViewById(R.id.change_time_zone_time_new_time);
        timeZoneTV = view.findViewById(R.id.change_time_zone_time_identified_zone);
        timeTV = view.findViewById(R.id.change_time_zone_time_identified_time);
        saveButton = view.findViewById(R.id.change_time_zone_save_button);
        saveButton.setVisibility(View.GONE);

        // Identify layout that we need to hide
        LinearLayout hiddenLayout = view.findViewById(R.id.change_time_zone_hidden_layout);
        hiddenLayout.setVisibility(View.GONE);

        Button yesButton = view.findViewById(R.id.change_time_zone_button_yes);
        yesButton.setOnClickListener(v -> navController.navigateUp());

        Button noButton = view.findViewById(R.id.change_time_zone_button_no);
        noButton.setOnClickListener(v ->
                {
                    hiddenLayout.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                    noButton.setBackgroundResource(R.drawable.greyish_white_with_stroke);
                    yesButton.setVisibility(View.GONE);
                }
        );

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showTimeZoneAndTime();

        setupSpinners(view);

        setupSaveButton();

        navController = Navigation.findNavController(view);
    }

    // Stops time showing threads
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        oldHandler.removeCallbacks(oldRunnable);
        newHandler.removeCallbacks(newRunnable);
    }

    // DONT FORGET TO IMPLEMENT SHARED PREFERENCES FOR TIMEZONE
    private void showTimeZoneAndTime()
    {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("time_zone", Context.MODE_PRIVATE);

        boolean negative_sign = sharedPreferences.getBoolean("is_sign_negative", false);
        int hours_change = sharedPreferences.getInt("hours_to_change", 0);
        int minutes_change = sharedPreferences.getInt("minutes_to_change", 0);
        Log.d(TAG, "showTimeZoneAndTime: hours:" + hours_change);
        Log.d(TAG, "showTimeZoneAndTime: minutes:" + minutes_change);

        // Find time zone
        TimeZone timeZone = TimeZone.getDefault();
        boolean daylight = timeZone.inDaylightTime(new Date());
        String timeZoneText = timeZone.getDisplayName(daylight, TimeZone.LONG);
        if(!(hours_change==0 && minutes_change==0))
        {
            timeZoneText += " (and ";
            if(negative_sign)
            {
                timeZoneText += "-";
            }
            else
            {
                timeZoneText += "+";
            }
            timeZoneText += Integer.toString(hours_change);
            timeZoneText += ":";
            timeZoneText += Integer.toString(minutes_change);
            timeZoneText += ")";
        }
        timeZoneTV.setText(timeZoneText);
        // Set texts
        setupOldTimeTV(timeZone);
        setupNewTimeTV(timeZone);
    }

    private Calendar adjustLocalTimeZoneChanges(Calendar calendar)
    {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("time_zone", Context.MODE_PRIVATE);

        boolean negative_sign = sharedPreferences.getBoolean("is_sign_negative", false);
        int hours_change = sharedPreferences.getInt("hours_to_change", 0);
        int minutes_change = sharedPreferences.getInt("minutes_to_change", 0);

        // Add hours, minutes
        if(negative_sign)
        {
            calendar.add(Calendar.HOUR_OF_DAY, -hours_change);
            calendar.add(Calendar.MINUTE, -minutes_change);
        }
        else
        {
            calendar.add(Calendar.HOUR_OF_DAY, +hours_change);
            calendar.add(Calendar.MINUTE, +minutes_change);
        }

        return calendar;
    }

    private String constructOldDateString(TimeZone timeZone)
    {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar = adjustLocalTimeZoneChanges(calendar);
        return String.format("%02d" , calendar.get(Calendar.HOUR_OF_DAY))+":"+
                String.format("%02d" , calendar.get(Calendar.MINUTE))+" "+
                String.format("%02d" , calendar.get(Calendar.DAY_OF_MONTH))+"/"+
                String.format("%02d" , calendar.get(Calendar.MONTH)) +"/"+
                String.format("%02d" , calendar.get(Calendar.YEAR));
    }

    private String constructNewDateString(TimeZone timeZone)
    {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar = adjustLocalTimeZoneChanges(calendar);
        // Add hours, minutes
        if(isSignNegative)
        {
            calendar.add(Calendar.HOUR_OF_DAY, -hours_to_change);
            calendar.add(Calendar.MINUTE, -minutes_to_change);
        }
        else
        {
            calendar.add(Calendar.HOUR_OF_DAY, +hours_to_change);
            calendar.add(Calendar.MINUTE, +minutes_to_change);
        }

        return String.format("%02d" , calendar.get(Calendar.HOUR_OF_DAY))+":"+
                String.format("%02d" , calendar.get(Calendar.MINUTE))+" "+
                String.format("%02d" , calendar.get(Calendar.DAY_OF_MONTH))+"/"+
                String.format("%02d" , calendar.get(Calendar.MONTH)) +"/"+
                String.format("%02d" , calendar.get(Calendar.YEAR));
    }

    private void setupSpinners(View view)
    {
        setupSignSpinner(view);
        setupHoursSpinner(view);
        setupMinutesSpinner(view);
    }

    private void setupSignSpinner(View view)
    {
        Spinner spinner = view.findViewById(R.id.time_zone_sign_spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectionString = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: sign selected:" + selectionString);

                if(selectionString.equals("+"))
                {
                    isSignNegative = false;
                }
                if(selectionString.equals("-"))
                {
                    isSignNegative = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void setupHoursSpinner(View view)
    {
        Spinner spinner = view.findViewById(R.id.time_zone_hours_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectionString = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: hours selected:" + selectionString);

                hours_to_change = Integer.parseInt(selectionString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void setupMinutesSpinner(View view)
    {
        Spinner spinner = view.findViewById(R.id.time_zone_minutes_spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectionString = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: minutes selected:" + selectionString);

                minutes_to_change = Integer.parseInt(selectionString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void setupOldTimeTV(TimeZone timeZone)
    {
        oldHandler = new Handler();
        oldRunnable = new Runnable() {
            @Override
            public void run() {
                timeTV.setText(constructOldDateString(timeZone));
                oldHandler.postDelayed(this, 1000);
            }
        };
        oldHandler.postDelayed(oldRunnable, 10);
    }

    private void setupNewTimeTV(TimeZone timeZone)
    {
        newHandler = new Handler();
        newRunnable = new Runnable() {
            @Override
            public void run() {
                    newTimeTV.setText(constructNewDateString(timeZone));
                newHandler.postDelayed(this, 1000);

            }
        };
        newHandler.postDelayed(newRunnable, 10);
    }

    private void setupSaveButton()
    {
        saveButton.setOnClickListener(v ->
        {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("time_zone", Context.MODE_PRIVATE);

            // Get the previous so we can store the difference not just the new
            boolean negative_sign = sharedPreferences.getBoolean("is_sign_negative", false);
            int hours_change = sharedPreferences.getInt("hours_to_change", 0);
            int minutes_change = sharedPreferences.getInt("minutes_to_change", 0);

            if(hasTimeZoneChanged(negative_sign, hours_change, minutes_change))
            {
                JSONObject json = findDifference(negative_sign, hours_change, minutes_change);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                try
                {
                    editor.putBoolean("is_sign_negative", json.getBoolean("is_sign_negative"));
                    editor.putInt("hours_to_change", json.getInt("hours_to_change"));
                    editor.putInt("minutes_to_change", json.getInt("minutes_to_change"));
                }
                catch (JSONException e)
                {
                    Log.d(TAG, "setupSaveButton: JSON exception when trying to get sign/hours/minutes from formed JSON");
                }

                editor.apply();

                getTableAndResetAlarms();

                Toast.makeText(getContext(), "Your changes have been saved!", Toast.LENGTH_LONG).show();
            }

            navController.navigateUp();
        });
    }

    private JSONObject findDifference(boolean negative_sign, int hours_change, int minutes_change)
    {
        // Convert sign, hours, minute to double (for example -,1,30 will be converted to -1.5; +,3,15 will be converted to 3.25
        double oldChange, newChange;

        // work out oldChange
        if(negative_sign)
        {
            oldChange = -hours_change - ((double)minutes_change/60);
        }
        else
        {
            oldChange = hours_change + ((double)minutes_change/60);
        }

        // work out newChange
        if(isSignNegative)
        {
            newChange = -hours_to_change - ((double)minutes_to_change/60);
        }
        else
        {
            newChange = hours_to_change + ((double)minutes_to_change/60);
        }

        double difference = newChange + oldChange;

        String doubleAsString = String.valueOf(difference);
        int indexOfDecimal = doubleAsString.indexOf(".");
        int integerPartOfDouble = Integer.parseInt(doubleAsString.substring(0, indexOfDecimal));
        boolean sign = -1==Integer.signum(integerPartOfDouble);
        int hours = Math.abs(integerPartOfDouble);
        int minutes = (int) (Double.parseDouble(doubleAsString.substring(indexOfDecimal)) * 60);

        // Form JSON
        JSONObject json = new JSONObject();
        try
        {
            json.put("is_sign_negative", sign);
            json.put("hours_to_change", hours);
            json.put("minutes_to_change", minutes);
        }
        catch(JSONException e)
        {
            Log.d(TAG, "findDifference: json exception when trying to form sign/hours/minutes json");
        }
        return json;
    }

    private boolean hasTimeZoneChanged(boolean negative_sign, int hours_change, int minutes_change)
    {
        return !(negative_sign==isSignNegative && hours_change == hours_to_change && minutes_change == minutes_to_change);
    }

    private void getTableAndResetAlarms()
    {
        GetTableAsync getTableAsync = new GetTableAsync();
        getTableAsync.execute();
    }

    private void resetAllAlarms(List<Series> currentList)
    {
        ResetAlarmForSeries resetAlarmForSeries = new ResetAlarmForSeries(getContext());
        for(int i = 0; i < currentList.size(); i++)
        {
            resetAlarmForSeries.reset(currentList.get(i));
        }
    }

    public class GetTableAsync extends AsyncTask<Void, Void, Void>
    {
        private boolean wasRequestSuccessful;
        private ArrayList<Series> list;

        @Override
        protected Void doInBackground(Void... voids)
        {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Account", Context.MODE_PRIVATE);
            String session = sharedPreferences.getString("session", "");
            SelectTable selectTable = new SelectTable(session, getContext());
            list = selectTable.getSeriesList();
            wasRequestSuccessful = selectTable.getWasRequestSuccessful();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            if(wasRequestSuccessful)
            {
                resetAllAlarms(list);
            }

            super.onPostExecute(aVoid);
        }
    }
}