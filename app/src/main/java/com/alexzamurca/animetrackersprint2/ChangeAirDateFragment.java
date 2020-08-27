package com.alexzamurca.animetrackersprint2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alexzamurca.animetrackersprint2.Date.ConvertDateToCalendar;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Objects;

public class ChangeAirDateFragment extends Fragment
{
    private static final String TAG = "ChangeAirDateFragment";

    private boolean isSignNegative = false;
    private int hours_to_change = 0;
    private int minutes_to_change = 0;

    private ConvertDateToCalendar convertDateToCalendar;
    private Series series;
    private Calendar oldTimeCalendar;
    private TextView newTimeTV;
    private FloatingActionButton saveButton;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) 
    {
        return inflater.inflate(R.layout.fragment_change_air_date, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initSeries();
        convertDateToCalendar = new ConvertDateToCalendar();
        oldTimeCalendar = convertDateToCalendar.convert(series.getAir_date());
        navController = Navigation.findNavController(view);
        setupToolbar(view);
        setupTextViews(view);
        setupCalendarButtons(view);
        setupSaveButton(view);
        setupHiddenLayoutAndButtons(view);
        setupSpinners(view);
    }

    private void setupToolbar(View view)
    {
        // identify toolbar object
        Toolbar toolbar = view.findViewById(R.id.change_air_date_toolbar_object);

        // Set toolbar and add home (back button)
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupTextViews(View view)
    {
        String header = "This is the Air Date we identified for\n\"" + series.getTitle() + "\":";
        TextView identifiedHeader = view.findViewById(R.id.c_air_date_identified_header);
        identifiedHeader.setText(header);

        String oldAirDate = series.getAir_date() + " (24 hour time) (European date format)";
        TextView oldAirDateTV = view.findViewById(R.id.c_air_date_identified_air_date);
        oldAirDateTV.setText(oldAirDate);

        String newHeader = "This is the Air Date you identified for\n\"" + series.getTitle() + "\":";
        TextView identifiedNewHeader = view.findViewById(R.id.c_air_date_identified_new_header);
        identifiedNewHeader.setText(newHeader);

        newTimeTV = view.findViewById(R.id.change_air_time_new_time);
        newTimeTV.setText(oldAirDate);
    }

    private void initSeries()
    {
        assert getArguments() != null;
        ChangeNotificationReminderFragmentArgs args = ChangeNotificationReminderFragmentArgs.fromBundle(getArguments());
        series = args.getSeries();
    }

    private void setupCalendarButtons(View view)
    {
        String anichartURL = "https://anichart.net/airing";
        ImageView anichart = view.findViewById(R.id.anichart_image);
        anichart.setOnClickListener(v ->
            openURL(anichartURL)
        );

        String crunchyrollURL = "https://www.crunchyroll.com/simulcastcalendar";
        ImageView crunchyroll = view.findViewById(R.id.crunchyroll_image);
        crunchyroll.setOnClickListener(v ->
            openURL(crunchyrollURL)
        );

        String livechartURL = "https://www.livechart.me/schedule/tv";
        ImageView livechart = view.findViewById(R.id.livechart_image);
        livechart.setOnClickListener(v ->
            openURL(livechartURL)
        );
    }

    private void setupHiddenLayoutAndButtons(View view)
    {
        // Identify layout that we need to hide
        LinearLayout hiddenLayout = view.findViewById(R.id.change_air_date_hidden_layout);
        hiddenLayout.setVisibility(View.GONE);


        Button yesButton = view.findViewById(R.id.change_air_date_button_yes);
        yesButton.setOnClickListener(v -> navController.navigateUp());

        Button noButton = view.findViewById(R.id.change_air_date_button_no);
        noButton.setOnClickListener(v ->
                {
                    hiddenLayout.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                    noButton.setBackgroundResource(R.drawable.greyish_white_with_stroke);
                    yesButton.setVisibility(View.GONE);
                }
        );
    }

    private void setupSaveButton(View view)
    {
        saveButton = view.findViewById(R.id.change_air_date_save_button);
    }

    private void openURL(String URL)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(URL));
        requireContext().startActivity(intent);
    }

    private void setupSpinners(View view)
    {
        setupSignSpinner(view);
        setupHoursSpinner(view);
        setupMinutesSpinner(view);
    }

    private void setupSignSpinner(View view)
    {
        Spinner spinner = view.findViewById(R.id.air_date_sign_spinner);

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
                updateNewTimeTV();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void setupHoursSpinner(View view)
    {
        Spinner spinner = view.findViewById(R.id.air_date_hours_spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectionString = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: hours selected:" + selectionString);

                hours_to_change = Integer.parseInt(selectionString);
                updateNewTimeTV();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void setupMinutesSpinner(View view)
    {
        Spinner spinner = view.findViewById(R.id.air_date_minutes_spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectionString = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: minutes selected:" + selectionString);

                minutes_to_change = Integer.parseInt(selectionString);
                updateNewTimeTV();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void updateNewTimeTV()
    {
        // Form string
        Calendar newCalendar = oldTimeCalendar;
        // Add hours, minutes
        if(isSignNegative)
        {
            newCalendar.add(Calendar.HOUR_OF_DAY, -hours_to_change);
            newCalendar.add(Calendar.MINUTE, -minutes_to_change);
        }
        else
        {
            newCalendar.add(Calendar.HOUR_OF_DAY, +hours_to_change);
            newCalendar.add(Calendar.MINUTE, +minutes_to_change);
        }
        String text = convertDateToCalendar.reverseConvert(newCalendar);
        newTimeTV.setText(text);
    }
}