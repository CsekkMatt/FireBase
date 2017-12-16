package com.example.petok.firebase;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class HomeActivity extends AppCompatActivity {

    private RecyclerView mPostList;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;


    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");


        mPostList = (RecyclerView) findViewById(R.id.post_list);
        mPostList.setHasFixedSize(true);
        mPostList.setLayoutManager(new LinearLayoutManager(this));


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem addPost = menu.findItem(R.id.action_add);
        MenuItem logOut = menu.findItem(R.id.action_logout);
        MenuItem logIn = menu.findItem(R.id.action_login);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            addPost.setVisible(false);
            logIn.setVisible(true);
            logOut.setVisible(false);


        }
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            logIn.setVisible(false);

        }


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_add){
            startActivity(new Intent(HomeActivity.this,PostActivity.class));
        }
        if(item.getItemId() == R.id.action_logout){
            mAuth.signOut();
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        }
        if(item.getItemId() == R.id.action_login){
            startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
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



