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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.ilm.visitingcard_v11.Fragments.SettingFragment;

public class DeleteAccountActivity extends AppCompatActivity {

    EditText mail,pwd;
    Button confirm, cancel;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        mail = findViewById(R.id.curr_user_mail);
        pwd = findViewById(R.id.curr_user_password);
        confirm = findViewById(R.id.confirm_btn);
        cancel = findViewById(R.id.cancel_btn);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser().getEmail().equals(mail.getText().toString().trim())){
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(mail.getText().toString().trim(),pwd.getText().toString());

                    mAuth.getCurrentUser().reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mAuth.getCurrentUser().delete();
                            Toast.makeText(DeleteAccountActivity.this,"Account Deleted",Toast.LENGTH_SHORT);
                            finish();
                            startActivity(new Intent(DeleteAccountActivity.this,LoginActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mail.setText(null);
                            pwd.setText(null);
                            Toast.makeText(DeleteAccountActivity.this,"Email or Password is incorrect",Toast.LENGTH_LONG).show();
                            Log.e("TAG","Failed to Delete");
                        }
                    });
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_fragment,new SettingFragment());
                fragmentTransaction.commit();
            }
        });
    }
}
