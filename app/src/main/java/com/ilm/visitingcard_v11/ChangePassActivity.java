package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ChangePassActivity extends AppCompatActivity{

    EditText newPwd,rePwd,currPwd;
    Button confirmbtn, clearbtn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        showData();

        // On pull down refresh activation
        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(getIntent());
                newPwd.setText(null);
                currPwd.setText(null);
                rePwd.setText(null);
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void showData() {                       // Show data on the activity view
        currPwd = findViewById(R.id.current_pwd);
        newPwd = findViewById(R.id.new_password);
        rePwd = findViewById(R.id.re_new_password);
        confirmbtn = findViewById(R.id.confirm_btn);
        clearbtn = findViewById(R.id.clear);

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currPwd.getText()!= null && newPwd.getText() != null && rePwd.getText() != null){
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(Objects.requireNonNull
                                    (Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()),currPwd.getText().toString()); // Get auth credentials from the user

                    mAuth.getCurrentUser().reauthenticate(credential)               //Re-Authenticate user with the received credentials
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                String newPass = newPwd.getText().toString();
                                if(newPass.equals(rePwd.getText().toString())) {    //Validate inserted password
                                    mAuth.getCurrentUser().updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {      //Update new password to firebase authentication
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ChangePassActivity.this,"Password Changed",Toast.LENGTH_SHORT).show();
                                                Log.e("TAG", "Password updated");
                                                mAuth.signOut();
                                                finish();
                                                startActivity(new Intent(ChangePassActivity.this,LoginActivity.class));
                                            } else {
                                                Toast.makeText(ChangePassActivity.this,"Failed to Change Password",Toast.LENGTH_LONG).show();
                                                Log.e("TAG", "Error password not updated"+task.getResult());
                                            }
                                        }
                                    });
                                }else{
                                    Log.e("TAG","Password dose not match with other field");
                                    Toast.makeText(ChangePassActivity.this,"Password dose not match with other field",Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Log.e("TAG", "Error auth failed");
                                Toast.makeText(ChangePassActivity.this,"Error Authentication failed",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        clearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPwd.setText(null);
                currPwd.setText(null);
                rePwd.setText(null);
            }
        });
    }
}
