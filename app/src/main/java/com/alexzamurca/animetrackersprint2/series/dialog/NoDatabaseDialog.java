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

public class NoDatabaseDialog extends DialogFragment
{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_database_error, container, false);
        setCancelable(false);

        ReportBugListener reportBugListener = (ReportBugListener)getArguments().getSerializable("reportBugListener");

        Button reportBugButton = view.findViewById(R.id.report_bug_button);
        reportBugButton.setOnClickListener(v ->
        {
            reportBugListener.OnReportBugButtonClick();
            dismiss();
        });

        ImageButton closeButton = view.findViewById(R.id.close_image_button);
        closeButton.setOnClickListener(v -> dismiss());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public interface ReportBugListener extends Serializable
    {
        void OnReportBugButtonClick();
    }
}