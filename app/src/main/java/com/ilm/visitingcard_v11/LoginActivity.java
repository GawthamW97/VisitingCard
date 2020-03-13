package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ilm.visitingcard_v11.Animation.MyBounceInterpolator;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {

    private EditText userMail,userPassword;
    private Button btnLogin,btnRegister;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    private TextView resetPwd;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        userMail = findViewById(R.id.login_mail);
        userPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.loginBtn);
        btnRegister = findViewById(R.id.registerBtn);
        loginProgress = findViewById(R.id.login_progress);
        mAuth = FirebaseAuth.getInstance();
        resetPwd = findViewById(R.id.forgot_pwd);

        loginProgress.setVisibility(View.INVISIBLE);


        resetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
            }
        });

        //ON BUTTON CLICK VERIFY THE FIELDS AND INPUTS
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = findViewById(R.id.loginBtn);
                Animation myAnim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.bounce);
                MyBounceInterpolator interpolator = new MyBounceInterpolator(0.1, 20);
                myAnim.setInterpolator(interpolator);
                button.startAnimation(myAnim);
//                loginProgress.setVisibility(View.VISIBLE);
//                btnLogin.setVisibility(View.INVISIBLE);
                String mail = userMail.getText().toString().trim();
                String password = userPassword.getText().toString();

                if (mail.isEmpty() || password.isEmpty()){          //Check of the input-filed are empty
                    showMessage("Please Verify all fields");
//                    loginProgress.setVisibility(View.INVISIBLE);
//                    btnLogin.setVisibility(View.VISIBLE);
//                    myAnim = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in);
//                    button.startAnimation(myAnim);
                }
                else{
                    signIn(mail,password);
                }

            }
        });
    }

    //USER LOGIN WITH MAIL ADDRESS AND PASSWORD
    private void signIn(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this,"Logged in Successfully",Toast.LENGTH_LONG).show();
                    updateUI();
                }
                else{
                    showMessage(Objects.requireNonNull(task.getException()).getMessage());
                    Toast.makeText(LoginActivity.this,"Log in Failed"+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateUI() {
        startActivity(new Intent(LoginActivity.this,NavigationActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            updateUI();
        }
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }

    public void didTapButton(View view) {

        timer = new Timer();
        Button button = (Button)findViewById(R.id.registerBtn);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.1, 20);
        myAnim.setInterpolator(interpolator);
        button.startAnimation(myAnim);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        },1500);

    }
}
