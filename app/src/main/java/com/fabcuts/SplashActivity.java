package com.fabcuts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        //editor=sharedPreferences.edit();
        delayEnter();
    }

    private void delayEnter() {
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {

                if (sharedPreferences.getBoolean("saloon",false)){
                    startActivity(new Intent(getApplicationContext(),SaloonDashboard.class));
                    finish();
                }
                else if (sharedPreferences.getBoolean("user",false)){
                    startActivity(new Intent(getApplicationContext(),FirstActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                    finish();
                }
                /*   if (sharedPreferences.getBoolean("verified",false)){
                    if (sharedPreferences.getBoolean("login",false)){
                        if (sharedPreferences.getString("postalcode",null)!=null){
                            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                            finish();
                        }
                        else {
                            Toast.makeText(SplashActivity.this, "We need your Location..", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MapsActivity.class)
                            .putExtra("check",0));
                            finish();
                        }
                    }
                    else {
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        finish();
                    }

                }
                else {
                    if (sharedPreferences.getBoolean("login",false)){
                        if (sharedPreferences.getString("postalcode",null)!=null){
                            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                            finish();
                        }
                        else {
                            startActivity(new Intent(getApplicationContext(),MapsActivity.class)
                                    .putExtra("check",0));
                            finish();
                        }
                    }
                    else {
                        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                        finish();
                    }
                }
*/


            }
        }.start();
    }
}
