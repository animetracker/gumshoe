package com.alexzamurca.animetrackersprint2.series.series_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import com.alexzamurca.animetrackersprint2.R;
import com.bumptech.glide.Glide;

public class SeriesInfoFragment extends Fragment
{
    TextView descriptionTV;
    TextView titleTV;
    TextView notificationsOnTV;
    TextView notificationChangeTV;
    TextView airDateChangeTV;
    ImageView coverImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) 
    {
        View view = inflater.inflate(R.layout.fragment_series_info, container, false);

        Toolbar toolbar = view.findViewById(R.id.series_info_toolbar_object);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if(activity.getSupportActionBar()!=null)
        {
            activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle arguments = getArguments();
        if(arguments!=null)
        {
            Series selectedSeries = (Series)arguments.getSerializable("series");

            if(selectedSeries!=null)
            {
                descriptionTV = view.findViewById(R.id.individual_series_description);
                titleTV = view.findViewById(R.id.individual_series_title);
                coverImage = view.findViewById(R.id.individual_series_cover_image);
                notificationsOnTV = view.findViewById(R.id.individual_series_notifications_on);
                notificationChangeTV = view.findViewById(R.id.individual_series_notification_change);
                airDateChangeTV = view.findViewById(R.id.individual_series_air_date_change);

                String title = selectedSeries.getTitle();
                int notifications_on = selectedSeries.getNotifications_on();
                String notification_change = selectedSeries.getNotification_change();
                String air_date_change = selectedSeries.getAir_date_change();
                air_date_change = stringifyAirDateChange(air_date_change);

                descriptionTV.setText(HtmlCompat.fromHtml(selectedSeries.getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                notificationsOnTV.setText(notifications_on == 0 ? "No" : "Yes");
                notificationChangeTV.setText(notification_change.equals("") ? "You will be notified immediately when the series airs!" : "You will be notified " + notification_change + " the series airs");
                airDateChangeTV.setText(air_date_change);
                titleTV.setText(title);

                String image_directory = selectedSeries.getCover_image();
                // Setting the image
                Glide.with(requireContext())
                        .asBitmap()
                        .load(image_directory)
                        .into(coverImage);
            }
        }

        
        
        return view;
    }

    private String stringifyAirDateChange(String air_date_change)
    {
        if(air_date_change.equals("")) return "You are using the air date provided by AniChart, you do not want to adjust to when your streaming service airs this series!";
        char signChar = air_date_change.toCharArray()[0];
        int hoursInt = Integer.parseInt(air_date_change.substring(1, air_date_change.indexOf(':')));
        int minutesInt = Integer.parseInt(air_date_change.substring(air_date_change.indexOf(':') + 1));

        String sign = signChar=='+' ? "added on" : "taken away";
        String hours = hoursInt==0 ? "" : + hoursInt + " hours";
        String minutes = minutesInt==0 ? "" : minutesInt + " minutes";

        return "You have " + sign + " " + hours + ( (!minutes.equals("") && !hours.equals("")) ? " and " : "") + minutes + (signChar=='+' ? " to" : " from") + " the air date provided by AniChart.";
    }
}