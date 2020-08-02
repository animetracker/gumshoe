package com.alexzamurca.animetrackersprint2.series.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alexzamurca.animetrackersprint2.R;

public class NoDatabaseDialog extends DialogFragment
{
    private ImageButton closeButton;
    private Button reportBugButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_database_error, container, false);
        setCancelable(false);

        reportBugButton = view.findViewById(R.id.report_bug_button);
        reportBugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "<Engrish Accent>\nThis no working now men, but not to worry it will work.. at some point.\n</Engrish Accent>", Toast.LENGTH_SHORT).show();
            }
        });

        closeButton = view.findViewById(R.id.close_image_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });


        return view;
    }
}