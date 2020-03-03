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
import com.ilm.visitingcard_v11.Fragments.SettingFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DeleteAccountActivity extends AppCompatActivity {

    EditText mail,pwd;
    Button confirm, cancel;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    Map<Object,String> imageStored = new HashMap<>();
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
        cancel = findViewById(R.id.cancel_btn);
        progressBar = findViewById(R.id.progress);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("user").document(mAuth.getCurrentUser().getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            try {
                                DocumentSnapshot doc = task.getResult();
                                ItemsModel list = Objects.requireNonNull(doc).toObject(ItemsModel.class);
                                Log.e("TAG", Objects.requireNonNull(list).getFront());
                                if (list.getFront() != null) {
                                    imageStored.put(1,list.getFront());
                                }
                                if (list.getBack() != null) {
                                    imageStored.put(2,list.getBack());
                                }
                                if (list.getProfilePic() != null) {
                                    imageStored.put(0,list.getProfilePic());
                                }
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
                confirm.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                if(mAuth.getCurrentUser().getEmail().equals(mail.getText().toString().trim())){
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(mail.getText().toString().trim(),pwd.getText().toString());
                    mAuth.getCurrentUser().reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(DeleteAccountActivity.this,"Your account is Deleted",Toast.LENGTH_LONG).show();
                            StorageReference profile_pic = storageRef.child((mAuth.getCurrentUser().getUid() + "/ProfilePicture"));
                            profile_pic.delete();
                            StorageReference card_front = storageRef.child((mAuth.getCurrentUser().getUid() + "/FrontView"));
                            card_front.delete();
                            StorageReference card_back = storageRef.child((mAuth.getCurrentUser().getUid() + "/BackView"));
                            card_back.delete();
                            db.collection("user").document(mAuth.getCurrentUser().getUid())
                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.e("TAG","Account Deleted");
                                            finish();
                                            startActivity(new Intent(DeleteAccountActivity.this,LoginActivity.class));
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
