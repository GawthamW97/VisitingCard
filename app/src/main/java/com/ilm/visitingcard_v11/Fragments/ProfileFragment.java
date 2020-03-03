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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment{

    private EditText fName,lName,userPosition,userMail,userAddress,userPhone,userCompany,userSite,workPhone;
    private ImageView cardFront,cardBack,profilePic;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    int i;
    private final int CODE_IMAGE_GALLERY = 1;
    private final int FRONT_IMG_GALLERY = 2;
    private final int BACK_IMG_GALLERY = 3;
    Map<Object,Object> user;

    private static ArrayList<Uri> ImageList = new ArrayList<>();
    private View mView;
    static ArrayList<String> urlList = new ArrayList<>();
    StorageReference Ref = FirebaseStorage.getInstance().getReference();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        mView = inflater.inflate(R.layout.activity_profile, container, false);
        profilePic = mView.findViewById(R.id.user_image);
        fName = mView.findViewById(R.id.user_fName);
        lName = mView.findViewById(R.id.user_lName);
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
                    if(itemsModel.getProfilePic() !=null) {
                        Picasso.get().load(Objects.requireNonNull(itemsModel).getProfilePic()).into(profilePic);
                    }
                    if(itemsModel.getFront() != null) {
                        Picasso.get().load(itemsModel.getFront()).into(cardFront);
                    }
                    if(itemsModel.getBack() != null) {
                        Picasso.get().load(itemsModel.getBack()).into(cardBack);
                    }
                    fName.setText(Objects.requireNonNull(itemsModel).getfName());
                    lName.setText(Objects.requireNonNull(itemsModel).getlName());
                    userMail.setText(Objects.requireNonNull(itemsModel).geteMail());
                    userPosition.setText(Objects.requireNonNull(itemsModel).getPosition());
                    userCompany.setText(itemsModel.getCompany());
                    userPhone.setText(String.valueOf(itemsModel.getpNo()));
                    workPhone.setText(String.valueOf(itemsModel.getwNo()));
                    userAddress.setText(itemsModel.getAddress());
                    userSite.setText(itemsModel.getWebsite());
                    Log.e("Success", "Success");
                }
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ImageList.isEmpty()) {
                    Intent intent = CropImage.activity()
                            .getIntent(getContext());
                    startActivityForResult(intent,CODE_IMAGE_GALLERY );
                    Log.e("TAG1",ImageList.toString());
                }else{
                    Log.e("TAG1",ImageList.toString());
                    Intent intent = CropImage.activity()
                            .getIntent(getContext()).putExtra("pic",ImageList.get(1));
                    startActivityForResult(intent, CODE_IMAGE_GALLERY);
                }
            }
        });
        cardFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ImageList.isEmpty()) {
                    Intent intent = CropImage.activity()
                            .getIntent(getContext());
                    startActivityForResult(intent,FRONT_IMG_GALLERY );
                    Log.e("TAG1",ImageList.toString());
                }else{
                    Log.e("TAG1",ImageList.toString());
                    Intent intent = CropImage.activity()
                            .getIntent(getContext()).putExtra("pic",ImageList.get(1));
                    startActivityForResult(intent, FRONT_IMG_GALLERY);
                }
            }
        });
        cardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ImageList.isEmpty()) {
                    Intent intent = CropImage.activity()
                            .getIntent(getContext());
                    startActivityForResult(intent,BACK_IMG_GALLERY );
                    Log.e("TAG1",ImageList.toString());
                }else{
                    Log.e("TAG1",ImageList.toString());
                    Intent intent = CropImage.activity()
                            .getIntent(getContext()).putExtra("pic",ImageList.get(1));
                    startActivityForResult(intent, BACK_IMG_GALLERY);
                }
            }
        });

        // UPDATE THE CURRENT USER DETAIL ON THE CLICK OF THE BUTTON
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = fName.getText().toString();
                String lastName = lName.getText().toString();
                String workAddress = userAddress.getText().toString();
                int phoneNumber = Integer.parseInt(String.valueOf(userPhone.getText()).trim());
                String companyName = userCompany.getText().toString();
                String position = userPosition.getText().toString();
                String companySite = userSite.getText().toString();
                int workNumber = Integer.parseInt(workPhone.getText().toString().trim());

                user = new HashMap<>();
                user.put("fName",firstName);
                user.put("lName",lastName);
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
                                db.collection("user").document(Objects.requireNonNull(mAuth.getUid()))
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
                AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
                builder.setMessage("Are you sure that you want to update your profile?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        return mView;
    }

    //GET IMAGE URL
    private void uploadImage(final Map<Object, Object> user){
        StorageReference imageName = null;
        for(i = 0; i<ImageList.size();i++){
            final Uri image = ImageList.get(i);
            if(i == 0){
                imageName = Ref.child(mAuth.getCurrentUser().getUid()+"/ProfilePic");
            }
            if(i == 1) {
                imageName = Ref.child(mAuth.getCurrentUser().getUid()+"/FrontView");
            }else if(i == 2){
                imageName = Ref.child(mAuth.getCurrentUser().getUid()+"/BackView");
            }
            final StorageReference finalImageName = imageName;
            imageName.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    finalImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("TAG",String.valueOf(i));
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
        urlList.add(url);
        if(urlList.size() == 2) {
            user.put("front",urlList.get(0));
            user.put("back",urlList.get(1));
            db.collection("user").document(mAuth.getCurrentUser().getUid())
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed to upload", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == CODE_IMAGE_GALLERY && ImageList.isEmpty()){
                Uri pic = data.getData();
                ImageList.add(0,pic);
                Picasso.get().load(pic).into(profilePic);
            }else{
                Uri pic = data.getData();
                ImageList.add(0,pic);
                if(ImageList.get(1)== null && ImageList.get(2) != null){
                    Picasso.get().load(pic).into(profilePic);
                    Picasso.get().load(ImageList.get(2)).into(cardBack);
                }else if(ImageList.get(1)!= null && ImageList.get(2) == null){
                    Picasso.get().load(pic).into(profilePic);
                    Picasso.get().load(ImageList.get(1)).into(cardBack);
                }else {
                    Picasso.get().load(pic).into(profilePic);
                    Picasso.get().load(ImageList.get(1)).into(cardBack);
                    Picasso.get().load(ImageList.get(2)).into(cardBack);
                }
            }
        }
        Intent intent = getActivity().getIntent();
        if(resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (requestCode == FRONT_IMG_GALLERY && intent.getSerializableExtra("pic") == null && ImageList.isEmpty()) {
                Uri frontCard = result.getUri();
                ImageList.add(1, frontCard);
                Picasso.get().load(ImageList.get(1)).into(cardFront);
            } else if (requestCode == FRONT_IMG_GALLERY && ImageList.get(0) == null && ImageList.get(2) != null) {
                Log.e("TAG1111", "PASS");
                Uri frontCard = result.getUri();
                ImageList.add(1, frontCard);
                Picasso.get().load(ImageList.get(1)).into(cardFront);
                Picasso.get().load(ImageList.get(2)).into(cardBack);
            }else if(requestCode == FRONT_IMG_GALLERY && ImageList.get(0) != null && ImageList.get(2) == null){
                Uri frontCard = result.getUri();
                ImageList.add(1, frontCard);
                Picasso.get().load(ImageList.get(1)).into(cardFront);
                Picasso.get().load(ImageList.get(0)).into(profilePic);
            }else{
                Uri frontCard = result.getUri();
                ImageList.add(1, frontCard);
                Picasso.get().load(ImageList.get(0)).into(profilePic);
                Picasso.get().load(ImageList.get(1)).into(cardFront);
                Picasso.get().load(ImageList.get(2)).into(profilePic);
            }

            if (requestCode == BACK_IMG_GALLERY && intent.getSerializableExtra("pic") == null && ImageList.isEmpty()) {
                Uri backCard = result.getUri();
                ImageList.add(2, backCard);
                Picasso.get().load(ImageList.get(1)).into(cardBack);
            } else if (requestCode == BACK_IMG_GALLERY && ImageList.get(0) == null && ImageList.get(1) != null) {
                Log.e("TAG1122", "PASS");
                Uri backCard = result.getUri();
                ImageList.add(2, backCard);
                Log.e("TAG", ImageList.toString());
                Picasso.get().load(ImageList.get(1)).into(cardFront);
                Picasso.get().load(ImageList.get(2)).into(cardBack);
            }else if(requestCode == BACK_IMG_GALLERY && ImageList.get(0) != null && ImageList.get(1) == null){
                Uri frontCard = result.getUri();
                ImageList.add(1, frontCard);
                Picasso.get().load(ImageList.get(1)).into(cardFront);
                Picasso.get().load(ImageList.get(0)).into(profilePic);
            }else{
                Uri frontCard = result.getUri();
                ImageList.add(1, frontCard);
                Picasso.get().load(ImageList.get(0)).into(profilePic);
                Picasso.get().load(ImageList.get(1)).into(cardFront);
                Picasso.get().load(ImageList.get(2)).into(profilePic);
            }
        }
    }
}
