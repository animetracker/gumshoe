package com.alexzamurca.animetrackersprint2.dialog;

import android.content.Intent;
import android.net.Uri;
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

public class NoDatabaseDialog extends DialogFragment
{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_database_error, container, false);

        Button reportBugButton = view.findViewById(R.id.report_bug_button);
        reportBugButton.setOnClickListener(v ->
                {
                    dialog_report_bug dialogReportBug = new dialog_report_bug();
                    dialogReportBug.show(requireActivity().getSupportFragmentManager(), "dialog_report_button");
                    openDiscord();
                }

        );

        ImageButton closeButton = view.findViewById(R.id.close_image_button);
        closeButton.setOnClickListener(v -> dismiss());
        return view;
    }

    public void openDiscord() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/s2C8eJ2"));
        startActivity(intent);
    }
}