package com.example.petok.firebase;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        String encodedPost_key = mPost_key.substring(mPost_key.lastIndexOf("/") + 1);

         mPostSingleDesc = (TextView)findViewById(R.id.single_post_desc);
         mPostSingleTitle = (TextView)findViewById(R.id.single_post_title);
         mPostSingleImage = (ImageView)findViewById(R.id.single_post_image);

        mDatabase.child(encodedPost_key).addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 String post_title = (String) dataSnapshot.child("Title").getValue();
                 String post_desc = (String) dataSnapshot.child("Desc").getValue();
                 String post_image = (String) dataSnapshot.child("image").getValue();

                 mPostSingleTitle.setText(post_title);
                 mPostSingleDesc.setText(post_desc);
                 Picasso.with(PostSingleActivity.this).load(post_image).into(mPostSingleImage);
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });

       // Toast.makeText(PostSingleActivity.this,mPost_key,Toast.LENGTH_SHORT).show();
    }
}
