package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ilm.visitingcard_v11.Fragments.DialogFragment;
import com.ilm.visitingcard_v11.Fragments.SettingFragment;

public class ChangeMailActivity extends AppCompatActivity implements DialogFragment.DialogList {

    TextView confirm, cancel;
    EditText editMail;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mail);
        confirm = findViewById(R.id.confirm);
        cancel = findViewById(R.id.cancel);
        editMail = findViewById(R.id.edit_mail);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangeMailActivity.this, NavigationActivity.class));
            }
        });
    }

    private void openDialog() {
        DialogFragment dialog = new DialogFragment();
        dialog.show(getSupportFragmentManager(),"Dialog");
    }

    @Override
    public void applyText(final String password) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(),password);

        mAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.e("User", "Re-Authenticated");
                    mAuth.fetchSignInMethodsForEmail(editMail.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if(task.isSuccessful()){
                                try{
                                    if(task.getResult().getSignInMethods().size() == 1) {
                                        Log.e("TAG", "onComplete: Email address already exist");
                                        Toast.makeText(ChangeMailActivity.this,"Email exist",Toast.LENGTH_SHORT).show();
                                    }else{
                                        Log.e("TAG","Email can be added");

                                        mAuth.getCurrentUser().updateEmail(editMail.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Log.e("TAG","user email address updated");
                                                            Toast.makeText(ChangeMailActivity.this,"Email Updated",Toast.LENGTH_LONG).show();
                                                            mAuth.signOut();
                                                            finish();
                                                            startActivity(new Intent(ChangeMailActivity.this,LoginActivity.class));
                                                        }
                                                    }
                                                });
                                    }
                                }catch (NullPointerException e){
                                    Log.e("TAG","NPE"+e.getMessage());
                                }
                            }else{
                                Log.e("TAG","Failed");
                            }
                        }
                    });
                }
            }
        });

    }
}
