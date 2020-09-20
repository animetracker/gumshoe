package com.alexzamurca.animetrackersprint2.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.alexzamurca.animetrackersprint2.MainActivity;
import com.alexzamurca.animetrackersprint2.R;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;

    private SliderAdapter sliderAdapter;

    private Button mNextBtn;
    private Button mBackBtn;

    private TextView[] mDots;

    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        mSlideViewPager = findViewById(R.id.slideViewPager);

        mBackBtn = findViewById(R.id.prevBtn);
        mNextBtn = findViewById(R.id.nextBtn);

        mDots= new TextView[7];

        SliderAdapter sliderAdapter = new SliderAdapter(this);

        mSlideViewPager.setAdapter(sliderAdapter);

        mSlideViewPager.addOnPageChangeListener(viewListener);

        //onClickListener
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentPage==mDots.length-1){
                    openMain();
                }
                else {
                    mSlideViewPager.setCurrentItem(mCurrentPage+1);
                }
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlideViewPager.setCurrentItem(mCurrentPage-1);
            }
        });
    }


    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int i) {
            mCurrentPage = i;

            if (i==0){
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(false);
                mBackBtn.setVisibility(View.INVISIBLE);

                mNextBtn.setText("Next");
                mBackBtn.setText("");
            }
            else if(i==mDots.length-1){
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText("Finish");
                mBackBtn.setText("Back");
            }
            else {
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText("Next");
                mBackBtn.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
    public void openMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

