package com.example.petok.firebase;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class HomeActivity extends AppCompatActivity {

    private RecyclerView mPostList;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");

        mPostList = (RecyclerView) findViewById(R.id.post_list);
        mPostList.setHasFixedSize(true);
        mPostList.setLayoutManager(new LinearLayoutManager(this));

    }



    @Override
    protected void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<Post,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post,BlogViewHolder>(
                Post.class,
                R.layout.post_row,
                BlogViewHolder.class,
                mDatabase

        ){
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Post model,int position){

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
               // Log.i("TAG", "Cim:" + model.getImage() );

                Toast.makeText(HomeActivity.this,model.getImage().toString(),Toast.LENGTH_SHORT).show();

            }
        };

        mPostList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setTitle(String title){
            TextView post_title =(TextView)mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }
        public void setDesc(String desc){
            TextView post_desc = (TextView)mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setImage(Context ctx, String image){

            //Log.i("TAG", "Cim:" + image );


            ImageView post_image = (ImageView)mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);


        }
    }
}



