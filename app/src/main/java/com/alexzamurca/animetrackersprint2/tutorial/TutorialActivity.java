package com.alexzamurca.animetrackersprint2.tutorial;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.alexzamurca.animetrackersprint2.MainActivity;
import com.alexzamurca.animetrackersprint2.R;
import com.alexzamurca.animetrackersprint2.login.LoginActivity;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;

    int currentPosition;
    boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mSlideViewPager = findViewById(R.id.slideViewPager);

        SliderAdapter sliderAdapter = new SliderAdapter(this);

        mSlideViewPager.setAdapter(sliderAdapter);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        firstTime = getIntent().getBooleanExtra("first_time", false);
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            currentPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public void goBack(View view) {
        mSlideViewPager.setCurrentItem(currentPosition + 1);
    }

    public void goForward(View view) {
        mSlideViewPager.setCurrentItem(currentPosition - 1);
    }

    public void finish(View view) {
        openMain();
    }

    public void openMain()
    {
        if(firstTime)
        {
            SharedPreferences sharedPreferences = getSharedPreferences("Account", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("logged_in", false);
            editor.putString("session", "");
            editor.putBoolean("has_session_expired", false);
            editor.apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        TutorialActivity.this.finish();

    }
}