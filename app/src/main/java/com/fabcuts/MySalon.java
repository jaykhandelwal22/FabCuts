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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MySalon extends AppCompatActivity {

    ActionBar actionBar;
    private RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<ListMySalon,BlogViewHolder> firebaseRecyclerAdapter;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_salon);

        actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("My Salon");

        mBlogList = findViewById(R.id.rvmysalon);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

        loadfav();

    }

    private void loadfav() {

        sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                    .child("favourites");
            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ListMySalon, BlogViewHolder>
                    (ListMySalon.class, R.layout.layout_my_salon,BlogViewHolder.class, mDatabase) {

                @Override
                protected void populateViewHolder(final BlogViewHolder viewHolder, final ListMySalon model, int position) {
                    viewHolder.setName(model.getName());
                    FirebaseDatabase.getInstance().getReference().child("saloons").child(model.getPhone()).child("address")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        viewHolder.tvaddress.setText(dataSnapshot.getValue(String.class));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase.getInstance().getReference().child("saloons").child(model.getPhone()).child("status")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()){
                                                if (dataSnapshot.getValue(Integer.class)==1){
                                                    startActivity(new Intent(getApplicationContext(),SalonServices.class)
                                                            .putExtra("mob",model.getPhone())
                                                            .putExtra("name",model.getName()));
                                                }
                                                else {
                                                    Toast.makeText(MySalon.this, "Salon is closed for online appointments", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        }
                    });

                }

            };
            mBlogList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView tvaddress;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            tvaddress=mView.findViewById(R.id.tvaddress);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }

        public void setName(String name){
            TextView post_aval=mView.findViewById(R.id.tvnamesalon);
            post_aval.setText(name);
        }
    }
}
