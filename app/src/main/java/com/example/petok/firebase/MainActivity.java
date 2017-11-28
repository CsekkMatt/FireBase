package com.example.petok.firebase;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "";
    private DatabaseReference mDatabase;
    private DatabaseReference journalCloudEndPoint;
    private DatabaseReference tagCloudEndPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            journalCloudEndPoint = mDatabase.child("journalentris");
            tagCloudEndPoint = mDatabase.child("tags");
            journalCloudEndPoint.setValue("Valami");

            journalCloudEndPoint.setValue("Valami").addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, e.getLocalizedMessage());
                    
                }

            });

            Log.d(TAG,"Succes");
        }catch(Exception e){
            Log.d(TAG,"Failure");
        }
        setContentView(R.layout.activity_main);

    }
}
