package com.example.chien.photoshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Skip Log in if user is already signed in
 *
 * Created by chien on 4/30/2017.
 */

public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "onStart";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmail;
    private EditText mPassword;
    private Button btnSignIn;
    private Button btnSignUp;
    public Photo photoUser;
    public Photo photoLoggedInUser;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = (EditText) findViewById(R.id.IdEditText);
        mPassword = (EditText)findViewById(R.id.passwordEditText);
        btnSignIn = (Button) findViewById(R.id.signInButton);
        btnSignUp = (Button) findViewById(R.id.signUpButton);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String email = user.getEmail();
                    String userID = getID(email);
                     photoLoggedInUser = new Photo(userID);
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());

                    Intent intent = new Intent(getBaseContext(),MainActivity.class);
                    intent.putExtra("userID", userID);
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };

        btnSignUp.setOnClickListener(new View.OnClickListener(){

            
            @Override
            public void onClick(View view){
                mAuth.signOut();
                toastMessage("Signing out...");

            }
                                      }
        );

        btnSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                photoUser = new Photo();
                Intent intent = new Intent(view.getContext(),MainActivity.class);
                String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();
                String userID = getID(email);
                photoUser.setUserId(userID);


                if(!email.equals("")&&!pass.equals("")){
                    mAuth.signInWithEmailAndPassword(email,pass);
                    intent.putExtra("userID", userID);
                    view.getContext().startActivity(intent);

                }
                else{
                    toastMessage("Please fill in all fields required.");
                }
            }


        });
    }


    /**
     * retrieves userID from email
     * @param email
     * @return userID
     */
    public String getID(String email){
        String array [] = email.split("@");
        return array[0];
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}
