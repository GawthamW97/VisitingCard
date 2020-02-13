package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity{

    private ImageView userImage;
    private  EditText userMail;
    private EditText password;
    private EditText re_passsword;
    private Button regBtn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

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
                                        startActivity(new Intent(getApplicationContext(),EditActivity.class));
                                        Toast.makeText(RegisterActivity.this,"Registration Complete",Toast.LENGTH_LONG).show();
                                    }else {
                                        Toast.makeText(RegisterActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });



    }

}
