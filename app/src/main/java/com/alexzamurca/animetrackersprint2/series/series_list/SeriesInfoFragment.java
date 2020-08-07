package com.alexzamurca.animetrackersprint2.series.series_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.alexzamurca.animetrackersprint2.R;
import com.bumptech.glide.Glide;

public class SeriesInfoFragment extends Fragment
{
    TextView descriptionTV;
    TextView titleTV;
    ImageView coverImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) 
    {
        View view = inflater.inflate(R.layout.fragment_series_info, container, false);

        Toolbar toolbar = view.findViewById(R.id.series_info_toolbar_object);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle arguments = getArguments();
        Series selectedSeries = (Series)arguments.getSerializable("series");
        descriptionTV = view.findViewById(R.id.individual_series_description);
        titleTV = view.findViewById(R.id.individual_series_title);
        coverImage = view.findViewById(R.id.individual_series_cover_image);

        String title = selectedSeries.getTitle();

        descriptionTV.setText(selectedSeries.getDescription());
        titleTV.setText(title);

        String image_directory = selectedSeries.getCover_image();
        // Setting the image
        Glide.with(getContext())
                .asBitmap()
                .load(image_directory)
                .into(coverImage);
        
        
        return view;
    }
}