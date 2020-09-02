package com.alexzamurca.animetrackersprint2.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.alexzamurca.animetrackersprint2.R;


public class dialog_report_bug extends DialogFragment {

    Button reportBugButton;
    ImageButton closeDialogButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View mView = inflater.inflate(R.layout.fragment_dialog_report_bug, container, false);

        reportBugButton = mView.findViewById(R.id.reportBugButton);
        closeDialogButton = (ImageButton) mView.findViewById(R.id.closeDialogButton);

        reportBugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDiscord();
            }
        });

        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return mView;
    }

    public void openDiscord() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/s2C8eJ2"));
        startActivity(intent);
    }

}