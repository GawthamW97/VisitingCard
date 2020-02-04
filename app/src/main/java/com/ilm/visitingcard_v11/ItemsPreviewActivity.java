package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ItemsPreviewActivity extends AppCompatActivity {

    ImageView profilePic,cardFront,cardBack;
    TextView userName,userPosition,userMail,userAddress,userPhone;
    ItemsModel itemsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_preview);

        profilePic = findViewById(R.id.user_image);
        userName = findViewById(R.id.conn_name);
        userMail = findViewById(R.id.conn_mail);
        userPhone = findViewById(R.id.conn_phone);
        userPosition = findViewById(R.id.conn_position);
        userAddress = findViewById(R.id.conn_location);
        cardFront = findViewById(R.id.front);
        cardBack = findViewById(R.id.back);

        Intent intent = getIntent();
        if(intent.getSerializableExtra("items") != null){
            itemsModel = (ItemsModel) intent.getSerializableExtra("items");
            Picasso.get().load(itemsModel.getProfilePic()).into(profilePic);
            Picasso.get().load(itemsModel.getFront()).into(cardFront);
            Picasso.get().load(itemsModel.getBack()).into(cardBack);
            userName.setText(Objects.requireNonNull(itemsModel).getfName()+" "+Objects.requireNonNull(itemsModel).getlName());
            userMail.setText(Objects.requireNonNull(itemsModel).geteMail());
            userPosition.setText(Objects.requireNonNull(itemsModel).getAddress());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ItemsPreviewActivity.this, NavigationActivity.class));
    }
}
