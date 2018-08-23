package com.example.chien.photoshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chien.photoshare.Photo;
import com.example.chien.photoshare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

/**
 * Created by chien on 5/2/2017.
 * TODO: implement user interface with account photos
 */



public class AccountActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button btnSignOut;
    private TextView userId;
    private BottomNavigationView mBottomNav;

    public Photo currentPhotoUser;
    public String currentPhotoUserId;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        userId = (TextView) findViewById(R.id.userIDTextView);
        btnSignOut = (Button) findViewById(R.id.signOutButton);
        mAuth = FirebaseAuth.getInstance();

        mBottomNav = (BottomNavigationView) findViewById(R.id.navigation);

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.camera){

                    //get currentUser
                    currentPhotoUserId = getIntent().getStringExtra("userID");
                    Intent intent = new Intent(getBaseContext(), UploadActivity.class);
                    intent.putExtra("userID" , currentPhotoUserId);
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.account){
                    return true;
                }
                else if(item.getItemId() == R.id.menu_home){

                    currentPhotoUserId = getIntent().getStringExtra("userID");
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("userID", currentPhotoUserId);
                    startActivity(intent);
                }
                else{
                    return false;
                }

                return true;
            }
        });

        currentPhotoUserId = getIntent().getStringExtra("userID");
        Photo userPhoto = new Photo();
        //userPhoto.getPhotoUrl();

        userId.setText("User ID: " + currentPhotoUserId);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
                toastMessage("Signing out...");

            }
        });
    }


    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
