package com.example.petok.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "";
    private DatabaseReference mDatabase;

//https://console.cloud.google.com/storage/settings?project=fir-4f1d6
    private ImageButton mSelectImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitBtn;

    private Uri mImageUri = null;

    private static final int GALLERY_REQUEST = 1;
    private StorageReference mStorage;

    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSelectImage = (ImageButton)findViewById(R.id.imageSelect);
        mPostTitle = (EditText)findViewById(R.id.titleField);
        mPostDesc = (EditText)findViewById(R.id.descField);

        mSubmitBtn = (Button)findViewById(R.id.submitBtn);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");



        mProgress = new ProgressDialog(this);

        mSelectImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);

            }
        });

       mSubmitBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
              startPosting();
           }
       });

    }

    private void startPosting() {

        mProgress.setMessage("Uploading...");
        final String title_val = mPostTitle.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();


        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri != null){
            mProgress.show();

            StorageReference filepath = mStorage.child("Images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost  = mDatabase.push();

                    newPost.child("Title").setValue(title_val);
                    newPost.child("Desc").setValue(desc_val);
                    newPost.child("image").setValue(downloadUrl.toString());
                   // newPost.child("email").setValue(email);
                  //  newPost.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());

                    mProgress.dismiss();

                    startActivity(new Intent(PostActivity.this,HomeActivity.class));
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            mImageUri = data.getData();

            mSelectImage.setImageURI(mImageUri);
        }
    }

}

