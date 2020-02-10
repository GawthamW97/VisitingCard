package com.ilm.visitingcard_v11.Fragements;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ilm.visitingcard_v11.ItemsModel;
import com.ilm.visitingcard_v11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private ItemsModel itemsModel;
    private EditText userName,userPosition,userMail,userAddress,userPhone,userCompany,userSite,workPhone;
    private ImageView cardFront,cardBack;
    private Button updatebtn;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    int i;

    ArrayList<Uri> ImageList = new ArrayList<Uri>();

    Uri imageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.activity_profile, container, false);
        userName = mView.findViewById(R.id.user_profile_name);
        userMail = mView.findViewById(R.id.user_profile_mail);
        userPhone = mView.findViewById(R.id.user_profile_phone);
        userPosition = mView.findViewById(R.id.user_profile_position);
        userAddress = mView.findViewById(R.id.user_profile_location);
        cardFront = mView.findViewById(R.id.visiting_front);
        cardBack = mView.findViewById(R.id.visiting_back);
        updatebtn = mView.findViewById(R.id.user_update);
        userCompany = mView.findViewById(R.id.user_profile_company);
        userSite = mView.findViewById(R.id.user_profile_web);
        workPhone = mView.findViewById(R.id.user_work_phone);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        db.collection("user").document(mAuth.getCurrentUser().getUid().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    ItemsModel itemsModel = doc.toObject(ItemsModel.class);
                    Picasso.get().load(itemsModel.getFront()).into(cardFront);
                    Picasso.get().load(itemsModel.getBack()).into(cardBack);
                    userName.setText(Objects.requireNonNull(itemsModel).getfName());
                    userMail.setText(Objects.requireNonNull(itemsModel).geteMail());
                    userPosition.setText(Objects.requireNonNull(itemsModel).getPosition());
                    userCompany.setText(itemsModel.getCompany());
                    Log.e("Success", "success");
                }
            }
        });

        cardFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryFront();
            }
        });

        cardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryBack();
            }
        });

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = userName.getText().toString();
                String workAddress = userAddress.getText().toString();
                String phoneNumber = userPhone.getText().toString();
                String companyName = userCompany.getText().toString();
                String position = userPosition.getText().toString();
                String companySite = userSite.getText().toString();
                String workNumber = workPhone.getText().toString();


                Map<Object,String> user = new HashMap<>();
                user.put("fName",firstName);
                user.put("company",companyName);
                user.put("pNo",phoneNumber);
                user.put("wNo",workNumber);
                user.put("address",workAddress);
                user.put("position",position);
                user.put("eMail",mAuth.getCurrentUser().getEmail());
                user.put("website",companySite);
                uploadImage(user);

                db.collection("user").document(mAuth.getUid())
                        .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProfileFragment.this.getActivity(),"Successfully Updated",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileFragment.this.getActivity(),"Failed to Update",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == 1 && data != null){
            imageUri = data.getData();
            ImageList.add(imageUri);
        }
    }

    private void uploadImage(final Map<Object, String> user){
        StorageReference Ref = FirebaseStorage.getInstance().getReference();

        for(i = 0; i<ImageList.size();i++){
            Uri image = ImageList.get(i);
            final StorageReference imageName = Ref.child("Image"+ image.getLastPathSegment());
            imageName.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = String.valueOf(uri);
                            StoreLink(url,user);
                        }
                    });
                }
            });
        }
    }

    private void StoreLink(String url,Map<Object,String> user) {
        user.put("profilePic",url);

        db.collection("user").document(mAuth.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileFragment.this.getActivity(),"uploaded",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileFragment.this.getActivity(),"Failed to upload",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openGalleryFront() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,1);
    }

    private void openGalleryBack() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,1);
    }
}
