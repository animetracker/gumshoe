package com.alexzamurca.animetrackersprint2.series.series_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        
        Bundle arguments = getArguments();
        Series selectedSeries = (Series)arguments.getSerializable("series");
        descriptionTV = view.findViewById(R.id.individual_series_description);
        titleTV = view.findViewById(R.id.individual_series_title);
        coverImage = view.findViewById(R.id.individual_series_cover_image);

        descriptionTV.setText(selectedSeries.getDescription());
        titleTV.setText(selectedSeries.getTitle());

        String image_directory = selectedSeries.getCover_image();
        // Setting the image
        Glide.with(getContext())
                .asBitmap()
                .load(image_directory)
                .into(coverImage);
        
        
        return view;
    }
}