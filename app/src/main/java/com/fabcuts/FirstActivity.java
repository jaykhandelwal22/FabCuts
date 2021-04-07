package com.fabcuts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class FirstActivity extends AppCompatActivity {

    ActionBar actionBar;
    ImageView imageView;
    TextView tvusername;
    ImageView ivmysalon,ivmenu,ivnotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        tvusername=findViewById(R.id.tvusername);
        tvusername.setText(sharedPreferences.getString("name",null));
        imageView=findViewById(R.id.ivcheckmein);
        ivmysalon=findViewById(R.id.ivmysalon);
        ivmenu=findViewById(R.id.ivmenu);
        ivnotification=findViewById(R.id.ivnotification);

        ivmysalon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MySalon.class));
            }
        });

        ivmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMenuOptions();
            }
        });

        ivnotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openClockIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
                openClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(openClockIntent);
            }
        });


    }

    private void createMenuOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_layout_menu,null);
        builder.setView(dialogView);

        TextView map=dialogView.findViewById(R.id.imap);
        TextView appointment=dialogView.findViewById(R.id.ipa);
        TextView favourites=dialogView.findViewById(R.id.ifav);
        TextView howto=dialogView.findViewById(R.id.howto);
        TextView feedback=dialogView.findViewById(R.id.ifeed);
        TextView logout=dialogView.findViewById(R.id.ilog);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            }
        });

        appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),HistoryActivity.class));
            }
        });

        favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MySalon.class));
            }
        });

        howto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),HowToUse.class));
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"fabcutsofficial@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Help");
                intent.setPackage("com.google.android.gm");
                if (intent.resolveActivity(getPackageManager())!=null)
                    startActivity(intent);
                else
                    Toast.makeText(getApplicationContext(),"Gmail App is not installed",Toast.LENGTH_SHORT).show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("user",false);
                editor.apply();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                Toast.makeText(FirstActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null)).child("checkedin")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            if (dataSnapshot.getValue(Integer.class)==1){
                                imageView.setImageResource(R.drawable.checked_in);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(getApplicationContext(),CheckedInActivity.class));
                                    }
                                });
                            }
                            else {
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                                    }
                                });
                            }
                        }
                        else {
                            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
