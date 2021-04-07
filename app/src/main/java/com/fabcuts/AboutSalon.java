package com.fabcuts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutSalon extends AppCompatActivity {

    String mob;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    EditText etname,etphone;
    ImageButton ibplus,ibminus;
    TextView tvguestcount,tvsalonname,tvsalonaddress;
    Button checkmein;
    int guests=1,count,globalcount;
    FirebaseRecyclerAdapter<ListServices,BlogViewHolder> firebaseRecyclerAdapter;
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase,refservicecart;
    private DatabaseReference dbref,refcart,reforder,refhistory,refuser,refglobalcount,refcount,refwaittimeofbarbers;
    String random_string;
    TextView tvewt;
    ProgressBar progressBar;
    int timetosend=0,mintimeofbarber=1000,i=0;
    int waittimeforuser;
    String nameofbarber,ss="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_about_salon);

        mob=getIntent().getStringExtra("mob");
        sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        random_string=FirebaseDatabase.getInstance().getReference().child("orders").child(mob).push().getKey();
        reforder=FirebaseDatabase.getInstance().getReference().child("orders").child(mob).child(random_string);
        refuser=FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null));
        refhistory=refuser.child("history").push();
        refcart=refuser.child("servicecart");

        etphone=findViewById(R.id.etphone);
        etname=findViewById(R.id.etname);
        ibminus=findViewById(R.id.ibminus);
        ibplus=findViewById(R.id.ibplus);
        tvguestcount=findViewById(R.id.tvguestcount);
        checkmein=findViewById(R.id.bcheckmein);
        checkmein.setVisibility(View.INVISIBLE);
        tvsalonname=findViewById(R.id.tvsalonname);
        tvsalonaddress=findViewById(R.id.tvsalonaddress);
        tvewt=findViewById(R.id.tvewt);
        progressBar=findViewById(R.id.progressCount);

        findwaittime();

        mBlogList = findViewById(R.id.rvservicelist);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

        etname.setText(sharedPreferences.getString("name",null));
        etname.clearFocus();
        etphone.setText(sharedPreferences.getString("phone",null));
        etphone.clearFocus();

        ibplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guests++;
                tvguestcount.setText(guests+"");

            }
        });
        ibminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guests>1){
                    guests--;
                    tvguestcount.setText(guests+"");
                }
            }
        });

        FirebaseDatabase.getInstance().getReference().child("saloons").child(mob).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    tvsalonname.setText(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("saloons").child(mob).child("address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    tvsalonaddress.setText(dataSnapshot.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        loadservices();
        findwaittimesofbarbers();


        checkmein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ss.equals("")){

                    if (isNetworkAvailable(AboutSalon.this)) {

                        Toast.makeText(AboutSalon.this, "Please Wait..Checking", Toast.LENGTH_SHORT).show();

                        FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone", null))
                                .child("checkedin")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            if (dataSnapshot.getValue(Integer.class) == 0) {
                                                progressBar.setVisibility(View.VISIBLE);
                                                refcart = FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone", null))
                                                        .child("servicecart");
                                                refcart.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        Long tsLong = System.currentTimeMillis() / 1000;

                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                            String name = snapshot.child("servicename").getValue(String.class);
                                                            int price = snapshot.child("price").getValue(Integer.class);
                                                            int time = snapshot.child("estimatedtime").getValue(Integer.class);
                                                            OrderItem orderItem = new OrderItem(name, price, time);
                                                            reforder.child("orderitems").child(name).setValue(orderItem);
                                                            refhistory.child("orderitems").child(name).setValue(orderItem);
                                                            timetosend += time;
                                                            refcart.child(name).removeValue();

                                                        }
                                                        refuser.child("checkedin").setValue(1);
                                                        refuser.child("checkedinat").setValue(mob);
                                                        reforder.child("timestamp").setValue(String.valueOf(tsLong));
                                                        reforder.child("time").setValue(timetosend);
                                                        reforder.child("orderedfrom").setValue(mob);
                                                        refuser.child("random_string").setValue(random_string);
                                                        reforder.child("random_string").setValue(random_string);
                                                        reforder.child("name").setValue(etname.getText().toString());
                                                        reforder.child("status").setValue(0);
                                                        //reforder.child("status").setValue("Your Order has been placed successfully.\n Waiting for partner store to accept it.");
                                                        //reforder.child("orderedfromname").setValue(pname);
                                                        reforder.child("orderedby").setValue(sharedPreferences.getString("phone", null));
                                                        reforder.child("assignedto").setValue(nameofbarber);
                                                        refuser.child("assignedto").setValue(nameofbarber);
                                                        waittimeforuser = mintimeofbarber + timetosend;
                                                        FirebaseDatabase.getInstance().getReference().child("saloons").child(mob).child("barbers")
                                                                .child(nameofbarber).child("time").setValue(waittimeforuser);
                                                        refuser.child("waittime").setValue(mintimeofbarber);
                                                        refuser.child("timestamp").setValue(String.valueOf(tsLong));

                                                        refhistory.child("timestamp").setValue(String.valueOf(tsLong));
                                                        refhistory.child("orderedfrom").setValue(mob);

                                                        refcount = FirebaseDatabase.getInstance().getReference().child("saloons").child(mob).child("count");
                                                        refglobalcount = FirebaseDatabase.getInstance().getReference().child("controls").child("globalcount");

                                                        refcount.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {
                                                                    count = dataSnapshot.getValue(Integer.class);

                                                                    refglobalcount.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            if (dataSnapshot.exists()) {
                                                                                globalcount = dataSnapshot.getValue(Integer.class);

                                                                                globalcount = globalcount + 1;
                                                                                count = count + 1;
                                                                                refglobalcount.setValue(globalcount);
                                                                                refcount.setValue(count)
                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                            @Override
                                                                                            public void onSuccess(Void aVoid) {
                                                                                                Intent intent = new Intent(AboutSalon.this,
                                                                                                        CheckedInActivity.class);
                                                                                                intent.putExtra("checkedinat", mob);
                                                                                                startActivity(intent);
                                                                                            }
                                                                                        });

                                                                            } else {
                                                                                Toast.makeText(AboutSalon.this, "Global Count is 0", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });

                                                                } else {

                                                                    count = 1;
                                                                    globalcount = 1;
                                                                    refcount.setValue(1);
                                                                    refglobalcount.setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Intent intent = new Intent(AboutSalon.this,
                                                                                    CheckedInActivity.class);
                                                                            intent.putExtra("checkedinat", mob);
                                                                            startActivity(intent);
                                                                        }
                                                                    });

                                                                }
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
                                        } else {
                                            progressBar.setVisibility(View.VISIBLE);
                                            refcart = FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone", null))
                                                    .child("servicecart");
                                            refcart.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    Long tsLong = System.currentTimeMillis() / 1000;

                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                                        String name = snapshot.child("servicename").getValue(String.class);
                                                        int price = snapshot.child("price").getValue(Integer.class);
                                                        int time = snapshot.child("estimatedtime").getValue(Integer.class);

                                                        OrderItem orderItem = new OrderItem(name, price, time);
                                                        reforder.child("orderitems").child(name).setValue(orderItem);
                                                        refhistory.child("orderitems").child(name).setValue(orderItem);
                                                        timetosend += time;
                                                        refcart.child(name).removeValue();


                                                    }
                                                    refuser.child("checkedin").setValue(1);
                                                    refuser.child("checkedinat").setValue(mob);
                                                    reforder.child("timestamp").setValue(String.valueOf(tsLong));
                                                    reforder.child("time").setValue(timetosend);
                                                    reforder.child("orderedfrom").setValue(mob);
                                                    refuser.child("random_string").setValue(random_string);
                                                    reforder.child("random_string").setValue(random_string);
                                                    reforder.child("name").setValue(etname.getText().toString());
                                                    reforder.child("status").setValue(0);
                                                    //reforder.child("status").setValue("Your Order has been placed successfully.\n Waiting for partner store to accept it.");
                                                    //reforder.child("orderedfromname").setValue(pname);
                                                    reforder.child("orderedby").setValue(sharedPreferences.getString("phone", null));
                                                    reforder.child("assignedto").setValue(nameofbarber);
                                                    refuser.child("assignedto").setValue(nameofbarber);
                                                    waittimeforuser = mintimeofbarber + timetosend;
                                                    FirebaseDatabase.getInstance().getReference().child("saloons").child(mob).child("barbers")
                                                            .child(nameofbarber).child("time").setValue(waittimeforuser);
                                                    refuser.child("waittime").setValue(mintimeofbarber);
                                                    refuser.child("timestamp").setValue(String.valueOf(tsLong));

                                                    refhistory.child("timestamp").setValue(String.valueOf(tsLong));
                                                    refhistory.child("orderedfrom").setValue(mob);

                                                    refcount = FirebaseDatabase.getInstance().getReference().child("saloons").child(mob).child("count");
                                                    refglobalcount = FirebaseDatabase.getInstance().getReference().child("controls").child("globalcount");

                                                    refcount.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.exists()) {
                                                                count = dataSnapshot.getValue(Integer.class);

                                                                refglobalcount.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.exists()) {
                                                                            globalcount = dataSnapshot.getValue(Integer.class);

                                                                            globalcount = globalcount + 1;
                                                                            count = count + 1;
                                                                            refglobalcount.setValue(globalcount);
                                                                            refcount.setValue(count)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            Intent intent = new Intent(AboutSalon.this,
                                                                                                    CheckedInActivity.class);
                                                                                            intent.putExtra("checkedinat", mob);
                                                                                            startActivity(intent);
                                                                                        }
                                                                                    });

                                                                        } else {
                                                                            Toast.makeText(AboutSalon.this, "Global Count is 0", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });

                                                            } else {

                                                                count = 1;
                                                                globalcount = 1;
                                                                refcount.setValue(1);
                                                                refglobalcount.setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Intent intent = new Intent(AboutSalon.this,
                                                                                CheckedInActivity.class);
                                                                        intent.putExtra("checkedinat", mob);
                                                                        startActivity(intent);
                                                                    }
                                                                });

                                                            }
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
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
            }
            else {
                    Toast.makeText(AboutSalon.this, "Please add a service before Checking In", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void findwaittimesofbarbers() {

        refwaittimeofbarbers=FirebaseDatabase.getInstance().getReference().child("saloons").child(mob)
                .child("barbers");
        refwaittimeofbarbers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    if (snapshot.child("status").getValue(Integer.class)==1) {

                        i++;
                        if (i == 1) {
                            mintimeofbarber = snapshot.child("time").getValue(Integer.class);
                            nameofbarber = snapshot.child("name").getValue(String.class);
                        } else if (i != 1) {

                            if (mintimeofbarber >= snapshot.child("time").getValue(Integer.class)) {
                                mintimeofbarber = snapshot.child("time").getValue(Integer.class);
                                nameofbarber = snapshot.child("name").getValue(String.class);
                            }
                        }
                    }

                }

                checkmein.setVisibility(View.VISIBLE);
                if (mintimeofbarber!=1000)
                tvewt.setText(tvewt.getText().toString()+" "+mintimeofbarber+" min");
                else {
                    tvewt.setText("No barber is available for service");
                    checkmein.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(AboutSalon.this, "Can't check in at the moment", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                //Toast.makeText(AboutSalon.this, mintimeofbarber+" min", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void findwaittime() {
        FirebaseDatabase.getInstance().getReference().child("saloons").child(mob).child("waititme").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    tvewt.setText(tvewt.getText()+" "+dataSnapshot.getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadservices() {

        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null)).child("servicecart");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ListServices, BlogViewHolder>
                (ListServices.class, R.layout.layout_services,BlogViewHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final ListServices model, int position) {
                //Toast.makeText(AboutSalon.this, sharedPreferences.getString("phone",null), Toast.LENGTH_SHORT).show();

                //checkmein.setVisibility(View.VISIBLE);
                ss=model.getServicename();
                viewHolder.badd.setVisibility(View.GONE);
                viewHolder.bremove.setVisibility(View.VISIBLE);
                viewHolder.setServiceName(model.getServicename());
                viewHolder.setServicePrice(model.getPrice());


                viewHolder.bremove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        refservicecart=FirebaseDatabase.getInstance().getReference().child("users").
                                child(sharedPreferences.getString("phone",null)).child("servicecart").child(model.getServicename());
                        refservicecart.removeValue();
                        Toast.makeText(AboutSalon.this, "Service removed", Toast.LENGTH_SHORT).show();
                    }
                });


                //viewHolder.setServiceET(model.getEstimatedtime());
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    private void addtoUserServices(String service,int price,int waittime) {
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        refservicecart=FirebaseDatabase.getInstance().getReference().child("users").
                child(sharedPreferences.getString("phone",null)).child("servicecart").child(service);
        refservicecart.child("servicenaame").setValue(service);
        refservicecart.child("price").setValue(price);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView bremove;
        TextView badd;
        int i=0;



        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            badd=mView.findViewById(R.id.tvbadd);
            bremove=mView.findViewById(R.id.tvremove);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //mClickListener.onItemClick(v, getAdapterPosition());

                }
            });

        }


        public void setServiceName(String serviceName){
            TextView post_aval=mView.findViewById(R.id.tvsname);
            post_aval.setText(serviceName);

        }

        public void setServicePrice(int servicePrice){
            TextView post_aval=mView.findViewById(R.id.tvsprice);
            post_aval.setText("Rs."+servicePrice+"");

        }


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
        //inflater.inflate(R.menu.menu_call,menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menucall) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mob));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (progressBar.getVisibility()==View.VISIBLE){

        }
        else {
            super.onBackPressed();
        }
    }
}
