package com.ilm.visitingcard_v11.Fragments;

import android.content.ClipData;
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
import com.google.firebase.firestore.SetOptions;
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

    private EditText userName,userPosition,userMail,userAddress,userPhone,userCompany,userSite,workPhone;
    private ImageView cardFront,cardBack;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    int i;
    private final int CODE_IMAGE_GALLERY = 1;
    private final int CODE_MULTI_IMG_GALLERY = 2;

    private ArrayList<Uri> ImageList = new ArrayList<Uri>();

    Uri imageUri;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_profile, container, false);
        userName = mView.findViewById(R.id.user_profile_name);
        userMail = mView.findViewById(R.id.user_profile_mail);
        userPhone = mView.findViewById(R.id.user_profile_phone);
        userPosition = mView.findViewById(R.id.user_profile_position);
        userAddress = mView.findViewById(R.id.user_profile_location);
        cardFront = mView.findViewById(R.id.visiting_front);
        cardBack = mView.findViewById(R.id.visiting_back);
        Button updatebtn = mView.findViewById(R.id.user_update);
        userCompany = mView.findViewById(R.id.user_profile_company);
        userSite = mView.findViewById(R.id.user_profile_web);
        workPhone = mView.findViewById(R.id.user_work_phone);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // GET THE DETAILS OF THE CURRENT USER FROM THE DATABASES
        db.collection("user").document(mAuth.getCurrentUser().getUid().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                    userPhone.setText(String.valueOf(itemsModel.getpNo()));
                    workPhone.setText(String.valueOf(itemsModel.getwNo()));
                    userAddress.setText(itemsModel.getAddress());
                    Log.e("Success", "success");
                }
            }
        });

        cardFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intent.createChooser(new Intent().
                        setAction(Intent.ACTION_GET_CONTENT).
                        setType("image/*"),"Select one Image"),
                        CODE_IMAGE_GALLERY);
            }
        });

        cardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                startActivityForResult(Intent.createChooser(intent,"Select multiple images"),
                        CODE_MULTI_IMG_GALLERY);
            }
        });

        // UPDATE THE CURRENT USER DETAIL ON THE CLICK OF THE BUTTON

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = userName.getText().toString();
                String workAddress = userAddress.getText().toString();
                int phoneNumber = Integer.parseInt(String.valueOf(userPhone.getText()).trim());
                String companyName = userCompany.getText().toString();
                String position = userPosition.getText().toString();
                String companySite = userSite.getText().toString();
                int workNumber = Integer.parseInt(workPhone.getText().toString().trim());

                Map<Object,Object> user = new HashMap<>();
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
                        .set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
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

//        back_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), NavigationActivity.class));
//            }
//        });

        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CODE_IMAGE_GALLERY && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            if(imageUri != null){
                cardFront.setImageURI(imageUri);
            }
        }else if(requestCode == CODE_MULTI_IMG_GALLERY && resultCode == RESULT_OK){
            ClipData clipData = data.getClipData();

            if(clipData != null){
                cardFront.setImageURI(clipData.getItemAt(0).getUri());
                cardBack.setImageURI(clipData.getItemAt(1).getUri());

                for(int i = 0; i < clipData.getItemCount();i++){
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri url = item.getUri();
                    ImageList.add(url);
                    Log.e("MULTI", url.toString());
                }
            }
        }
    }

    //GET IMAGE URL
    private void uploadImage(final Map<Object, Object> user){
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

    //UPLOAD IMAGE TO DATABASE
    private void StoreLink(String url,Map<Object,Object> user) {
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
