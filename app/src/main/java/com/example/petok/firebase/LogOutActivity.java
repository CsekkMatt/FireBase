package com.example.petok.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogOutActivity extends AppCompatActivity {

    Button button;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser myUser;

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_out);

        button = (Button) findViewById(R.id.logout);
        Button btnList = (Button) findViewById(R.id.list);
        mAuth = FirebaseAuth.getInstance();
        myUser = mAuth.getCurrentUser();
        final String ui = myUser.getEmail();
        Toast.makeText(LogOutActivity.this,ui, Toast.LENGTH_SHORT).show();



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){

                    startActivity(new Intent(LogOutActivity.this,HomeActivity.class));
                }
            }
        };
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });

        btnList.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View view){
                startActivity(new Intent(LogOutActivity.this,PostActivity.class));

            }
        });
    }
}
