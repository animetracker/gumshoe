package com.alexzamurca.animetrackersprint2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    // insert screenshots here
    public int[] slide_images = {
            R.drawable.screenshot1,
            R.drawable.screenshot2,
            R.drawable.screenshot3,
            R.drawable.screenshot4,
            R.drawable.screenshot5,
            R.drawable.screenshot6,
            R.drawable.screenshot6
    };

    public String[] slide_text = {
            "By pressing the blue plus button, you can see when YOUR favourite episodes release! You can easily add any airing anime to your own custom list to see when they’ll release their next episode! You'll also get notifications for when your favourite animes next episode is airing.",
            "Once you press the plus button, you can search for any anime you want! You can add this anime to your list by clicking on it, or you can view additional information by pressing the arrow.",
            "Includes the thumbnail, next episode date and more! Your friend tell you about a “good” anime? Find out what it’s really about with a glance! You’ll even see ratings by critics and users so you can make the best choice!",
            "Once you have added your favourite animes to your list, you can do several actions with them. By pressing the first button, you can turn off notifications for that anime. The second button allows you to remove that anime from your list. The third button allows you to choose when you want to be notified for when the next episode is released. The last button allows you to tell us if the release date or time is incorrect.",
            "Overly obsessive with order? Sort your animes in any and EVERY way that YOU want by pressing the sort button in the top right! We even have a favourites bar for those which you can’t miss!",
            "The settings tab gives you a variety of features. Under the customisation tab, you can change to dark mode, change your time zone and alert delay.",
            "Under the general tab, you can report a bug or view our about page, which gives you the contact details of our developers! If you would like to donate to us, you can also do so here. Furthermore, if you enjoy using our app, then you can share it with your friends and family using the share button in the top right."
    };

    public SliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return slide_text.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.scImage);
        TextView slideText = (TextView) view.findViewById(R.id.tutorialText);

        slideImageView.setImageResource(slide_images[position]);
        slideText.setText(slide_text[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
