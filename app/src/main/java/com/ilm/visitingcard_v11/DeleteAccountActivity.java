package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteAccountActivity extends AppCompatActivity {

    EditText mail,pwd;
    Button confirm;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ProgressBar progressBar;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        mail = findViewById(R.id.curr_user_mail);
        pwd = findViewById(R.id.curr_user_password);
        confirm = findViewById(R.id.confirm_btn);
        progressBar = findViewById(R.id.progress);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                // Delete user from the document list of firebase firestore
                db.collection("user").document(mAuth.getCurrentUser().getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Log.e("TAG","User Deleted from Firestore");
                        }
                    }
                });
                confirm.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                if(mAuth.getCurrentUser().getEmail().equals(mail.getText().toString().trim())){
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(mail.getText().toString().trim(),pwd.getText().toString());  // Set credential with the data inserted in the fields
                    mAuth.getCurrentUser().reauthenticate(credential)                                        //Re-authenticate users with the received credentials
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {                              //If task is successful remove images from firebase storage
                            Toast.makeText(DeleteAccountActivity.this,"Your account is Deleted",Toast.LENGTH_LONG).show();

                            StorageReference profile_pic = storageRef.child((mAuth.getCurrentUser().getUid() + "/pPic"));
                            profile_pic.delete();
                            StorageReference card_front = storageRef.child((mAuth.getCurrentUser().getUid() + "/FrontView"));
                            card_front.delete();
                            StorageReference card_back = storageRef.child((mAuth.getCurrentUser().getUid() + "/BackView"));
                            card_back.delete();

                            db.collection("user").document(mAuth.getCurrentUser().getUid())         //Delete user from firebase Authentication
                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.e("TAG","Account Deleted");
                                            finish();
                                            startActivity(new Intent(DeleteAccountActivity.this,LoginActivity.class));      //On deletion re-direct to Login
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mail.setText(null);
                                    pwd.setText(null);
                                    confirm.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Log.e("TAG","Failed to delete account");
                                    Toast.makeText(DeleteAccountActivity.this,"Failed to Delete",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mail.setText(null);
                            pwd.setText(null);
                            confirm.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(DeleteAccountActivity.this,"Email or Password is incorrect",Toast.LENGTH_LONG).show();
                            Log.e("TAG","Failed to Delete");
                        }
                    });
                }
            }
        });
    }
}
