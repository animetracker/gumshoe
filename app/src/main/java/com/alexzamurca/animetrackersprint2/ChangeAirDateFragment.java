package com.alexzamurca.animetrackersprint2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class ChangeAirDateFragment extends Fragment
{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) 
    {
        View view = inflater.inflate(R.layout.fragment_change_air_date, container, false);

        // identify toolbar object
        Toolbar toolbar = view.findViewById(R.id.change_air_date_toolbar_object);

        // Set toolbar and add home (back button)
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return view;
    }
}