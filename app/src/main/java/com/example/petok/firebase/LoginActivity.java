package com.example.petok.firebase;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
        FirebaseAuth mAuth;
        SignInButton btn;
        GoogleApiClient mGoogleApiClient;
        private final static int RC_SIGN_IN = 2;

        private EditText mLoginEmailField;
        private EditText mLoginPasswordField;
        private Button mLoginButton;

        private DatabaseReference mDatabase;
        private ProgressDialog mProgress;

        FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart(){
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgress = new ProgressDialog(this);

        btn  = (SignInButton) findViewById(R.id.googleBtn);
        Button guestbtn = (Button)findViewById(R.id.fbbtn);
        Button registerBtn = (Button)findViewById(R.id.register);
        mAuth = FirebaseAuth.getInstance();
        //login
        mLoginPasswordField = (EditText)findViewById(R.id.loginPasswordField);
        mLoginEmailField = (EditText)findViewById(R.id.loginEmailField);

        mLoginButton =  (Button)findViewById(R.id.loginBtn);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mLoginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkLogin();
            }
        });

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                signIn();
            }
        });
        guestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(firebaseAuth.getCurrentUser() != null){
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    }


            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder (this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this,"Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    private void checkLogin() {
        String email = mLoginEmailField.getText().toString().trim();
        String password = mLoginPasswordField.getText().toString().trim();
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        checkUserExist();
                    }
                    else{
                        Toast.makeText(LoginActivity.this,"Error login",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void checkUserExist() {
        mProgress.setMessage("Logging in...");
        mProgress.show();
        final String user_id  = mAuth.getCurrentUser().getUid();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user_id)){
                    mProgress.dismiss();
                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgress.dismiss();
                Toast.makeText(LoginActivity.this,"You need to setup your account",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }else {
                Toast.makeText(LoginActivity.this,"Auth went wrong", Toast.LENGTH_SHORT).show();

            }
            }
        }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
       // Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());


        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());


                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
}

