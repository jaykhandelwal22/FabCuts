package com.fabcuts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SalonServices extends AppCompatActivity {


    Button bproceed;
    String mob,name;
    //ImageView ivsalon;
    ProgressBar progressBar;
    TextView tvadd,tvname,tvwait;
    ActionBar actionBar;
    private RecyclerView mBlogList,mBlogList2;
    FirebaseRecyclerAdapter<ListServices,BlogViewHolder> firebaseRecyclerAdapter;

    FirebaseRecyclerAdapter<ListPhotos,BlogViewHolder2> firebaseRecyclerAdapterphotos;
    private DatabaseReference mDatabase,mDatabase2,refservicecart,checkswitch,refwaittimeofbarbers;
    int waittime;
    int mintimeofbarber=0,i=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_services);

        actionBar=getSupportActionBar();

        mob=getIntent().getStringExtra("mob");
        name=getIntent().getStringExtra("name");
        waittime=getIntent().getIntExtra("waittime",1000);
        actionBar.setTitle(name);
        actionBar.hide();

        bproceed=findViewById(R.id.bproceed);
        tvname=findViewById(R.id.tvnameofsalon);
        tvwait=findViewById(R.id.tvrtwaittime);
        tvname.setText(name);
        //ivsalon=findViewById(R.id.ivsalon);
        progressBar=findViewById(R.id.prog);
        mBlogList = findViewById(R.id.rvservices);
        mBlogList2=findViewById(R.id.rvphotos);
        tvadd=findViewById(R.id.tvadd);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        mBlogList2.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        //loadimage();

        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        checkswitch = FirebaseDatabase.getInstance().getReference().child("saloons").child(mob)
                .child("status");
        checkswitch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.getValue(Integer.class)==1){
                        bproceed.setVisibility(View.VISIBLE);
                        bproceed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getApplicationContext(),AboutSalon.class)
                                        .putExtra("mob",mob));
                            }
                        });
                    }
                    else {
                        bproceed.setText("Salon is Closed for Online Appointment");
                        bproceed.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(SalonServices.this, "Salon is Closed for Online Appointment", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        findwaittimesofbarbers();
        loadphotos();
        loadaddress();
        loadservices();


    }

    private void findwaittimesofbarbers() {

        refwaittimeofbarbers=FirebaseDatabase.getInstance().getReference().child("saloons").child(mob)
                .child("barbers");
        refwaittimeofbarbers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    if (snapshot.child("status").getValue(Integer.class)==1){
                        i++;
                        if (i==1){
                            mintimeofbarber=snapshot.child("time").getValue(Integer.class);
                            //nameofbarber=snapshot.child("name").getValue(String.class);
                        }
                        else if (i!=1){

                            if (mintimeofbarber>=snapshot.child("time").getValue(Integer.class))
                            {
                                mintimeofbarber=snapshot.child("time").getValue(Integer.class);
                                //nameofbarber=snapshot.child("name").getValue(String.class);
                            }
                        }
                    }


                }

                //checkmein.setVisibility(View.VISIBLE);
                tvwait.setText(tvwait.getText().toString()+""+mintimeofbarber+" minutes");
                //Toast.makeText(AboutSalon.this, mintimeofbarber+" min", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadphotos() {
        mDatabase2= FirebaseDatabase.getInstance().getReference().child("saloons").child(mob).child("url");

        firebaseRecyclerAdapterphotos = new FirebaseRecyclerAdapter<ListPhotos, BlogViewHolder2>
                (ListPhotos.class, R.layout.layout_photos,BlogViewHolder2.class, mDatabase2) {

            @Override
            protected void populateViewHolder(BlogViewHolder2 viewHolder, ListPhotos model, int position) {

                viewHolder.setPhoto(model.getUrl());
                progressBar.setVisibility(View.GONE);
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(),SalonPhotos.class)
                        .putExtra("dbref",mDatabase2.toString())
                        .putExtra("mob",mob));
                    }
                });

            }



        };
        mBlogList2.setAdapter(firebaseRecyclerAdapterphotos);
    }

    private void loadservices() {

        mDatabase= FirebaseDatabase.getInstance().getReference().child("saloons").child(mob).child("services");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ListServices, BlogViewHolder>
                (ListServices.class, R.layout.layout_services,BlogViewHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final ListServices model, int position) {

                viewHolder.setServiceName(model.getServicename());
                viewHolder.setServicePrice(model.getPrice());

                viewHolder.badd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addtoUserServices(model.getServicename(),model.getPrice(),model.getEstimatedtime());
                        viewHolder.badd.setVisibility(View.GONE);
                        viewHolder.bremove.setVisibility(View.VISIBLE);
                        Toast.makeText(SalonServices.this, "Service Added", Toast.LENGTH_SHORT).show();

                    }
                });

                viewHolder.bremove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        refservicecart=FirebaseDatabase.getInstance().getReference().child("users").
                                child(sharedPreferences.getString("phone",null)).child("servicecart").child(model.servicename);
                        refservicecart.removeValue();
                        Toast.makeText(SalonServices.this, "Service removed", Toast.LENGTH_SHORT).show();
                        viewHolder.bremove.setVisibility(View.GONE);
                        viewHolder.badd.setVisibility(View.VISIBLE);
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
        refservicecart.child("servicename").setValue(service);
        refservicecart.child("price").setValue(price);
        refservicecart.child("estimatedtime").setValue(waittime);
    }

    private void loadaddress() {
        FirebaseDatabase.getInstance().getReference().child("saloons").child(mob).child("address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    tvadd.setText(dataSnapshot.getValue(String.class));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*private void loadimage() {
        FirebaseDatabase.getInstance().getReference().child("saloons").child(mob).child("url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Picasso.with(getApplicationContext()).load(dataSnapshot.getValue(String.class)).fit().into(ivsalon);
                    progressBar.setVisibility(View.GONE);

                }
                else
                    progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/


    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView badd;
        TextView bremove;
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

    public static class BlogViewHolder2 extends RecyclerView.ViewHolder {
        View mView;



        public BlogViewHolder2(View itemView) {
            super(itemView);
            mView = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //mClickListener.onItemClick(v, getAdapterPosition());

                }
            });

        }


        public void setPhoto(String url){
            ImageView imageView=mView.findViewById(R.id.ivsalon);
            Picasso.with(mView.getContext()).load(url).fit().into(imageView);
            //progressBar.setVisibility(View.GONE);


        }

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {

        MenuInflater inflater = getMenuInflater();
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_waittime,menu);
        final MenuItem item = menu.findItem(R.id.menuwaittime);
        if (waittime!=1000)
        //item.setTitle("Est. wait time : "+waittime+" min");
        item.setCheckable(false);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menuwaittime) {
        }
        return super.onOptionsItemSelected(item);
    }

}
