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

public class NotificationsOffDialog extends DialogFragment
{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_notifications_off, container, false);
        setCancelable(false);

        assert getArguments() != null;
        Series series  = (Series) getArguments().getSerializable("series");

        TextView explanation = view.findViewById(R.id.notifications_off_explanation);
        assert series != null;
        explanation.setText(buildExplanation(series));

        Button yesButton = view.findViewById(R.id.notifications_off_yes_button);
        yesButton.setOnClickListener(v ->
        {
            // Communicate with database and update Notification_off state of series
            dismiss();
        });

        Button noButton = view.findViewById(R.id.notifications_off_no_button);
        noButton.setOnClickListener(v ->
        {
            dismiss();
        });

        ImageButton closeButton = view.findViewById(R.id.notifications_off_close);
        closeButton.setOnClickListener(v -> dismiss());
        return view;
    }

    private String buildExplanation(Series series)
    {
        // format: "It appears, you do not want to receive notifications for when a {series_name} episode is airing! Notifications can be turned back on in the future."
        return "It appears, you do not want to receive notifications for when a \"" + series.getTitle() + "\"  episode is airing! Notifications can be turned back on in the future.";
    }
}