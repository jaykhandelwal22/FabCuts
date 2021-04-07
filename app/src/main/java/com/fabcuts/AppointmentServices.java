package com.fabcuts;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class AppointmentServices extends AppCompatActivity {

    private RecyclerView mBlogList;
    FirebaseRecyclerAdapter<ListOrderItems,BlogViewHolder> firebaseRecyclerAdapter;
    private DatabaseReference mDatabase;
    String random_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_services);

        mBlogList = findViewById(R.id.rvappointmentservices);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));
        random_string=getIntent().getStringExtra("random_string");
        loadOrderItems();
    }

    private void loadOrderItems() {
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        mDatabase= FirebaseDatabase.getInstance().getReference().child("orders").child(sharedPreferences.getString("phone",null))
        .child(random_string).child("orderitems");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ListOrderItems, BlogViewHolder>
                (ListOrderItems.class, R.layout.layout_services,BlogViewHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, ListOrderItems model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setPrice(model.getPrice());
                viewHolder.add.setVisibility(View.GONE);
                viewHolder.remove.setVisibility(View.GONE);
            }


        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView add,remove;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            add=mView.findViewById(R.id.tvbadd);
            remove=mView.findViewById(R.id.tvremove);

        }


        public void setName(String name){
            TextView post_aval=mView.findViewById(R.id.tvsname);
            post_aval.setText(name);

        }
        public void setPrice(int price){
            TextView post_aval=mView.findViewById(R.id.tvsprice);
            post_aval.setText("Rs."+price);

        }

    }
}
