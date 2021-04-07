package com.fabcuts;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SalonPhotos extends AppCompatActivity {

    FirebaseRecyclerAdapter<ListPhotos,BlogViewHolder2> firebaseRecyclerAdapterphotos;
    private DatabaseReference mDatabase,mDatabase2;
    String mob;
    private RecyclerView mBlogList2;
    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_photos);

        mBlogList2=findViewById(R.id.rvsalonphotos);
        mBlogList2.setLayoutManager(new LinearLayoutManager(this));
        actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.hide();

        mob=getIntent().getStringExtra("mob");
        loadphotos();
    }

    private void loadphotos() {
        mDatabase2= FirebaseDatabase.getInstance().getReference().child("saloons").child(mob).child("url");

        firebaseRecyclerAdapterphotos = new FirebaseRecyclerAdapter<ListPhotos, BlogViewHolder2>
                (ListPhotos.class, R.layout.layout_salon_photos,BlogViewHolder2.class, mDatabase2) {

            @Override
            protected void populateViewHolder(BlogViewHolder2 viewHolder, ListPhotos model, int position) {

                viewHolder.setPhoto(model.getUrl());
                //progressBar.setVisibility(View.GONE);

            }



        };
        mBlogList2.setAdapter(firebaseRecyclerAdapterphotos);
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
            ImageView imageView=mView.findViewById(R.id.ivsalonpic);
            Picasso.with(mView.getContext()).load(url).fit().into(imageView);
            //progressBar.setVisibility(View.GONE);


        }

    }
}
