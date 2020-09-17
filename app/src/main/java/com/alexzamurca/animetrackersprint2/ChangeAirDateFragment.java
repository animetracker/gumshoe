package com.alexzamurca.animetrackersprint2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.alexzamurca.animetrackersprint2.Date.ConvertDateToCalendar;
import com.alexzamurca.animetrackersprint2.algorithms.ResetAlarmForSeries;
import com.alexzamurca.animetrackersprint2.Database.UpdateAirDateChange;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class ChangeAirDateFragment extends Fragment
{
    private static final String TAG = "ChangeAirDateFragment";

    private boolean isSignNegative = false;
    private int hours_to_change = 0;
    private int minutes_to_change = 0;

    private ConvertDateToCalendar convertDateToCalendar;
    private Series series;
    private TextView newTimeTV;
    private FloatingActionButton saveButton;
    private NavController navController;
    private LinearLayout errorLayout;
    private LinearLayout hoursErrorLayout;
    private LinearLayout minutesErrorLayout;

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
        navController = Navigation.findNavController(view);
        setupToolbar(view);
        setupTextViews(view);
        setupCalendarButtons(view);
        setupSaveButton(view);
        setupHiddenLayoutAndButtons(view);
        setupSignSpinner(view);
        setupErrorLayouts(view);
        setupHoursEditText(view);
        setupMinutesEditText(view);
    }

    private void setVariables()
    {
        String air_date_change = series.getAir_date_change();
        if(!air_date_change.equals(""))
        {
            String[] signHoursMinutesArray  = air_date_change.split(":");
            Character sign = air_date_change.toCharArray()[0];
            int hours = Integer.parseInt(signHoursMinutesArray[0].substring(1));
            int minutes = Integer.parseInt(signHoursMinutesArray[1]);

            isSignNegative = sign.equals('-');
            hours_to_change = hours;
            minutes_to_change =  minutes;
        }
    }

    private void setupToolbar(View view)
    {
        // identify toolbar object
        Toolbar toolbar = view.findViewById(R.id.change_air_date_toolbar_object);

        // Set toolbar and add home (back button)
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if(activity.getSupportActionBar()!=null)
        {
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupTextViews(View view)
    {
        String header = "This is the Air Date we identified for\n\"" + series.getTitle() + "\":";
        TextView identifiedHeader = view.findViewById(R.id.c_air_date_identified_header);
        identifiedHeader.setText(header);

        ConvertDateToCalendar convertDateToCalendar = new ConvertDateToCalendar();
        Calendar oldAirDateCalendar = convertDateToCalendar.convert(series.getAir_date());
        String oldAirDate =  convertDateToCalendar.reverseConvert(oldAirDateCalendar) + " (24 hour time)<br><i>*this air date is not affected by notification reminder changes</i>";
        TextView oldAirDateTV = view.findViewById(R.id.c_air_date_identified_air_date);
        oldAirDateTV.setText(HtmlCompat.fromHtml(oldAirDate, HtmlCompat.FROM_HTML_MODE_LEGACY));

        String newHeader = "This is the Air Date you identified for\n\"" + series.getTitle() + "\":";
        TextView identifiedNewHeader = view.findViewById(R.id.c_air_date_identified_new_header);
        identifiedNewHeader.setText(newHeader);

        newTimeTV = view.findViewById(R.id.change_air_time_new_time);

        String oldTitle = "Current Air Date Change for\n\"" + series.getTitle() + "\":";
        TextView changeTitle = view.findViewById(R.id.change_air_date_change_title);
        changeTitle.setText(oldTitle);

        String change = series.getAir_date_change();
        String changeText;
        if(change.equals(""))
        {
            changeText = "There is currently no air date change set for this series, you will be notified at the time specified by AniList!";
        }
        else
        {
            changeText = change;
        }
        TextView changeTV = view.findViewById(R.id.change_air_date_change);
        changeTV.setText(changeText);

    }

    private void initSeries()
    {
        assert getArguments() != null;
        ChangeNotificationReminderFragmentArgs args = ChangeNotificationReminderFragmentArgs.fromBundle(getArguments());
        series = args.getSeries();
        Log.d(TAG, "initSeries: series airdate " + series.getAir_date());

        setVariables();
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
        saveButton.setVisibility(View.GONE);
        saveButton.setOnClickListener(v ->
            {
                if(hasAirDateChangeChanged())
                {
                    updateAirDateDB();

                    String change;
                    if(hours_to_change==0 && minutes_to_change==0) change = "";
                    else
                    {
                        change = getFormattedChange();
                    }

                    series.setAir_date_change(change);

                    ResetAlarmForSeries resetAlarmForSeries = new ResetAlarmForSeries(getContext());
                    resetAlarmForSeries.reset(series);
                }
            }
        );
    }

    private boolean hasAirDateChangeChanged()
    {
        String oldAirDateChange = series.getAir_date_change();
        String newAirDateChange = getFormattedChange();
        return !oldAirDateChange.equals(newAirDateChange);
    }

    private void openURL(String URL)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(URL));
        requireContext().startActivity(intent);
    }

    private void setupSignSpinner(View view)
    {
        Spinner spinner = view.findViewById(R.id.air_date_sign_spinner);

        if(isSignNegative) spinner.setSelection(1);
        else spinner.setSelection(0);

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

    private void setupHoursEditText(View view)
    {
        EditText hoursET = view.findViewById(R.id.air_date_hours_edit_text);
        hoursET.setText(Integer.toString(hours_to_change), TextView.BufferType.NORMAL);
        hoursET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                errorLayout.setVisibility(View.GONE);
                hoursErrorLayout.setVisibility(View.GONE);

                // if the edit text is not empty (cant convert nothing to integer)
                if(!s.toString().equals(""))
                {
                    if(Integer.parseInt(s.toString())>23)
                    {
                        errorLayout.setVisibility(View.VISIBLE);
                        hoursErrorLayout.setVisibility(View.VISIBLE);
                        saveButton.setVisibility(View.GONE);
                    }
                    else
                    {
                        hours_to_change = Integer.parseInt(s.toString());
                        saveButton.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    saveButton.setVisibility(View.GONE);
                }

                updateNewTimeTV();
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }

    private void setupMinutesEditText(View view)
    {
        EditText minutesET = view.findViewById(R.id.air_date_minutes_edit_text);
        minutesET.setText(Integer.toString(minutes_to_change), TextView.BufferType.EDITABLE);
        minutesET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                errorLayout.setVisibility(View.GONE);
                minutesErrorLayout.setVisibility(View.GONE);

                // if the edit text is not empty (cant convert nothing to integer)
                if(!s.toString().equals(""))
                {
                    if(Integer.parseInt(s.toString())>59)
                    {
                        errorLayout.setVisibility(View.VISIBLE);
                        minutesErrorLayout.setVisibility(View.VISIBLE);
                        saveButton.setVisibility(View.GONE);
                    }
                    else
                    {
                        minutes_to_change = Integer.parseInt(s.toString());
                        saveButton.setVisibility(View.VISIBLE);
                    }
                }
                else
                {
                    saveButton.setVisibility(View.GONE);
                }

                updateNewTimeTV();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }



    private void updateNewTimeTV()
    {
        // Form string
        Calendar newCalendar = convertDateToCalendar.convert(series.getAir_date());
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

    private void setupErrorLayouts(View view)
    {
        errorLayout = view.findViewById(R.id.change_air_date_error_layout);
        errorLayout.setVisibility(View.GONE);

        minutesErrorLayout = view.findViewById(R.id.minutes_air_date_warning);
        minutesErrorLayout.setVisibility(View.GONE);

        hoursErrorLayout = view.findViewById(R.id.hours_air_date_warning);
        hoursErrorLayout.setVisibility(View.GONE);
    }

    private void updateAirDateDB()
    {
        UpdateAirDateAsync updateAirDateAsync = new UpdateAirDateAsync();
        updateAirDateAsync.execute();
    }

    private String getFormattedChange()
    {
        String sign;
        if(isSignNegative) sign = "-";
        else sign = "+";
        return sign + hours_to_change + ":"  + minutes_to_change;
    }

    private class UpdateAirDateAsync extends AsyncTask<Void, Void, Void>
    {
        private boolean isSuccessful;

        @Override
        protected Void doInBackground(Void... voids)
        {
            String change;
            if(hours_to_change==0 && minutes_to_change==0) change = "";
            else
            {
                change = getFormattedChange();
            }

            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Account", Context.MODE_PRIVATE);
            String session = sharedPreferences.getString("session", "");

            UpdateAirDateChange updateAirDateChange = new UpdateAirDateChange(session, series.getAnilist_id(), change, getContext());
            isSuccessful = updateAirDateChange.update() == 0;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            String title = series.getTitle();
            if(isSuccessful)
            {
                if(hours_to_change==0 && minutes_to_change==0)
                {
                    Toast.makeText(getContext(), "You have changed to use the air date provided by AniList for " + "\"" + title +"\"!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getContext(), "You have changed the air date of" +  "\n\"" + title +"\"!", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(getContext(), "Failed to change air date for \"" + title +"\", report this bug.", Toast.LENGTH_LONG).show();
            }
            navController.navigateUp();
            super.onPostExecute(aVoid);
        }
    }
}