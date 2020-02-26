package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    TextView title;
    EditText fName,lName,company,pNo,address,position,website,industry;
    Button btnUpload;
    ImageView frontView, backView;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    int i;
    int FRONT_VIEW = 2;
    int BACK_VIEW = 3;

    String firstName,lastName,workAddress,companyName,userPosition,companySite,userIndustry;
    int phoneNumber;
    private ProgressBar progressBar;

    static ArrayList<Uri> ImageList = new ArrayList<Uri>();
    List<String> user_conn_list = new ArrayList<>();

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        title = findViewById(R.id.title);
        fName = findViewById(R.id.fName);
        lName = findViewById(R.id.lName);
        company =findViewById(R.id.company);
        position = findViewById(R.id.position);
        pNo = findViewById(R.id.pNo);
        address = findViewById(R.id.address);
        website = findViewById(R.id.url);
        industry = findViewById(R.id.industry);
        frontView = findViewById(R.id.visiting_front);
        backView = findViewById(R.id.visiting_back);
        btnUpload = findViewById(R.id.upload);

        progressBar = new ProgressBar(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnUpload.setEnabled(false);

        final Intent intent = CropImage.activity()
                .getIntent(CreateActivity.this);

        frontView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent, FRONT_VIEW);
            }
        });

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent, BACK_VIEW);
            }
        });

        if (validateFName() | validateLName() | validateCompanyName() | validatePhoneNumber() | validatePosition()) {
            btnUpload.setEnabled(true);
        }

        // UPLOAD THE DATA THAT WAS FILLED IN THE FIELDS BY THE USER
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                companySite = website.getText().toString().trim();
                userIndustry = industry.getText().toString().trim();
                btnUpload.setEnabled(true);
                Map<Object, Object> user = new HashMap<>();
                user.put("fName", firstName);
                user.put("lName", lastName);
                user.put("company", companyName);
                user.put("pNo", phoneNumber);
                user.put("address", workAddress);
                user.put("position", userPosition);
                user.put("eMail", mAuth.getCurrentUser().getEmail());
                user.put("website", companySite);
                user.put("industry", userIndustry);
                user.put("connection", user_conn_list);
//               user.put("profilePic",getPic.getSerializableExtra("pic"));
                uploadImage(user);

                db.collection("user").document(mAuth.getCurrentUser().getUid())
                        .set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(CreateActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", String.valueOf(requestCode));
        if(resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(requestCode == FRONT_VIEW){
                Uri frontCard = result.getUri();
                ImageList.add(0,frontCard);
                Picasso.get().load(ImageList.get(0)).into(frontView);
            }
            if(requestCode == BACK_VIEW){
                Uri backCard = result.getUri();
                ImageList.add(1,backCard);
                Picasso.get().load(ImageList.get(1)).into(backView);
            }
        }
    }

    private void uploadImage(final Map<Object, Object> user){
        StorageReference Ref = FirebaseStorage.getInstance().getReference();
        for(i = 0; i<ImageList.size();i++){
            Uri image = ImageList.get(i);
            final StorageReference imageName = Ref.child("Image"+ image.getLastPathSegment());
            imageName.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = String.valueOf(uri);
                            Log.e("pics",url);
                            StoreLink(url,user,i);
                        }
                    });
                }
            });
        }
    }

    private void StoreLink(String url, Map<Object, Object> user, int i) {
        if(i == 0){
            user.put("front",url);
        }else if(i == 1){
            user.put("back",url);
        }
        db.collection("user").document(mAuth.getCurrentUser().getUid())
                .set(user, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreateActivity.this,"uploaded",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateActivity.this,"Failed to upload",Toast.LENGTH_LONG).show();
            }
        });
    }

    // VALIDATE ALL FIELD INPUTS
    private boolean validateFName(){
        String firstName = fName.getText().toString().trim();
        if(firstName.isEmpty()){
            fName.setError("First Name Cannot be empty!!!");
            return false;
        }else{
            fName.setError(null);
            return true;
        }
    }

    private boolean validateLName(){
        String lastName = lName.getText().toString().trim();
        if(lastName.isEmpty()){
            lName.setError("First Name Cannot be empty!!!");
            return false;
        }else{
            lName.setError(null);
            return true;
        }
    }

    private boolean validatePhoneNumber(){
        String phoneNumber =  pNo.getText().toString().trim();
        if(phoneNumber.isEmpty()){
            pNo.setError("Number Cannot be Empty");
            return false;
        }else{
            pNo.setError(null);
            return true;
        }
    }

    private boolean validateCompanyName(){
        String companyName = company.getText().toString().trim();
        if(companyName.isEmpty()){
            company.setError("Company Name is required");
            return false;
        }else {
            company.setError(null);
            return true;
        }
    }

    private boolean validatePosition(){
        String userPosition = position.getText().toString().trim();
        if(userPosition.isEmpty()){
            position.setError("Position Cannot be empty");
            return false;
        }else{
            position.setError(null);
            return true;
        }
    }


}