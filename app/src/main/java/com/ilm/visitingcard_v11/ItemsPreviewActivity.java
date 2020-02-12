package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ItemsPreviewActivity extends AppCompatActivity {

    ImageView profilePic,cardFront,cardBack;
    TextView userName,userPosition,userMail,userAddress,userPhone,userCompany,userWno;
    ItemsModel itemsModel;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        userCompany = findViewById(R.id.conn_company);
        userWno = findViewById(R.id.conn_work_phone);
        cardFront = findViewById(R.id.front);
        cardBack = findViewById(R.id.back);

        //TODO: View user profile from the list
        final Intent intent = getIntent();
        if(intent.getSerializableExtra("items") != null){
            itemsModel = (ItemsModel) intent.getSerializableExtra("items");
            Picasso.get().load(itemsModel.getProfilePic()).into(profilePic);
            Picasso.get().load(itemsModel.getFront()).into(cardFront);
            Picasso.get().load(itemsModel.getBack()).into(cardBack);
            userName.setText(Objects.requireNonNull(itemsModel).getfName()+" "+Objects.requireNonNull(itemsModel).getlName());
            userMail.setText(Objects.requireNonNull(itemsModel).geteMail());
            userPosition.setText(Objects.requireNonNull(itemsModel).getAddress());
            userCompany.setText(Objects.requireNonNull(itemsModel).getCompany());
            userPhone.setText(String.valueOf(Objects.requireNonNull(itemsModel).getpNo()));
            userWno.setText(String.valueOf(Objects.requireNonNull(itemsModel).getwNo()));
        }
        //TODO: Get user profile after scanning
        Intent qrIntent = getIntent();
        if(qrIntent.getSerializableExtra("id")!= null){
            String userID = (String) qrIntent.getSerializableExtra("id");
            Log.e("ID",userID);
            db.collection("user").document(userID)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        ItemsModel itemsModel = doc.toObject(ItemsModel.class);
                        Picasso.get().load(itemsModel.getFront()).into(cardFront);
                        Picasso.get().load(itemsModel.getBack()).into(cardBack);
                        userName.setText(Objects.requireNonNull(itemsModel).getfName());
                        userMail.setText(Objects.requireNonNull(itemsModel).geteMail());
                        userPosition.setText(Objects.requireNonNull(itemsModel).getPosition());
//                        userCompany.setText(itemsModel.getCompany());
                        Log.e("TAG", "success");
                    }else{
                        Log.e("TAG", "Fail");
                    }
                }
            });

            db.collection("user").document(mAuth.getCurrentUser().getUid()).update("conn", FieldValue.arrayUnion(userID));
        }

        //TODO:On Click Activity for Phone Numbers and Email Address

        userMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{itemsModel.geteMail()});
                startActivity(emailIntent);
            }
        });

        userPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+itemsModel.getpNo()));
                startActivity(intent);
            }
        });

        userWno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent workPhone = new Intent(Intent.ACTION_DIAL);
                workPhone.setData(Uri.parse("tel:"+itemsModel.getwNo()));
                startActivity(workPhone);
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        startActivity(new Intent(ItemsPreviewActivity.this, NavigationActivity.class));
    }
}
