package com.alexzamurca.animetrackersprint2.tutorial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.alexzamurca.animetrackersprint2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    // insert screenshots here
    public int[] slide_images = {
            R.drawable.tutorial_list_empty,
            R.drawable.tutorial_search,
            R.drawable.tutorial_search_expand,
            R.drawable.tutorial_list_added,
            R.drawable.tutorial_list_added,
            R.drawable.tutorial_list_added,
            R.drawable.tutorial_list_added,
            R.drawable.tutorial_settings,
            R.drawable.tutorial_settings,
            R.drawable.tutorial_settings
    };

    public String[] slide_text = {
            "<h3>Press the blue plus button to search for series</h3>",
            "Search for an anime that is <u>currently airing</u> (or soon to be released)<h3>Click on the anime to add it</h3>",
            "<h3>Press the down arrow</h3>to get more info about the anime.",
            "<h3>Press the bell icon</h3>to turn <u>notifications on or off</u> for that anime (by default notifications are on).",
            "<h3>Press the bin icon</h3>to <u>remove</u> that anime from the list.",
            "<h3>Press the hourglass icon</h3>to <u>change when you want to be notified</u> (for example, be notified 30 minutes before the next episode releases).",
            "<h3>Press the error icon</h3>if the suggested <u>air date is different</u> to when your streaming services releases this anime (you manually change the air date).",
            "<h3>Customise</h3>You can turn <u>dark mode</u> on/off and change the profile icon.",
            "<h3>Support the team</h3>Donate and watch ads to support us. Use the about page to get more info about the team.",
            "Use the report button to let us know of any issues.<br>You can replay this tutorial anytime from settings. Enjoy :)"
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
        slideText.setText(HtmlCompat.fromHtml(slide_text[position], HtmlCompat.FROM_HTML_MODE_LEGACY));

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
