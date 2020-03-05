package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
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
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity{

    private ImageView userImage;
    private  EditText userMail,password,re_passsword;
    private Button regBtn;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressBar progressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Uri profilePic;
    String eMail,pwd,pwdRe;
    ArrayList<Uri> ImageList = new ArrayList<>();
    Map<String,Object> user = new HashMap<>();

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 4 characters
                    "$");

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

        userMail.addTextChangedListener(new TextWatcher() {                 //Check for each value inserted by the user
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(validateMail()){
                    eMail = String.valueOf(s);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {              //Check for each value inserted by the user
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(validatePassword()){
                    pwd = String.valueOf(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        re_passsword.addTextChangedListener(new TextWatcher() {              //Check for each value inserted by the user
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(validateRePassword()){
                    pwdRe = String.valueOf(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //on button click upload the data to
            regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //REGISTER USER TO THE FIREBASE
                regBtn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                if(validateMail()|validatePassword()|validateRePassword()){     // Validate input-fields
                    mAuth.createUserWithEmailAndPassword(eMail, pwd)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if(task.isSuccessful()){
                                        uploadImage();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        regBtn.setVisibility(View.VISIBLE);
                                        startActivity(new Intent(RegisterActivity.this, CreateActivity.class));
                                        Toast.makeText(RegisterActivity.this,"Registration Complete",Toast.LENGTH_LONG).show();
                                    }else{
                                        progressBar.setVisibility(View.INVISIBLE);
                                        regBtn.setVisibility(View.VISIBLE);
                                        Toast.makeText(RegisterActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    regBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(RegisterActivity.this,"Fill the Fields with proper values",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateMail() {
        String eMail = userMail.getText().toString().trim();
        if (TextUtils.isEmpty(eMail)) {
            userMail.setError("Field cannot be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(eMail).matches()) {
            userMail.setError("Email address is not valid");
            return false;
        } else {
            userMail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String pwd = password.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            password.setError("Field cannot be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(pwd).matches()) {
            password.setError("Password is too week: It must have 6 character and at least 1 special character");
            return false;
        } else {
            userMail.setError(null);
            return true;
        }
    }
    private boolean validateRePassword() {
        String pwd = password.getText().toString().trim();
        String pwdRe = re_passsword.getText().toString().trim();
        if(TextUtils.isEmpty(pwdRe)){
            re_passsword.setError("Field cannot be empty");
            return false;
        }else if(!pwd.equals(pwdRe)){
            re_passsword.setError("Password Fields are mismatching");
            return false;
        }else{
            userMail.setError(null);
            return true;
        }
    }

    private void openGallery() {
        CropImage.activity().start(RegisterActivity.this);
    }

    private void uploadImage(){
        StorageReference Ref = FirebaseStorage.getInstance().getReference();
        for(int i = 0; i < ImageList.size(); i++){
            Uri image = ImageList.get(i);
            final StorageReference imageName = Ref.child(mAuth.getCurrentUser().getUid()+"/ProfilePicture");
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
        user.put("pPic",url);
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
