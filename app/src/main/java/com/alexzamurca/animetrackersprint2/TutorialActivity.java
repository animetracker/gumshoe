package com.alexzamurca.animetrackersprint2;

import android.Manifest;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    //private LinearLayout mDotLayout;

    private SliderAdapter sliderAdapter;

    //private TextView[] mDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        //mDotLayout = (LinearLayout) findViewById(R.id.dotLayout);
        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);

        sliderAdapter = new SliderAdapter(this);

        mSlideViewPager.setAdapter(sliderAdapter);

        //addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);
    }

    /*
    public void addDotsIndicator(int k) {

        mDots = new TextView[7];
        mDotLayout.removeAllViews();

        for(int  i = 0; i < mDots.length; i++) {
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length > 0) {
            mDots[k].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }
*/

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
           // addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
}