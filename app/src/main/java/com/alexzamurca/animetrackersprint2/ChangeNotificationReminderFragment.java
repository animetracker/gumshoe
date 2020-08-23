package com.alexzamurca.animetrackersprint2;

import android.os.Bundle;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class ChangeNotificationReminderFragment extends Fragment
{
    private static final String TAG = "ChangeNotificationReminderFragment";

    private NavController navController;
    private TextView changeTV;
    private TextView newChangeTV;
    private FloatingActionButton saveButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_change_notification_reminder, container, false);

        // identify toolbar object
        Toolbar toolbar = view.findViewById(R.id.change_notification_reminder_toolbar_object);

        // Set toolbar and add home (back button)
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set text views and button
        changeTV = view.findViewById(R.id.change_notification_reminder_change);
        newChangeTV = view.findViewById(R.id.change_notification_reminder_new_change);
        saveButton = view.findViewById(R.id.change_notification_reminder_save_button);
        saveButton.setVisibility(View.GONE);

        // Identify layout that we need to hide
        LinearLayout hiddenLayout = view.findViewById(R.id.change_notification_reminder_hidden_layout);
        hiddenLayout.setVisibility(View.GONE);

        Button yesButton = view.findViewById(R.id.change_notification_reminder_button_yes);
        yesButton.setOnClickListener(v -> navController.navigateUp());

        Button noButton = view.findViewById(R.id.change_notification_reminder_button_no);
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

        setupSpinners(view);

        setupSaveButton();

        navController = Navigation.findNavController(view);
    }

    private void setupSpinners(View view)
    {
        setupMetricSpinner(view);
        setupBeforeAfterSpinner(view);
    }

    private void setupMetricSpinner(View view)
    {
        Spinner spinner = view.findViewById(R.id.change_notification_reminder_metric_spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectionString = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: metric selected:" + selectionString);
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectionString = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: before/after selected:" + selectionString);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void setupSaveButton()
    {
        saveButton.setOnClickListener(v ->
        {

            Toast.makeText(getContext(), "Your changes have been saved!", Toast.LENGTH_LONG).show();
            navController.navigateUp();
        });
    }
}