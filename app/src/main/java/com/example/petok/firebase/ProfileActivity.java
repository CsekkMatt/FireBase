package com.example.petok.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.core.Context;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;


public class ProfileActivity extends AppCompatActivity {

    GoogleApiClient mGoogleApiClient;

    private ImageButton mSetupImageBtn;
    private EditText mNameField,mPhoneField;
    private Button mSubmitBtn;

    private Uri mImageUri = null;
    private static final int GALLERY_REQUEST = 1;

    private DatabaseReference mDatabaseUsers;
    private StorageReference mStorageImage;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorageImage = FirebaseStorage.getInstance().getReference().child("Profile_images");

        mProgress = new ProgressDialog(this);

        mSetupImageBtn = (ImageButton) findViewById(R.id.setProfilePicture);
        mNameField = (EditText) findViewById(R.id.setupNameField);
        mPhoneField = (EditText) findViewById(R.id.setupPhoneNumberField);
        mSubmitBtn = (Button) findViewById(R.id.setupSubmitBtn);
        mDatabaseUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                mNameField.setText(user.getName());
                mPhoneField.setText(user.getPhone());
                String prof_image = (String) dataSnapshot.child("image").getValue();

                Picasso.with(getApplicationContext()).load(prof_image).into(mSetupImageBtn);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mNameField.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            mPhoneField.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            Uri prof_picture = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
            Picasso.with(getApplicationContext()).load(prof_picture).into(mSetupImageBtn);
        } else {


        }*/



        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSetupAccount();
                Toast.makeText(ProfileActivity.this,"onclick",Toast.LENGTH_SHORT).show();
            }
        });

        mSetupImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);

            }
        });

    }

    private void startSetupAccount() {

        final String name = mNameField.getText().toString().trim();
       final String phone = mPhoneField.getText().toString().trim();
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone) && mImageUri != null){

            StorageReference filePath = mStorageImage.child(mImageUri.getLastPathSegment());
            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    mProgress.setMessage("Saving Changes...");

                    mProgress.show();
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    mDatabaseUsers.child(uid).child("name").setValue(name);
                    mDatabaseUsers.child(uid).child("phone").setValue(phone);
                    mDatabaseUsers.child(uid).child("image").setValue(downloadUri);

                    mProgress.dismiss();
                    startActivity(new Intent(ProfileActivity.this,HomeActivity.class));
                }

            });

        }

    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        //Toast.makeText(ProfileActivity.this,"Belepett",Toast.LENGTH_SHORT).show();

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
          //  Toast.makeText(ProfileActivity.this, "Gallery es ok ", Toast.LENGTH_SHORT).show();
        }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                //Toast.makeText(ProfileActivity.this,"2Belepes",Toast.LENGTH_SHORT).show();

                if (resultCode == RESULT_OK) {
                    mImageUri = result.getUri();
                    mSetupImageBtn.setImageURI(mImageUri);
                    //Toast.makeText(ProfileActivity.this, "OK", Toast.LENGTH_SHORT).show();

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                  //  Toast.makeText(ProfileActivity.this, "RESULT", Toast.LENGTH_SHORT).show();
                }
            }

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
            editProfile.setVisible(false);
        }


        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
        }
        if(item.getItemId() == R.id.action_login){
            startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
