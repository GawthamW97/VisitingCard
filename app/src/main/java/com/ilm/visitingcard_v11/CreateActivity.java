package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    EditText fName,lName,company,pNo,wNo,address,position,website,industry;
    Button btnUpload;
    ImageView frontView, backView;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    int i;
    int FRONT_VIEW = 20;
    int BACK_VIEW = 30;

    static ArrayList<String> urlList = new ArrayList<>();
    String firstName,lastName,workAddress,companyName,userPosition,companySite,userIndustry;
    int phoneNumber,workNumber;
    private ProgressBar progressBar;

    static ArrayList<Uri> ImageList = new ArrayList<Uri>();
    List<String> user_conn_list = new ArrayList<>();

    StorageReference Ref = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        progressBar = findViewById(R.id.progress);

        title = findViewById(R.id.title);
        fName = findViewById(R.id.fName);
        lName = findViewById(R.id.lName);
        company =findViewById(R.id.company);
        position = findViewById(R.id.position);
        pNo = findViewById(R.id.pNo);
        wNo = findViewById(R.id.wNo);
        address = findViewById(R.id.address);
        website = findViewById(R.id.url);
        industry = findViewById(R.id.industry);
        frontView = findViewById(R.id.visiting_front);
        backView = findViewById(R.id.visiting_back);
        btnUpload = findViewById(R.id.upload);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressBar.setVisibility(View.INVISIBLE);

        // On Image Click Open Gallery and crop selected image for Front Card View
        frontView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ImageList.isEmpty()) {               //If ImageList is empty this is the first image selected by the user
                    Intent intent = CropImage.activity()
                            .getIntent(CreateActivity.this);
                    startActivityForResult(intent, FRONT_VIEW);
                }else{
                    Intent intent = CropImage.activity()
                            .getIntent(CreateActivity.this).putExtra("pic",ImageList.get(1));
                    startActivityForResult(intent, FRONT_VIEW);
                }
            }
        });

        // On Image Click Open Gallery and crop selected image for Back Card View

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ImageList.isEmpty()) {               // If ImageList is empty this is the first image selected by the user
                    Intent intent = CropImage.activity()
                            .getIntent(CreateActivity.this);
                    startActivityForResult(intent, BACK_VIEW);
                }else{
                    Intent intent = CropImage.activity()
                            .getIntent(CreateActivity.this).putExtra("pic",ImageList.get(0));
                    startActivityForResult(intent, BACK_VIEW);
                }
            }
        });


        // UPLOAD THE DATA THAT WAS FILLED IN THE FIELDS BY THE USER
        fName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(validateFName()){
                    firstName = String.valueOf(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(validateLName()){
                    lastName = String.valueOf(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        company.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(validateCompanyName()){
                    companyName = String.valueOf(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnUpload.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                lastName = lName.getText().toString().trim();
                companySite = website.getText().toString().trim();
                userIndustry = industry.getText().toString().trim();
                if(pNo.getText().toString().trim().isEmpty()){
                    phoneNumber = 0;
                }else {
                    phoneNumber = Integer.parseInt(pNo.getText().toString().trim());
                }
                if(wNo.getText().toString().trim().isEmpty()){
                    workNumber = 0;
                }else{
                    workNumber = Integer.parseInt(wNo.getText().toString());
                }
                companyName = company.getText().toString().trim();
                userPosition = position.getText().toString().trim();
                workAddress = address.getText().toString().trim();

                if (validateFName() | validateLName() | validateCompanyName()) {
                    // Create Map to store inserted values from the fields and use to upload it to the firebase
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
                    user.put("conn", user_conn_list);
                    uploadImage(user);                  //Upload card Images to the firebase

                    //Upload the HashMap to the Firebase FireStore
                    db.collection("user").document(mAuth.getCurrentUser().getUid())
                            .set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(CreateActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                            btnUpload.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            finish();
                            startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                            btnUpload.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }else{
                    btnUpload.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        if(resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data); //Get cropped image
            if(requestCode == FRONT_VIEW && intent.getSerializableExtra("pic") == null && ImageList.isEmpty()){ //If front view of the card is selected by the user aa the first image
                Uri frontCard = result.getUri();
                ImageList.add(0,frontCard);                         //Add id to the ImageList
                Picasso.get().load(ImageList.get(0)).into(frontView);     //Set the ImageView with cropped image in the layout
            }else if (requestCode == FRONT_VIEW){                //If the user selects the Front Card as the second image
                Uri frontCard = result.getUri();
                ImageList.add(0,frontCard);
                Picasso.get().load(ImageList.get(0)).into(frontView);   //Set both image views with the images
                Picasso.get().load(ImageList.get(1)).into(backView);    // From ImageList ArrayList
            }

            if(requestCode == BACK_VIEW && intent.getSerializableExtra("pic") == null && ImageList.isEmpty()){  //If the user selects the Front Card as the first image
                Uri backCard = result.getUri();
                ImageList.add(1,backCard);                          //Add id to the ImageList
                Picasso.get().load(ImageList.get(1)).into(backView);       //Set the ImageView with cropped image in the layout
            }else if (requestCode == BACK_VIEW){        //If the user selects the Front Card as the second image
                Uri backCard = result.getUri();
                ImageList.add(1,backCard);
                Picasso.get().load(ImageList.get(0)).into(frontView);   //Set both image views with the images
                Picasso.get().load(ImageList.get(1)).into(backView);    // From ImageList ArrayList
            }
        }
    }

    private void uploadImage(final Map<Object, Object> user){
        StorageReference imageName;
        for(i = 0; i<ImageList.size();i++){
            final Uri image = ImageList.get(i);
            if(i == 0) {
                imageName = Ref.child(mAuth.getCurrentUser().getUid()+"/FrontView");
            }else{
                imageName = Ref.child(mAuth.getCurrentUser().getUid()+"/BackView");
            }
            final StorageReference finalImageName = imageName;
            imageName.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    finalImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("TAG",String.valueOf(i));
                            String url = String.valueOf(uri);
                            StoreLink(url,user);
                        }
                    });
                }
            });
        }
    }

    private void StoreLink(String url, Map<Object, Object> user) {
        urlList.add(url);
        if(urlList.size() == 2) {
            user.put("front",urlList.get(0));
            user.put("back",urlList.get(1));
            db.collection("user").document(mAuth.getCurrentUser().getUid())
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CreateActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateActivity.this, "Failed to upload", Toast.LENGTH_LONG).show();
                }
            });
        }
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