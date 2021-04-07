
package com.fabcuts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
/*import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;*/

import java.util.Calendar;
import java.util.Locale;


public class CheckedInActivity extends AppCompatActivity {


    ActionBar actionBar;
    RelativeLayout rl;
    String checkedinat,namep,random_string;
    TextView tvs,tvsa,tvwt,bqr,tvmysalon,tvdirections,tvreminder;
    Button bcancelcheckin;
    Double lat,lng;
    int waittime;
    String timestamp,assignedto;
    Long diff;
    Boolean check=false;
    ImageView ivstar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked_in);

        actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Checked In");

        rl=findViewById(R.id.rl);
        tvs=findViewById(R.id.tvs);
        tvsa=findViewById(R.id.tvsa);
        tvwt=findViewById(R.id.tvwt);
        tvmysalon=findViewById(R.id.tvmysalon);
        tvdirections=findViewById(R.id.tvdirections);
        tvreminder=findViewById(R.id.tvreminder);
        bcancelcheckin=findViewById(R.id.bcancelcheckin);
        ivstar=findViewById(R.id.ivstar);
        bqr=findViewById(R.id.bqr);


        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                .child("checkedinat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    checkedinat=dataSnapshot.getValue(String.class);
                    fetchdata();
                    checksalonforfavourites();
                }

                else {
                    Toast.makeText(CheckedInActivity.this, "There was some error..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        tvreminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CheckedInActivity.this, "Set an alarm according to wait time", Toast.LENGTH_SHORT).show();
                Intent openClockIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
                openClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(openClockIntent);
            }
        });
        findwaittime();
        checkforcheckin();
        refreshWT();
    }

    private void refreshWT() {
        new CountDownTimer(10000000, 60000) {
            @Override
            public void onTick(long l) {
                //Toast.makeText(CheckedInActivity.this, "Automatically Refreshed", Toast.LENGTH_SHORT).show();
                findwaittime();
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //refreshWT();
    }

    private void checkforcheckin() {
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                .child("checkedin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.getValue(Integer.class)==1){
                        rl.setVisibility(View.VISIBLE);
                        actionBar.setTitle("Checked in");
                    }
                    else {
                        rl.setVisibility(View.INVISIBLE);
                        actionBar.setTitle("You are no longer in the queue");
                        //Toast.makeText(CheckedInActivity.this, "You are no longer in the queue", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    rl.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checksalonforfavourites() {
        final SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                .child("favourites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (snapshot.child("phone").getValue(String.class).equals(checkedinat)){
                        check=true;
                        break;
                    }
                }
                if (check){
                    ivstar.setImageResource(R.drawable.ic_star_black_24dp);
                    tvmysalon.setText("Remove from My Salon");
                    tvmysalon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                                    .child("favourites").child(checkedinat).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(CheckedInActivity.this, "Removed from My Salon", Toast.LENGTH_SHORT).show();
                                            ivstar.setImageResource(R.drawable.ic_star_border_black_24dp);
                                            tvmysalon.setText("Add to My Salon");
                                        }
                                    });
                        }
                    });
                }
                else {
                    tvmysalon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                            FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                                    .child("favourites").child(checkedinat).child("name").setValue(namep);

                            FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                                    .child("favourites").child(checkedinat).child("phone").setValue(checkedinat)
                                    .addOnSuccessListener(
                                            new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            ivstar.setImageResource(R.drawable.ic_star_black_24dp);
                                            Toast.makeText(CheckedInActivity.this, "Added to My Salon", Toast.LENGTH_SHORT).show();
                                            tvmysalon.setText("Remove from My Salon");
                                            tvmysalon.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                                                            .child("favourites").child(checkedinat).removeValue()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    ivstar.setImageResource(R.drawable.ic_star_border_black_24dp);
                                                                    tvmysalon.setText("Add to my salon");
                                                                    Toast.makeText(CheckedInActivity.this, "Removed from My Salon", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void findwaittime() {
        final SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                .child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    timestamp=dataSnapshot.getValue(String.class);
                    FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                            .child("waittime").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            waittime=dataSnapshot.getValue(Integer.class);
                            //Long tsLong = System.currentTimeMillis() / 1000;
                            //diff=tsLong-Long.parseLong(timestamp);
                            /*if (waittime-diff>0)
                            tvwt.setText((waittime-diff)+"");
                            else
                                tvwt.setText("--");*/
                            Long tsLong = System.currentTimeMillis() / 1000;
                            Long tstoreach=(Long.parseLong(timestamp)+waittime*60);
                            diff=(tstoreach)-tsLong;
                          /*  Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis((Long.parseLong(timestamp)+waittime*60) * 1000L);
                            String date = DateFormat.format("hh:mm", cal).toString();*/
                          if (diff<0){
                              tvwt.setText("0");
                              //Toast.makeText(CheckedInActivity.this, "Reach Salon As soon as Possible", Toast.LENGTH_SHORT).show();
                          }
                          else {
                              tvwt.setText(diff/60+"");
                          }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void fetchdata() {

        FirebaseDatabase.getInstance().getReference().child("saloons").child(checkedinat).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    //rl.setVisibility(View.VISIBLE);

                    findRandomString();
                    namep=dataSnapshot.child("name").getValue(String.class);
                    //Toast.makeText(CheckedInActivity.this, random_string, Toast.LENGTH_SHORT).show();
                        tvs.setText(dataSnapshot.child("name").getValue(String.class));
                        tvsa.setText(dataSnapshot.child("address").getValue(String.class));
                        //tvwt.setText(dataSnapshot.child("waittime").getValue(Integer.class)+"");
                        lat=dataSnapshot.child("latitude").getValue(Double.class);
                        lng=dataSnapshot.child("longitude").getValue(Double.class);
                    initializeClicks();

                }
                else {
                    Toast.makeText(CheckedInActivity.this, "There was some error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void findRandomString() {
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null)).child("random_string")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        random_string=dataSnapshot.getValue(String.class);
                        //Toast.makeText(CheckedInActivity.this, random_string, Toast.LENGTH_SHORT).show();
                        bcancelcheckin.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                cancelalert();
                              /*  bcancelcheckin.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //Toast.makeText(CheckedInActivity.this, "", Toast.LENGTH_SHORT).show();


                                    }
                                });*/
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void initializeClicks() {




        tvdirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = String.valueOf(lat);
                String longitude = String.valueOf(lng);
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                try{
                        startActivity(mapIntent);

                }catch (NullPointerException e){
                }
            }
        });

        bqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (isNetworkAvailable(CheckedInActivity.this)){
                    IntentIntegrator integrator = new IntentIntegrator(CheckedInActivity.this);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                    integrator.setPrompt("focus on QR code");
                    integrator.setCameraId(0);
                    integrator.setBarcodeImageEnabled(true);
                    integrator.setOrientationLocked(true);
                    integrator.setBeepEnabled(true);
                    integrator.initiateScan();
                }
                else {
                    Toast.makeText(CheckedInActivity.this, "Please turn on internet to scan", Toast.LENGTH_SHORT).show();
                }

*/


            }
        });
    }

    private void cancelalert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CheckedInActivity.this);
        final AlertDialog alertDialog;
        alertDialog = alertDialogBuilder.create();
        alertDialogBuilder.setTitle("Are you sure to cancel?");
        alertDialogBuilder.setIcon(R.drawable.logo);
        alertDialogBuilder.setMessage("Canceling CheckIn will remove you from queue at salon!");
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton("Yes,Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                reducetime();
                Toast.makeText(CheckedInActivity.this, "Please Wait while we cancel the appointment", Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference().child("orders").child(checkedinat).child(random_string).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                checkuserout();
                            }
                        });
            }
        });

        //alertDialog.show();
        alertDialogBuilder.show();
    }

    private void reducetime() {
        findwaittime();
        final SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null)).
                child("assignedto").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String assignedto=dataSnapshot.getValue(String.class);
                FirebaseDatabase.getInstance().getReference().child("saloons").child(checkedinat).child("barbers").child(assignedto)
                        .child("time").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FirebaseDatabase.getInstance().getReference().child("saloons").child(checkedinat).child("barbers").child(assignedto)
                                .child("time").setValue((dataSnapshot.getValue(Integer.class)-waittime));
                        //Toast.makeText(CheckedInActivity.this, "Reduced to "+(dataSnapshot.getValue(Integer.class)-waittime), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


   /* @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {

            if (result.getContents() == null) {


                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();

            } else {

                checkscanresult(result.getContents());
            }

        } else {

            super.onActivityResult(requestCode, resultCode, data);

        }
    }*/

    private void checkscanresult(String contents) {
        if (contents.equals(checkedinat)){
            checkuserout();
        }
        else {

            Toast.makeText(this, contents, Toast.LENGTH_SHORT).show();
            Toast.makeText(this,"checked in at:"+ checkedinat, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Wrong QR scanned", Toast.LENGTH_LONG).show();
        }
    }

    private void checkuserout() {
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                .child("checkedin").setValue(0);

        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                .child("checkedinat").removeValue();

        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                .child("history").push().child(checkedinat).child("name").setValue(namep);

        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                .child("history").push().child(checkedinat).child("phone").setValue(checkedinat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        reducewaitofotherqueuedpeople();

                    }
                });



    }

    private void reducewaitofotherqueuedpeople() {


        FirebaseDatabase.getInstance().getReference().child("orders").child(checkedinat).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        final String orderedby;
                        int status;
                        //status=snapshot.child("status").getValue(Integer.class);
                        orderedby=snapshot.child("orderedby").getValue(String.class);

                        if (orderedby!=null)
                        {
                            FirebaseDatabase.getInstance().getReference().child("users").child(orderedby).child("waittime")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                FirebaseDatabase.getInstance().getReference().child("users").child(orderedby).child("waittime")
                                                        .setValue((dataSnapshot.getValue(Integer.class)-waittime));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }



                    }

                    Toast.makeText(CheckedInActivity.this, "Thankyou for using Fab Cuts service!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CheckedInActivity.this,
                            FirstActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(CheckedInActivity.this, "Thankyou for using Fab Cuts service!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CheckedInActivity.this,
                            FirstActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CheckedInActivity.this,
                FirstActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getActiveNetworkInfo()!=null
                &&connectivityManager.getActiveNetworkInfo().isConnected();

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {

        MenuInflater inflater = getMenuInflater();
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_refresh,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menurefresh) {
            findwaittime();

        }
        return super.onOptionsItemSelected(item);
    }

}
