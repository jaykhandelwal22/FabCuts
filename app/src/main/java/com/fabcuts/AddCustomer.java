package com.fabcuts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddCustomer extends AppCompatActivity {


    private RecyclerView mBlogList,mBlogList2;
    FirebaseRecyclerAdapter<ListServices,BlogViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerAdapter<ListBarbers,BlogViewHolder2> firebaseRecyclerAdapter2;
    private DatabaseReference mDatabase,mDatabase2,refservicecart,refwaittimeofbarbers;
    String bname,sname="",cname="";
    ActionBar actionBar;

    Button baddcustomer;
    String common;
    int addtowaittime=0;
    int initialwt;

    int timetosend=0,mintimeofbarber=1000,i=0;
    int waittimeforuser;
    String nameofbarber="";
    EditText etname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_add_customer);

        actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Add new offline Customer");
        baddcustomer=findViewById(R.id.baddtolist);
        etname=findViewById(R.id.etcustomername);
        mBlogList=findViewById(R.id.rvbarberservices);
        mBlogList2=findViewById(R.id.rvbarbername);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        mBlogList2.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        findwaittimesofbarbers();
        //loadBarbers();
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        common=FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                .push().getKey();
        loadservices();

        baddcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sname.equals("")&&mintimeofbarber!=1000&&!nameofbarber.equals("")){
                    final SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    Long tsLong = System.currentTimeMillis() / 1000;

                    cname=etname.getText().toString();

                    FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                            .child(common).child("timestamp").setValue(String.valueOf(tsLong));
                    FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                            .child(common).child("random_string").setValue(common);
                    if (cname.equals(""))
                    FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                            .child(common).child("name").setValue("offline customer");
                    else
                        FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                                .child(common).child("name").setValue(cname);

                    FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                            .child(common).child("status").setValue(0);
                    FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                            .child(common).child("time").setValue(addtowaittime);
                    FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                            .child(common).child("assignedto").setValue(nameofbarber);

                    FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
                            .child("barbers").child(nameofbarber).child("time").setValue((mintimeofbarber+addtowaittime))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddCustomer.this, "Added", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });

                }
                else {
                    Toast.makeText(AddCustomer.this, "Please Retry", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void findwaittimesofbarbers() {
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        refwaittimeofbarbers=FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
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

                //Toast.makeText(AddCustomer.this, mintimeofbarber+" min", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void loadBarbers() {
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mDatabase2= FirebaseDatabase.getInstance().getReference().child("saloons").
                child(sharedPreferences.getString("phone",null)).child("barbers");

        firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<ListBarbers,BlogViewHolder2>
                (ListBarbers.class, R.layout.layout_barber_list,BlogViewHolder2.class, mDatabase2) {

            @Override
            protected void populateViewHolder(BlogViewHolder2 viewHolder, final ListBarbers model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bname=model.getName();
                    }
                });
            }
        };
        mBlogList2.setAdapter(firebaseRecyclerAdapter2);
    }

    private void loadservices() {
        final SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
                .child("services");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ListServices,BlogViewHolder>
                (ListServices.class, R.layout.layout_services,BlogViewHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final ListServices model, int position) {
                viewHolder.setServiceName(model.getServicename());
                viewHolder.setServicePrice(model.getPrice());
                baddcustomer.setVisibility(View.VISIBLE);


                viewHolder.badd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        addtoUserServices(model.getServicename(),model.getPrice(),model.getEstimatedtime());
                        viewHolder.badd.setVisibility(View.GONE);
                        viewHolder.bremove.setVisibility(View.VISIBLE);
                        sname=model.getServicename();
                        addtowaittime+=model.getEstimatedtime();
                        Toast.makeText(AddCustomer.this, "Service Added", Toast.LENGTH_SHORT).show();
                        FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                                .child(common).child("random_string").setValue(common);


                    }
                });

                viewHolder.bremove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                        /*refservicecart=FirebaseDatabase.getInstance().getReference().child("users").
                                child(sharedPreferences.getString("phone",null)).child("servicecart").child(model.servicename);
                        refservicecart.removeValue();*/
                        FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null)).
                                child(common).child("orderitems").child(model.getServicename()).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                                .child(common).child("random_string").removeValue();
                        addtowaittime-=model.getEstimatedtime();
                        Toast.makeText(AddCustomer.this, "Service removed", Toast.LENGTH_SHORT).show();
                        viewHolder.bremove.setVisibility(View.GONE);
                        viewHolder.badd.setVisibility(View.VISIBLE);
                    }
                });
            }


        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    private void addtoUserServices(String service,int price,int waittime) {
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        refservicecart=FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null)).
                child(common).child("orderitems").child(service);
        refservicecart.child("name").setValue(service);
        refservicecart.child("price").setValue(price);
        refservicecart.child("estimatedtime").setValue(waittime);
    }


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

        }

        public void setName(String bname){
            TextView post_aval=mView.findViewById(R.id.tvbname);
            post_aval.setText(bname);

        }


    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
                .child(common).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(getApplicationContext(),SaloonDashboard.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                });
        //super.onBackPressed();
    }
}
