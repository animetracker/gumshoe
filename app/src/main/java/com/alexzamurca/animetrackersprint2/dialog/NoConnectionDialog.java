package com.alexzamurca.animetrackersprint2.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.algorithms.CheckConnection;
import com.alexzamurca.animetrackersprint2.algorithms.UpdateDB;
import com.alexzamurca.animetrackersprint2.notifications.UpdateFailedNotification;

public class NoConnectionDialog extends DialogFragment
{

    private static final String TAG = "NoConnectionDialog";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dialog_no_connection, container, false);
        boolean needToUpdate = false;

        Window window = requireDialog().getWindow();
        if(window!=null)
        {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // GET BOOLEAN IF TO UPDATE_DB
        Bundle args = getArguments();
        if(args!=null)
        {
            needToUpdate = args.getBoolean("update_db");
            Log.d(TAG, "onCreateView: try again listener needs to update db on click");
        }

        Button tryAgainButton = view.findViewById(R.id.try_again_button);
        boolean finalNeedToUpdate = needToUpdate;
        tryAgainButton.setOnClickListener(v -> {
            CheckConnection checkConnection = new CheckConnection(getContext());
            boolean isConnected = checkConnection.isConnected();
            if (isConnected) {
                // put your online code here
                Toast.makeText(getContext(), "Network connection is now available", Toast.LENGTH_SHORT).show();
                if(finalNeedToUpdate)
                {
                    UpdateDB updateDB = new UpdateDB(requireContext());
                    updateDB.run();
                }

                requireDialog().dismiss();
            }
            else
            {
                Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();

                if(finalNeedToUpdate)
                {
                    UpdateFailedNotification updateFailedNotification = new UpdateFailedNotification(requireContext());
                    updateFailedNotification.showNotification();

                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("App", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putBoolean("need_to_update_db", true);
                    Log.d(TAG, "OnCreateView: app set to need_t mode");
                    editor.apply();
                }
            }
        });


        setCancelable(false);

        return view;
    }
}
