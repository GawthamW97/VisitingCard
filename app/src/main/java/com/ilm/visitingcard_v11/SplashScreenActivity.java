package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();

//        mAuth.signOut();
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        }
        else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
