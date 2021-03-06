package com.alexzamurca.animetrackersprint2.dialog;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.alexzamurca.animetrackersprint2.R;


public class ReportBugFragment extends DialogFragment {

    Button reportBugButton;
    ImageButton closeDialogButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View mView = inflater.inflate(R.layout.dialog_report_bug, container, false);

        Window window = requireDialog().getWindow();
        if(window!=null)
        {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


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