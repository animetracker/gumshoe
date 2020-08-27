package com.alexzamurca.animetrackersprint2.series.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.series.series_list.Series;

import java.io.Serializable;

public class IncorrectAirDateDialog extends DialogFragment
{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_wrong_air_date, container, false);
        setCancelable(false);

        assert getArguments() != null;
        IncorrectAirDateListener listener = (IncorrectAirDateListener)getArguments().getSerializable("incorrectAirDateListener");
        assert listener != null;
        Series series  = (Series) getArguments().getSerializable("series");
        assert series != null;

        TextView explanation = view.findViewById(R.id.wrong_air_date_explanation);

        explanation.setText(buildExplanation(series));

        Button changeTimeZone = view.findViewById(R.id.wrong_air_date_change_time_zone_button);
        changeTimeZone.setOnClickListener(v ->
        {

            listener.OnChangeTimeZoneClick();
            dismiss();
        });

        Button changeAirDate = view.findViewById(R.id.wrong_air_date_change_air_date_button);
        changeAirDate.setOnClickListener(v ->
        {
            listener.OnChangeAirDateClick(series);
            dismiss();
        });

        ImageButton closeButton = view.findViewById(R.id.wrong_air_date_close);
        closeButton.setOnClickListener(v -> dismiss());
        return view;
    }

    private String buildExplanation(Series series)
    {
        // format: "It appears, the air date we have \n for {series_name} is incorrect!"
        return "It appears, the air date we have \n for \"" + series.getTitle() + "\"  is incorrect!";
    }

    public interface IncorrectAirDateListener extends Serializable
    {
        void OnChangeTimeZoneClick();
        void OnChangeAirDateClick(Series series);
    }
}