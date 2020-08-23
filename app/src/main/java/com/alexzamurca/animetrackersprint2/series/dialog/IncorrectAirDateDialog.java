package com.alexzamurca.animetrackersprint2.series.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alexzamurca.animetrackersprint2.R;

import java.io.Serializable;

public class IncorrectAirDateDialog extends DialogFragment
{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_wrong_air_date, container, false);
        setCancelable(false);

        IncorrectAirDateListener listener = (IncorrectAirDateListener)getArguments().getSerializable("incorrectAirDateListener");

        Button changeTimeZone = view.findViewById(R.id.wrong_air_date_change_time_zone_button);
        changeTimeZone.setOnClickListener(v ->
        {
            listener.OnChangeTimeZoneClick();
            dismiss();
        });

        Button changeAirDate = view.findViewById(R.id.wrong_air_date_change_air_date_button);
        changeAirDate.setOnClickListener(v ->
        {
            listener.OnChangeAirDateClick();
            dismiss();
        });

        ImageButton closeButton = view.findViewById(R.id.wrong_air_date_close);
        closeButton.setOnClickListener(v -> dismiss());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public interface IncorrectAirDateListener extends Serializable
    {
        void OnChangeTimeZoneClick();
        void OnChangeAirDateClick();
    }
}