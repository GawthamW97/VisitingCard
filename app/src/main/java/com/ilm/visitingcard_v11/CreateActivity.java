package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    ImageView cardFront, cardBack, profilePic;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Animation myAnim;
    int i;
    int CODE_IMAGE_GALLERY = 10;
    int FRONT_IMG_GALLERY = 20;
    int BACK_IMG_GALLERY = 30;

    ArrayList<Uri> img_collection = new ArrayList<>();
    static ArrayList<String> urlList = new ArrayList<>();
    String firstName,lastName,workAddress,companyName,userPosition,companySite,userIndustry;
    int phoneNumber,workNumber;
    private ProgressBar progressBar;

    static Map<Object,Uri> ImageList = new HashMap<>();
    List<String> user_conn_list = new ArrayList<>();

    StorageReference Ref = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        progressBar = findViewById(R.id.progress);

        profilePic = findViewById(R.id.profilePic);
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
        cardFront = findViewById(R.id.visiting_front);
        cardBack = findViewById(R.id.visiting_back);
        btnUpload = findViewById(R.id.upload);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressBar.setVisibility(View.INVISIBLE);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//If ImageList is empty this is the first image selected by the user
                    Intent intent = CropImage.activity()
                            .getIntent(CreateActivity.this);
                    startActivityForResult(intent, CODE_IMAGE_GALLERY);
            }
        });

        // On Image Click Open Gallery and crop selected image for Front Card View
        cardFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//If ImageList is empty this is the first image selected by the user
                    Intent intent = CropImage.activity()
                            .getIntent(CreateActivity.this);
                    startActivityForResult(intent, FRONT_IMG_GALLERY);
            }
        });

        // On Image Click Open Gallery and crop selected image for Back Card View

        cardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If ImageList is empty this is the first image selected by the user
                    Intent intent = CropImage.activity()
                            .getIntent(CreateActivity.this);
                    startActivityForResult(intent, BACK_IMG_GALLERY);
            }
        });


        // UPLOAD THE DATA THAT WAS FILLED IN THE FIELDS BY THE USER
        fName.addTextChangedListener(new TextWatcher() {                 //Check for each value inserted by the user
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
        lName.addTextChangedListener(new TextWatcher() {                 //Check for each value inserted by the user
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
        company.addTextChangedListener(new TextWatcher() {                   //Check for each value inserted by the user
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

        //On Button click validate inserted data and if valid upload
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myAnim = AnimationUtils.loadAnimation(CreateActivity.this, R.anim.fade_out);
                btnUpload.startAnimation(myAnim);
                progressBar.setVisibility(View.VISIBLE);
                lastName = lName.getText().toString().trim();
                companySite = website.getText().toString().trim();
                userIndustry = industry.getText().toString().trim();

                // If the phone number and work number is not given set them to zero
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

                // Validate inserted data in the fields
                if (validateFName() | validateLName() | validateCompanyName()) {
                    // Create Map to store inserted values from the fields and use to upload it to the firebase
                    Map<Object, Object> user = new HashMap<>();
                    user.put("fN", firstName);
                    user.put("lN", lastName);
                    user.put("cmp", companyName);
                    user.put("pNo", phoneNumber);
                    user.put("adr", workAddress);
                    user.put("pos", userPosition);
                    user.put("eM", mAuth.getCurrentUser().getEmail());
                    user.put("web", companySite);
                    user.put("conn", user_conn_list);
                    uploadImage(user);                  //Upload card Images to the firebase

                    //Upload the HashMap to the Firebase FireStore
                    db.collection("user").document(mAuth.getCurrentUser().getUid())
                            .set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(CreateActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                            myAnim = AnimationUtils.loadAnimation(CreateActivity.this, R.anim.fade_in);
                            btnUpload.startAnimation(myAnim);
                            progressBar.setVisibility(View.INVISIBLE);
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));    //On uploading the data to database redirect to LoginActivity
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
                            myAnim = AnimationUtils.loadAnimation(CreateActivity.this, R.anim.fade_in);
                            btnUpload.startAnimation(myAnim);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }else{
                    myAnim = AnimationUtils.loadAnimation(CreateActivity.this, R.anim.fade_in);
                    btnUpload.startAnimation(myAnim);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode ==CODE_IMAGE_GALLERY) {           //when the user selects the profile picture
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri pic = result.getUri();
            if (ImageList.isEmpty()) {                                                          // if the profile pic is chosen first
                ImageList.put("pPic", pic);
                Picasso.get().load(pic).into(profilePic);
            } else {
                ImageList.put("pPic", pic);
                if (ImageList.get("front") == null && ImageList.get("back") != null) {          // if back card view is chosen before profile pic
                    Picasso.get().load(ImageList.get("pPic")).into(profilePic);
                    Picasso.get().load(ImageList.get("back")).into(cardBack);
                } else if (ImageList.get("front") != null && ImageList.get("back") == null) {   // if front card view is chosen before profile pic
                    Picasso.get().load(ImageList.get("pPic")).into(profilePic);
                    Picasso.get().load(ImageList.get("front")).into(cardFront);
                } else {                                                                        // if front card view and back back view are chosen before front card view
                    Picasso.get().load(ImageList.get("pPic")).into(profilePic);
                    Picasso.get().load(ImageList.get("front")).into(cardBack);
                    Picasso.get().load(ImageList.get("back")).into(cardBack);
                }
            }
        }
        if(resultCode == RESULT_OK && requestCode == FRONT_IMG_GALLERY) {               //when the user selects the front card view
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri frontCard = result.getUri();
            if (ImageList.isEmpty()) {                            //if front card view is chosen first
                ImageList.put("front", frontCard);
                Picasso.get().load(ImageList.get("front")).into(cardFront);
            } else if (ImageList.get("pPic") == null && ImageList.get("back") != null) {        // if back card view is chosen before front card view
                Log.e("TAG1111", "PASS");
                ImageList.put("front", frontCard);
                Picasso.get().load(ImageList.get("front")).into(cardFront);
                Picasso.get().load(ImageList.get("back")).into(cardBack);
            } else if (ImageList.get("pPic") != null && ImageList.get("back") == null) {        // if profile pic is chosen before front card view
                ImageList.put("front", frontCard);
                Picasso.get().load(ImageList.get("front")).into(cardFront);
                Picasso.get().load(ImageList.get("pPic")).into(profilePic);
            } else {
                ImageList.put("front", frontCard);                                              // if back card view and profile pic are chosen before front card view
                Picasso.get().load(ImageList.get("pPic")).into(profilePic);
                Picasso.get().load(ImageList.get("front")).into(cardFront);
                Picasso.get().load(ImageList.get("back")).into(cardBack);
            }
        }

        if(resultCode == RESULT_OK && requestCode == BACK_IMG_GALLERY){             //when the user selects the back card view
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri backCard = result.getUri();
            if (ImageList.isEmpty()) {                                                      //if back card view is chosen first
                ImageList.put("back", backCard);
                Picasso.get().load(ImageList.get("back")).into(cardBack);
            }else if (ImageList.get("pPic") == null && ImageList.get("front") != null) {    // if front card view is chosen before back card view
                Log.e("TAG1122", "PASS");
                ImageList.put("back", backCard);
                Log.e("TAG", ImageList.toString());
                Picasso.get().load(ImageList.get("front")).into(cardFront);
                Picasso.get().load(ImageList.get("back")).into(cardBack);
            }else if(ImageList.get("pPic") != null && ImageList.get("front") == null){      // if profile pic is chosen before back card view
                ImageList.put("back", backCard);
                Picasso.get().load(ImageList.get("back")).into(cardBack);
                Picasso.get().load(ImageList.get("pPic")).into(profilePic);
            }else{
                ImageList.put("back", backCard);                                            //if back card view and profile pic are chosen before front card view
                Picasso.get().load(ImageList.get("pPic")).into(profilePic);
                Picasso.get().load(ImageList.get("front")).into(cardFront);
                Picasso.get().load(ImageList.get("back")).into(cardBack);
            }
        }
    }

    private void uploadImage(final Map<Object, Object> user){   //Upload image to the Firebase Storage
        StorageReference imageName = null;
        //Load images from the HashMap to the ArrayList
        img_collection.add(0,ImageList.get("pPic"));
        img_collection.add(1,ImageList.get("front"));
        img_collection.add(2,ImageList.get("back"));
        for(i = 0; i<img_collection.size();i++){
            Uri image = null;
            Uri image1 = ImageList.get("pPic");
            Uri image2 = ImageList.get("front");
            Uri image3 = ImageList.get("back");
            if(i == 0 && img_collection.get(0) != null){        // If the profile pic is selected and cropped upload the image
                image = image1;
                imageName = Ref.child(mAuth.getCurrentUser().getUid()+"/pPic");
                final StorageReference finalImageName = imageName;
                imageName.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        finalImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.e("TAG",String.valueOf(i));
                                String url = String.valueOf(uri);
                                user.put("pPic",url);
                                StoreLink(user);
                            }
                        });
                    }
                });
            }
            if(i == 1 && img_collection.get(1) != null){        // If the front card image is selected and cropped upload the image
                image = image2;
                imageName = Ref.child(mAuth.getCurrentUser().getUid()+"/FrontView");
                final StorageReference finalImageName = imageName;
                imageName.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        finalImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.e("TAG",String.valueOf(i));
                                String url = String.valueOf(uri);
                                user.put("front",url);
                                StoreLink(user);
                            }
                        });
                    }
                });
            }
            if(i == 2 && img_collection.get(2) != null){ // If the back card image is selected and cropped upload the image
                image = image3;
                imageName = Ref.child(mAuth.getCurrentUser().getUid()+"/BackView");
                final StorageReference finalImageName = imageName;
                imageName.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        finalImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = String.valueOf(uri);
                                user.put("back",url);
                                StoreLink(user);
                            }
                        });
                    }
                });
            }
        }
    }

    private void StoreLink(Map<Object, Object> user) {      //Store image url from firebase Storage to firebase cloud storage
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

    // VALIDATE INPUT FIELDS
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