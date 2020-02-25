package com.ilm.visitingcard_v11.Fragments;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
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
import com.ilm.visitingcard_v11.NavigationActivity;
import com.ilm.visitingcard_v11.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.yalantis.ucrop.UCrop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements DialogFragment.DialogList {

    private EditText userName,userPosition,userMail,userAddress,userPhone,userCompany,userSite,workPhone;
    private ImageView cardFront,cardBack,profilePic;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    int i;
    private final int CODE_IMAGE_GALLERY = 1;
    private final int FRONT_IMG_GALLERY = 2;
    private final int BACK_IMG_GALLERY = 3;
    Map<Object,Object> user;

    private ArrayList<Uri> ImageList = new ArrayList<>();
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        mView = inflater.inflate(R.layout.activity_profile, container, false);
        profilePic = mView.findViewById(R.id.user_image);
        userName = mView.findViewById(R.id.user_profile_name);
        userMail = mView.findViewById(R.id.user_profile_mail);
        userPhone = mView.findViewById(R.id.user_profile_phone);
        userPosition = mView.findViewById(R.id.user_profile_position);
        userAddress = mView.findViewById(R.id.user_profile_location);
        cardFront = mView.findViewById(R.id.visiting_front);
        cardBack = mView.findViewById(R.id.visiting_back);
        Button updatebtn = mView.findViewById(R.id.user_update);
        ImageView backBtn = mView.findViewById(R.id.back_btn);
        userCompany = mView.findViewById(R.id.user_profile_company);
        userSite = mView.findViewById(R.id.user_profile_web);
        workPhone = mView.findViewById(R.id.user_work_phone);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                startActivity(new Intent(getContext(), NavigationActivity.class));
            }
        });

        // GET THE DETAILS OF THE CURRENT USER FROM THE DATABASES
        db.collection("user").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    ItemsModel itemsModel = doc.toObject(ItemsModel.class);
                    Picasso.get().load(itemsModel.getProfilePic()).into(profilePic);
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

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CropImage.activity().getIntent(getContext());
                Log.e("TAG","1");
                startActivityForResult(intent,CODE_IMAGE_GALLERY);
            }
        });
        cardFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CropImage.activity().getIntent(getContext());
                Log.e("TAG","1");
                startActivityForResult(intent,1);
            }
        });
        cardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG","BACK");
                startActivityForResult(new Intent()
               .setAction(Intent.ACTION_GET_CONTENT)
               .setType("image/*"),2);
                Log.e("TAG","IMG");
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

                user = new HashMap<>();
                user.put("fName",firstName);
                user.put("company",companyName);
                user.put("pNo",phoneNumber);
                user.put("wNo",workNumber);
                user.put("address",workAddress);
                user.put("position",position);
                user.put("eMail",mAuth.getCurrentUser().getEmail());
                user.put("website",companySite);
                uploadImage(user);

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                db.collection("user").document(mAuth.getUid())
                                        .set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(ProfileFragment.this.getActivity(),"Successfully Updated",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getContext(),NavigationActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfileFragment.this.getActivity(),"Failed to Update",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure that you want to update your profile?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        return mView;
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
                            StoreLink(url,user,i);
                        }
                    });
                }
            });
        }
    }

    //UPLOAD IMAGE TO DATABASE
    private void StoreLink(String url,Map<Object,Object> user,int i) {
        if(i == 1){
            user.put("front",url);
        }else if( i == 2){
            user.put("back",url);
        }else if( i == 0){
            user.put("profilePic", url);
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG","IMG");
//        if(requestCode == 1 && resultCode == RESULT_OK){
//            Uri pic = data.getData();
//            cardFront.setImageURI(pic);
//        }
        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri pic = data.getData();
            if(pic != null){
                startCrop(pic);
            }
        }else if(requestCode == 2 && resultCode == RESULT_OK){
            Uri pic = data.getData();
            if(pic != null){
                startCrop(pic);
            }
        }else if(requestCode == UCrop.REQUEST_CROP && resultCode ==RESULT_OK){
            Uri imageUriCropped =UCrop.getOutput(data);

            if(imageUriCropped != null){
                cardBack.setImageURI(imageUriCropped);
            }
        }
//        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if(resultCode == RESULT_OK){
//                Uri resultUri = result.getUri();
//                Log.e("resultUri ->", String.valueOf(resultUri));
//                cardFront.setImageURI(resultUri);
//                ImageList.add(resultUri);
//            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
//                Exception error = result.getError();
//                Log.e("Error ->",String.valueOf(error));
//            }
//        }
    }

    private void startCrop(@NonNull Uri uri){
        Uri dFileName = Uri.parse("Front.jpg");
        UCrop.of(uri,dFileName)
                .withAspectRatio(1,1)
                .start(getContext(),this,UCrop.REQUEST_CROP);
    }

    private UCrop.Options getCropOptions() {
        UCrop.Options options = new UCrop.Options();

        options.setCompressionQuality(70);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        options.setStatusBarColor(getResources().getColor(R.color.bootstrap_brand_primary));
        options.setToolbarColor(getResources().getColor(R.color.bootstrap_brand_danger));

        return options;
    }

    @Override
    public void applyText(String password) {

    }
}
