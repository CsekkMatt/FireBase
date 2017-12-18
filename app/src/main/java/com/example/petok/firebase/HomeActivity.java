package com.example.petok.firebase;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.Query;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class HomeActivity extends AppCompatActivity {

    private RecyclerView mPostList;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private EditText mSearchField;
    private ImageButton mImageButton;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");

        mSearchField = (EditText) findViewById(R.id.searchField);
        mImageButton = (ImageButton)findViewById(R.id.imgBtn);
        mPostList = (RecyclerView) findViewById(R.id.post_list);
        mPostList.setHasFixedSize(true);
        mPostList.setLayoutManager(new LinearLayoutManager(this));


        mImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                    String searchText = mSearchField.getText().toString();
                 Toast.makeText(HomeActivity.this,searchText,Toast.LENGTH_SHORT).show();
                    firebaseUserSearch(searchText);
                }
        });

    }

    private void firebaseUserSearch(String searchText) {

        Query firebaseSearchQuery = mDatabase.orderByChild("Title").startAt(searchText).endAt(searchText + "\uf8ff");


        FirebaseRecyclerAdapter<Post,BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post,BlogViewHolder>(
                Post.class,
                R.layout.post_row,
                BlogViewHolder.class,
                firebaseSearchQuery

        ){
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Post model,int position){
                final String post_key = getRef(position).toString();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singlePostIntent = new Intent(HomeActivity.this,PostSingleActivity.class);
                        singlePostIntent.putExtra("post_id",post_key);
                        startActivity(singlePostIntent);                    }
                });


            }
        };

        mPostList.setAdapter(firebaseRecyclerAdapter);

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

                final String post_key = getRef(position).toString();



                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // Toast.makeText(HomeActivity.this,post_key,Toast.LENGTH_SHORT).show();

                        Intent singlePostIntent = new Intent(HomeActivity.this,PostSingleActivity.class);
                        singlePostIntent.putExtra("post_id",post_key);
                        startActivity(singlePostIntent);
                    }
                });


            }
        };

        mPostList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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

            ImageView post_image = (ImageView)mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);


        }

        @Override
        public void onClick(View v) {

        }
    }
}



