package com.fabcuts;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class HowToUse extends AppCompatActivity {

    private static ViewPager viewPager;

    ActionBar actionBar;
    private static int currentPage = 0;
    private static final Integer[] welcomeImage = {R.drawable.one, R.drawable.two, R.drawable.three,R.drawable.four,R.drawable.five};
    private ArrayList<Integer> WelcomeArray = new ArrayList<Integer>();
    Button skip;
    Boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_use);

        actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();
        init();
        skip=findViewById(R.id.bskip);
        check=getIntent().getBooleanExtra("check",false);

            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HowToUse.this,
                            FirstActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

    }

    private void init() {
        WelcomeArray.addAll(Arrays.asList(welcomeImage));

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyAdapter(HowToUse.this, WelcomeArray));

        // Auto start of viewpager
       /* final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == welcomeImage.length) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2500, 2500);*/
    }
}
