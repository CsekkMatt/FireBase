package com.example.petok.firebase;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class PostSingleActivity extends AppCompatActivity {

    private String mPost_key = null;
    private DatabaseReference mDatabase;

    private ImageView mPostSingleImage;
    private TextView mPostSingleTitle;
    private TextView mPostSingleDesc;
    private TextView mPostSingleAuthor;

    private Button mPostRemoveBtn;
    FirebaseAuth mAuth;

    public static String EncodeString(String string) {
        return string.replace(".", ",");
    }

    public static String DecodeString(String string) {
        return string.replace(",", ".");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_single);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");
        mPost_key = getIntent().getExtras().getString("post_id");
        mAuth = FirebaseAuth.getInstance();
        final String encodedPost_key = mPost_key.substring(mPost_key.lastIndexOf("/") + 1);

        mPostSingleDesc = (TextView) findViewById(R.id.single_post_desc);
        mPostSingleTitle = (TextView) findViewById(R.id.single_post_title);
        mPostSingleImage = (ImageView) findViewById(R.id.single_post_image);
        mPostSingleAuthor = (TextView) findViewById(R.id.single_post_author);
        mPostRemoveBtn = (Button) findViewById(R.id.PostRemoveBtn);

        mDatabase.child(encodedPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_title = (String) dataSnapshot.child("Title").getValue();
                String post_desc = (String) dataSnapshot.child("Desc").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();
                String post_author = (String)dataSnapshot.child("username").getValue();
                mPostSingleTitle.setText(post_title);
                mPostSingleDesc.setText(post_desc);
                mPostSingleAuthor.setText(post_author);
                Picasso.with(PostSingleActivity.this).load(post_image).into(mPostSingleImage);


                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    if (mAuth.getCurrentUser().getUid().equals(post_uid)) {
                        mPostRemoveBtn.setVisibility(View.VISIBLE);
                    }
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mPostRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(encodedPost_key).removeValue();
                startActivity(new Intent(PostSingleActivity.this, HomeActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem editPost = menu.findItem(R.id.edit_post);
        MenuItem addPost = menu.findItem(R.id.action_add);
        MenuItem logOut = menu.findItem(R.id.action_logout);
        MenuItem logIn = menu.findItem(R.id.action_login);
        MenuItem editProfile = menu.findItem(R.id.edit_profile);

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            editPost.setVisible(false);
            addPost.setVisible(false);
            logIn.setVisible(true);
            logOut.setVisible(false);
            editPost.setVisible(false);
            editProfile.setVisible(false);



        }
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            editPost.setVisible(false);
            addPost.setVisible(false);
            logIn.setVisible(false);
            logOut.setVisible(true);
            editPost.setVisible(false);
            editProfile.setVisible(true);
        }


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == R.id.action_logout){
            mAuth.signOut();
            startActivity(new Intent(PostSingleActivity.this,LoginActivity.class));
        }
        if(item.getItemId() == R.id.action_login){
            startActivity(new Intent(PostSingleActivity.this,LoginActivity.class));
        }
        if(item.getItemId() == R.id.edit_profile){
            startActivity(new Intent(PostSingleActivity.this,ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}