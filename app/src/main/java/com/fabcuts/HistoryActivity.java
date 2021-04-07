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
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView mBlogList;
    FirebaseRecyclerAdapter<ListHistory,BlogViewHolder> firebaseRecyclerAdapter;
    private DatabaseReference mDatabase;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mBlogList = findViewById(R.id.rvhistory);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Past Appointments");
        loadHistory();

    }

    private void loadHistory() {

        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(sharedPreferences.getString("phone",null))
                .child("history");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ListHistory, BlogViewHolder>
                (ListHistory.class, R.layout.layout_previous_booking,BlogViewHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, final ListHistory model, int position) {

                //viewHolder.setOrderedFrom(model.getOrderedfrom());
                if (model.getOrderedfrom()!=null){
                    FirebaseDatabase.getInstance().getReference().child("saloons").child(model.getOrderedfrom()).child("name")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        viewHolder.setOrderedFrom(dataSnapshot.getValue(String.class));
                                    }
                                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivity(new Intent(getApplicationContext(),SalonServices.class)
                                                    .putExtra("mob",model.getOrderedfrom())
                                                    .putExtra("name",dataSnapshot.getValue(String.class)));
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                }
                if (model.getTimestamp()!=null)
                viewHolder.setTimestamp(model.getTimestamp());

            }

        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
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


        public void setOrderedFrom(String orderedFrom){
            TextView post_aval=mView.findViewById(R.id.tvhistoryname);
            post_aval.setText(orderedFrom);

        }

        public void setTimestamp(String timestamp){

            if (timestamp!=null){
                TextView post_aval=mView.findViewById(R.id.tvhistorytime);
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(Long.parseLong(timestamp) * 1000L);
                String date = DateFormat.format("dd-MM-yy hh:mm", cal).toString();
                post_aval.setText(date);
            }

        }

    }
}
