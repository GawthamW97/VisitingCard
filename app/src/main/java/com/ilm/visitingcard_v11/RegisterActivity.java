package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity{

    private ImageView userImage;
    private  EditText userMail;
    private EditText password;
    private EditText re_passsword;
    private Button regBtn;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressBar progressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Uri profilePic;
    ArrayList<Uri> ImageList = new ArrayList<>();
    Map<String,Object> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userMail = findViewById(R.id.input_mail);
        password = findViewById(R.id.input_password);
        re_passsword = findViewById(R.id.input_re_password);
        userImage = findViewById(R.id.user_image);
        regBtn = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.regProgressBar);

        mAuth = FirebaseAuth.getInstance();

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eMail = userMail.getText().toString().trim();
                String pwd = password.getText().toString().trim();
                String pwdRe = re_passsword.getText().toString().trim();

                if(TextUtils.isEmpty(eMail)){
                    Toast.makeText(RegisterActivity.this,"Enter mail",Toast.LENGTH_LONG).show();
                }

                if(TextUtils.isEmpty(pwd)){
                    Toast.makeText(RegisterActivity.this,"Enter password",Toast.LENGTH_LONG).show();
                }

                if(TextUtils.isEmpty(pwdRe)){
                    Toast.makeText(RegisterActivity.this,"re-enter password",Toast.LENGTH_LONG).show();
                }

                //REGISTER USER TO THE FIREBASE
                if(pwd.equals(pwdRe)){
                    mAuth.createUserWithEmailAndPassword(eMail, pwd)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if(task.isSuccessful()){
                                        Log.e("pic",profilePic.toString());
                                        uploadImage();
//                                        Log.e("pic", Objects.requireNonNull(user.get(1)).toString());
                                        startActivity(new Intent(RegisterActivity.this,EditActivity.class));
                                        Toast.makeText(RegisterActivity.this,"Registration Complete",Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(RegisterActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
    private void openGallery() {
        CropImage.activity().start(RegisterActivity.this);
    }

    private void uploadImage(){
        StorageReference Ref = FirebaseStorage.getInstance().getReference();
        for(int i = 0; i < ImageList.size(); i++){
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
                            StoreLink(url,user);
                        }
                    });
                }
            });
        }
    }

    private void StoreLink(String url, Map<String, Object> user) {
        user.put("profilePic",url);
        db.collection("user").document(mAuth.getCurrentUser().getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterActivity.this,"uploaded",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this,"Failed to upload",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                profilePic = result.getUri();
                Picasso.get().load(profilePic).into(userImage);
                ImageList.add(profilePic);
            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e =result.getError();
                Toast.makeText(this,"Possible Error is:"+ e,Toast.LENGTH_LONG).show();
            }
        }
    }

}
