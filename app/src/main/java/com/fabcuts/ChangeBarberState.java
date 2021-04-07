package com.fabcuts;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;


public class ChangeBarberState extends AppCompatActivity {


    FirebaseRecyclerAdapter<LChangeState,BlogViewHolder> firebaseRecyclerAdapter;
    private DatabaseReference mDatabase;
    RecyclerView mBlogList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_barber_state);

        mBlogList = findViewById(R.id.rvchangestate);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        loadBarbers();
    }

    private void loadBarbers() {
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("saloons").child(sharedPreferences.getString("phone",null))
                .child("barbers");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<LChangeState, BlogViewHolder>
                (LChangeState.class, R.layout.layout_change_state,BlogViewHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final LChangeState model, int position) {

                viewHolder.setName(model.getName());
                viewHolder.setTime(model.getTime());
                if (model.getStatus()==1){
                    viewHolder.bswitch.setChecked(true);
                }

                viewHolder.bswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (viewHolder.bswitch.isChecked()){
                            mDatabase.child(model.getName()).child("status").setValue(1);
                        }
                        else {
                            mDatabase.child(model.getName()).child("status").setValue(0);
                        }
                    }
                });

                viewHolder.mten.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((model.getTime())-5>=0)
                        mDatabase.child(model.getName()).child("time").setValue((model.getTime())-5)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ChangeBarberState.this, "Reduced wait time by 5 minutes", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                viewHolder.tvtwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            mDatabase.child(model.getName()).child("time").setValue((model.getTime())+2)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ChangeBarberState.this, "increased wait time by 2 minutes", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                    }
                });
                viewHolder.tvfive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            mDatabase.child(model.getName()).child("time").setValue((model.getTime())+5)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ChangeBarberState.this, "increased wait time by 5 minutes", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                    }
                });

                viewHolder.pten.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.child(model.getName()).child("time").setValue((model.getTime())+10)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ChangeBarberState.this, "Increased wait time by 10 minutes", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                viewHolder.pthirty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.child(model.getName()).child("time").setValue((model.getTime())+30)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ChangeBarberState.this, "Reduced wait time by 30 minutes", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
               /* viewHolder.psixty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.child(model.getName()).child("time").setValue((model.getTime())+60)

                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ChangeBarberState.this, "Reduced wait time by 60 minutes", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });*/

                viewHolder.bremovebarber.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mDatabase.child(model.getName()).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ChangeBarberState.this, "Removed the barber from salon", Toast.LENGTH_SHORT).show();

                                    }
                                });
                        return false;
                    }
                });
            }



        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        Switch bswitch;
        TextView mten,pten,pthirty,psixty,tvtwo,tvfive;
        Button bremovebarber;




        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            bswitch=mView.findViewById(R.id.bswitch);
            mten=mView.findViewById(R.id.minusten);
            pten=mView.findViewById(R.id.plusten);
            pthirty=mView.findViewById(R.id.plusthirty);
            psixty=mten.findViewById(R.id.plussixty);
            tvtwo=mView.findViewById(R.id.plustwo);
            tvfive=mView.findViewById(R.id.plusfive);
            bremovebarber=mView.findViewById(R.id.bremovebarber);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //mClickListener.onItemClick(v, getAdapterPosition());

                }
            });

        }


        public void setName(String name){
            TextView post_aval=mView.findViewById(R.id.tvbarbername);
            post_aval.setText(name);

        }
        public void setTime(int time){

            TextView post_aval=mView.findViewById(R.id.tvbarbertime);
            //post_aval.setText(time+" minutes");
            Long tsLong = System.currentTimeMillis() / 1000;
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis((tsLong+60*time) * 1000L);
            String date = DateFormat.format("hh:mm", cal).toString();
            post_aval.setText("Busy till "+date);

        }



    }
}
