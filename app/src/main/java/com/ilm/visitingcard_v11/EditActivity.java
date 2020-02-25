package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    TextView title;
    EditText fName,lName,company,pNo,address,position,website,industry;
    Button btnUpload, images;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    int i;

    private ProgressBar progressBar;

    ArrayList<Uri> ImageList = new ArrayList<Uri>();

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        title = findViewById(R.id.title);
        fName = findViewById(R.id.fName);
        lName = findViewById(R.id.lName);
        company =findViewById(R.id.company);
        position = findViewById(R.id.position);
        pNo = findViewById(R.id.pNo);
        address = findViewById(R.id.address);
        website = findViewById(R.id.url);
        industry = findViewById(R.id.industry);
        images = findViewById(R.id.selectImage);
        btnUpload = findViewById(R.id.upload);

        progressBar = new ProgressBar(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // UPLOAD THE DATA THAT WAS FILLED IN THE FIELDS BY THE USER
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = fName.getText().toString().trim();
                String lastName = lName.getText().toString().trim();
                String workAddress = address.getText().toString().trim();
                int phoneNumber = Integer.parseInt(pNo.getText().toString().trim());
                String companyName = company.getText().toString().trim();
                String userPosition = position.getText().toString().trim();
                String companySite = website.getText().toString().trim();
                String userIndustry = industry.getText().toString().trim();
                List<String> user_conn_list = new ArrayList<>();

                Map<Object,Object> user = new HashMap<>();
                user.put("fName",firstName);
                user.put("lName",lastName);
                user.put("company",companyName);
                user.put("pNo",phoneNumber);
                user.put("address",workAddress);
                user.put("position",userPosition);
                user.put("eMail",mAuth.getCurrentUser().getEmail());
                user.put("website",companySite);
                user.put("industry",userIndustry);
                user.put("connection",user_conn_list);
//                user.put("profilePic",getPic.getSerializableExtra("pic"));
                uploadImage(user);

                db.collection("user").document(mAuth.getCurrentUser().getUid())
                        .set(user,SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(EditActivity.this,"Successfully Updated",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),NavigationActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditActivity.this,"Failed to Update",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == 1 && data != null){
            imageUri = data.getData();
            ImageList.add(imageUri);
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
                        Toast.makeText(EditActivity.this,"uploaded",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditActivity.this,"Failed to upload",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,1);
    }

    // VALIDATE ALL FIELD INPUTS
    private boolean validateInput(String firstName,String lastName,String companyName,String phoneNo,String userAddress,String currentPosition){
        if(firstName.isEmpty()){
            fName.setError("Cannot be Empty");
            return true;
        }

        if(lastName.isEmpty()){
            lName.setError("Cannot be Empty");
            return true;
        }

        if(companyName.isEmpty()){
            company.setError("Cannot be Empty");
            return true;
        }

        if(phoneNo.isEmpty()){
            pNo.setError("Cannot be Empty");
            return true;
        }

        if(userAddress.isEmpty()){
            address.setError("Cannot be Empty");
            return false;
        }

        if(currentPosition.isEmpty()){
            position.setError("Cannot be Empty");
            return true;
        }
        return false;
    }
}
