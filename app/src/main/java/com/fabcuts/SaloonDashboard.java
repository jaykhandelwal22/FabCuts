package com.fabcuts;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.CONNECTIVITY_SERVICE;

public class SaloonDashboard extends AppCompatActivity {

    Switch aswitch;
    DatabaseReference checkswitch;
    String phone;
    int bool;
    private RecyclerView mBlogList;
    FirebaseRecyclerAdapter<ListAppointments,BlogViewHolder> firebaseRecyclerAdapter;
    private DatabaseReference mDatabase;
    private static final int REQUEST_PHONE_CALL = 1;
    Button addcustomer,bonoff,bsales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saloon_dashboard);

        final SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        aswitch = findViewById(R.id.aswitch);
        addcustomer=findViewById(R.id.baddcustomer);
        bonoff=findViewById(R.id.bonoff);
        bsales=findViewById(R.id.bsales);

        phone=sharedPreferences.getString("phone",null);
        checkswitch = FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
                .child("status");

        aswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (aswitch.isChecked()&&isNetworkAvailable(SaloonDashboard.this)) {

                 /*   Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "ONLINE , RECEIVING ORDERS", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    */
                    if (phone!=null)
                        checkswitch.setValue(1);

                } else if (!aswitch.isChecked()&&isNetworkAvailable(SaloonDashboard.this)){

                    /*Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "OFFLINE , NO ORDERS FROM NOW", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    */
                    if (phone!=null)
                        checkswitch.setValue(0);
                }
                else if (!isNetworkAvailable(SaloonDashboard.this)){
                    Toast.makeText(SaloonDashboard.this, "Internet connection not found", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SaloonDashboard.this, "its taking longer", Toast.LENGTH_SHORT).show();
                }

            }
        });

        addcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //createBoxForCustomerName();
                startActivity(new Intent(getApplicationContext(),AddCustomer.class));
            }
        });

        bonoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ChangeBarberState.class));
            }
        });
        bsales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SalesActivity.class));
            }
        });

        checkswitch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bool=dataSnapshot.getValue(Integer.class);

                if ((bool)==1){
                    aswitch.setChecked(true);
                }
                else {
                    aswitch.setChecked(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mBlogList = findViewById(R.id.rvappointments);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        loadAppointments();
    }

    private void createBoxForCustomerName() {

        final SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(SaloonDashboard.this);
        alertdialog.setTitle("Add New Customer");

        final EditText etaddress = new EditText(SaloonDashboard.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        etaddress.setLayoutParams(lp);
        alertdialog.setView(etaddress);
        alertdialog.setIcon(getResources().getDrawable(R.drawable.logo));
        alertdialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (!etaddress.getText().toString().equals("")){
                    Long tsLong = System.currentTimeMillis() / 1000;

                    String common;
                    common=FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                            .push().getKey();
                    FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                            .child(common).child("timestamp").setValue(String.valueOf(Long.parseLong("9999999999") - tsLong));
                    FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                            .child(common).child("random_string").setValue(common);
                    FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                            .child(common).child("name").setValue(etaddress.getText().toString());
                    FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                            .child(common).child("status").setValue(0);

                    Toast.makeText(SaloonDashboard.this, "Added", Toast.LENGTH_SHORT).show();
                }


                else {
                    Toast.makeText(SaloonDashboard.this, "Enter name of Customer", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertdialog.show();


    }

    private void loadAppointments() {

        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null));

            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ListAppointments, BlogViewHolder>
                    (ListAppointments.class, R.layout.layout_appointments,BlogViewHolder.class, mDatabase.orderByChild("timestamp")) {

                @Override
                protected void populateViewHolder(final BlogViewHolder viewHolder, final ListAppointments model, int position) {
                    //Toast.makeText(SaloonDashboard.this, "yes", Toast.LENGTH_SHORT).show();
                    viewHolder.setOrderedBy(model.getName());

                    viewHolder.setTime(model.getTime());

                    if (model.getTimestamp()!=null)
                    viewHolder.setTimestamp(model.getTimestamp());
                    if (model.getStatus()==0){
                        viewHolder.bstart.setVisibility(View.VISIBLE);
                        viewHolder.bend.setVisibility(View.GONE);
                    }
                    else {
                        viewHolder.bstart.setVisibility(View.GONE);
                        viewHolder.bend.setVisibility(View.VISIBLE);
                    }

                    if (model.getOrderedby()==null){
                        viewHolder.ibcall.setVisibility(View.GONE);
                        //viewHolder.setOrderedBy("offline customer");
                    }

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (model.getRandom_string()!=null)
                            startActivity(new Intent(getApplicationContext(),AppointmentServices.class)
                            .putExtra("random_string",model.getRandom_string()));
                        }
                    });

                    viewHolder.ibcall.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + model.getOrderedby()));

                            if (ContextCompat.checkSelfPermission(SaloonDashboard.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(SaloonDashboard.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                            } else {
                                startActivity(intent);
                            }

                            return false;
                        }
                    });

                    viewHolder.bstart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (model.getRandom_string()!=null)
                            mDatabase.child(model.getRandom_string()).child("status").setValue(1);
                            if (model.getOrderedby()!=null)
                            FirebaseDatabase.getInstance().getReference().child("users").child(model.getOrderedby())
                                    .child("checkedin").setValue(0);
                            Long tsLong=System.currentTimeMillis()/1000;
                            mDatabase.child(model.getRandom_string()).child("startedat").setValue(tsLong+"");
                            viewHolder.bstart.setVisibility(View.GONE);

                        }
                    });

                    viewHolder.bend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //mDatabase.child(model.getRandom_string()).removeValue();
                            if (model.getOrderedby()!=null)
                            FirebaseDatabase.getInstance().getReference().child("users").child(model.getOrderedby())
                                    .child("checkedin").setValue(0);
                            reducewaittime(model.getRandom_string(),model.getAssignedto(),model.getTime());
                            Long endedat=System.currentTimeMillis()/1000;
                            int diff= (int) ((model.getTime())-((endedat-Long.parseLong(model.getStartedat()))/60));
                            reducewaitofotherqueuedpeople(diff);
                        }
                    });

                    if (model.getRandom_string()==null){

                        viewHolder.bremove.setVisibility(View.INVISIBLE);

                    }

                    viewHolder.bremove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (model.getOrderedby()!=null)
                            FirebaseDatabase.getInstance().getReference().child("users").child(model.getOrderedby()).child("checkedin").setValue(0);
                            reducewaitofotherqueuedpeople(model.getTime());
                            reducewaittime(model.getRandom_string(),model.getAssignedto(),model.getTime());
                        }
                    });
                }
            };
            mBlogList.setAdapter(firebaseRecyclerAdapter);

    }

    private void reducewaitofotherqueuedpeople(final int waittime) {

        final SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                .addListenerForSingleValueEvent(new ValueEventListener() {
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

                }
                else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void reducewaittime(final String random_string, final String nameofbarber, final int time) {

        final int[][] initial_time = {new int[1]};
        final int[] reduced_time = new int[1];
        final SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
                .child("barbers").child(nameofbarber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        initial_time[0][0] =dataSnapshot.child("time").getValue(Integer.class);
                        reduced_time[0] = initial_time[0][0]-time;
                        FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
                                .child("barbers").child(nameofbarber).child("time").setValue(reduced_time[0])
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mDatabase.child(random_string).removeValue();
                                    }
                                });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }


    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {

        MenuInflater inflater = getMenuInflater();
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menusalonsetup) {
            startActivity(new Intent(getApplicationContext(),SaloonSetUp.class));

        }
        return super.onOptionsItemSelected(item);
    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        Button bstart,bend,bremove;
        ImageButton ibcall;




        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            bstart=mView.findViewById(R.id.bstart);
            bend=mView.findViewById(R.id.bend);
            ibcall=mView.findViewById(R.id.ibcallcustomer);
            bremove=mView.findViewById(R.id.bremove);

        }


        public void setOrderedBy(String customername){

            TextView post_aval=mView.findViewById(R.id.tvcustomername);
            post_aval.setText(customername);

        }

        public void setTimestamp(String timestamp){

            TextView post_aval=mView.findViewById(R.id.tvtimestamp);
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            timestamp= String.valueOf((Long.parseLong(timestamp)));
            cal.setTimeInMillis(Long.parseLong(timestamp) * 1000L);
            String date = DateFormat.format("dd-MM-yy hh:mm", cal).toString();
            post_aval.setText(date);

        }

        public void setTime(int time){
            TextView post_aval=mView.findViewById(R.id.tvctime);
            post_aval.setText(time+" min");

        }
    }
}
