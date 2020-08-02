package com.alexzamurca.animetrackersprint2.series.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alexzamurca.animetrackersprint2.R;

import java.io.Serializable;

public class NoConnectionDialog extends DialogFragment
{

    private static final String TAG = "NoConnectionDialog";
    private Button tryAgainButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        TryAgainListener tryAgainListener = (TryAgainListener) getArguments().getSerializable("data");

        View view = inflater.inflate(R.layout.dialog_no_connection, container, false);

        tryAgainButton = view.findViewById(R.id.try_again_button);
        tryAgainButton.setOnClickListener(v -> {
            CheckConnection checkConnection = new CheckConnection(getContext());
            boolean isConnected = checkConnection.isConnected();
            if (isConnected) {
                // put your online code here
                Toast.makeText(getContext(), "Network connection is now available", Toast.LENGTH_SHORT).show();
                tryAgainListener.OnSuccessfulClick();
                getDialog().dismiss();
            } else {
                Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            }

        });

        setCancelable(false);


        return view;
    }

    public interface TryAgainListener extends Serializable
    {
        void OnSuccessfulClick();
    }
}
