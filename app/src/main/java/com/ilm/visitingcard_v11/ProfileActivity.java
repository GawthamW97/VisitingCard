package com.ilm.visitingcard_v11;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    ImageView profilePic,cardFront,cardBack;
    TextView userName,userPosition,userMail,userAddress,userPhone;
    ItemsModel itemsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
