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

public class ChangePassActivity extends AppCompatActivity{

    EditText newPwd,rePwd,currPwd;
    Button confirmbtn,backbtn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        showData();

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                finish();
                startActivity(getIntent());
                showData();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void showData() {
        currPwd = findViewById(R.id.current_pwd);
        newPwd = findViewById(R.id.new_password);
        rePwd = findViewById(R.id.re_new_password);
        confirmbtn = findViewById(R.id.confirm_btn);
        backbtn = findViewById(R.id.back_btn);

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currPwd.getText()!= null && newPwd.getText() != null && rePwd.getText() != null){
                    // Get auth credentials from the user for re-authentication
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(mAuth.getCurrentUser().getEmail(),currPwd.getText().toString());
                    Log.e("TAG", "Getting Re-Authenticated");
                    mAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                String newPass = newPwd.getText().toString();
                                Log.e("TAG", "Field Validation ");
                                if(newPass.equals(rePwd.getText().toString())) {
                                    mAuth.getCurrentUser().updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.e("TAG", "Password updated");
                                            } else {
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
                                Toast.makeText(ChangePassActivity.this,"Error auth failed",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePassActivity.this,NavigationActivity.class));
            }
        });
    }
}
