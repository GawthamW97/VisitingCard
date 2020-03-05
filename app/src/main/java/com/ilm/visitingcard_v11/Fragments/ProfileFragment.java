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

    private static Map<Object,Uri> ImageList = new HashMap<>();
    private View mView;
    static ArrayList<String> urlList = new ArrayList<>();
    ArrayList<Uri> img_collection = new ArrayList<>();

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
                    if(itemsModel.getpPic() !=null) {
                        Picasso.get().load(Objects.requireNonNull(itemsModel).getpPic()).into(profilePic);
                    }
                    if(itemsModel.getFront() != null) {
                        Picasso.get().load(itemsModel.getFront()).into(cardFront);
                    }
                    if(itemsModel.getBack() != null) {
                        Picasso.get().load(itemsModel.getBack()).into(cardBack);
                    }
                    fName.setText(Objects.requireNonNull(itemsModel).getfN());
                    lName.setText(Objects.requireNonNull(itemsModel).getlN());
                    userMail.setText(Objects.requireNonNull(itemsModel).geteM());
                    userPosition.setText(Objects.requireNonNull(itemsModel).getPos());
                    userCompany.setText(itemsModel.getCmp());
                    userPhone.setText(String.valueOf(itemsModel.getpNo()));
                    workPhone.setText(String.valueOf(itemsModel.getwNo()));
                    userAddress.setText(itemsModel.getAdr());
                    userSite.setText(itemsModel.getWeb());
                    Log.e("Success", "Success");
                }
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           //On profile image click open gallery and crop image
                Intent intent = CropImage.activity()
                            .getIntent(getContext());
                    startActivityForResult(intent,CODE_IMAGE_GALLERY );

            }
        });
        cardFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           //On front card image click open gallery and crop image
                Intent intent = CropImage.activity()
                            .getIntent(getContext());
                    startActivityForResult(intent,FRONT_IMG_GALLERY );
            }
        });
        cardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           //On front card image click open gallery and crop image
                Intent intent = CropImage.activity()
                            .getIntent(getContext());
                    startActivityForResult(intent,BACK_IMG_GALLERY );
            }
        });

        // UPDATE THE CURRENT USER DETAIL ON THE CLICK OF THE BUTTON
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get Inserted data from the input-field
                String firstName = fName.getText().toString();
                String lastName = lName.getText().toString();
                String workAddress = userAddress.getText().toString();
                int phoneNumber = Integer.parseInt(String.valueOf(userPhone.getText()).trim());
                String companyName = userCompany.getText().toString();
                String position = userPosition.getText().toString();
                String companySite = userSite.getText().toString();
                int workNumber = Integer.parseInt(workPhone.getText().toString().trim());

                //Add inserted data to the HashMap to upload it to the firebase
                user = new HashMap<>();
                user.put("fN",firstName);
                user.put("lN",lastName);
                user.put("cmp",companyName);
                user.put("pNo",phoneNumber);
                user.put("wNo",workNumber);
                user.put("adr",workAddress);
                user.put("pos",position);
                user.put("eM",mAuth.getCurrentUser().getEmail());
                user.put("web",companySite);
                uploadImage(user);

                //Ask user for confirmation when updating
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:           //If user selects yes as the confirmation option
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
                            case DialogInterface.BUTTON_NEGATIVE:           //If user selects no as the confirmation option
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

        //Load images from the HashMap to the ArrayList
        img_collection.add(0,ImageList.get("pPic"));
        img_collection.add(1,ImageList.get("front"));
        img_collection.add(2,ImageList.get("back"));
        for(i = 0; i<img_collection.size();i++){
            Uri image = null;
                Uri image1 = ImageList.get("pPic");
                Uri image2 = ImageList.get("front");
                Uri image3 = ImageList.get("back");
                if(i == 0 && img_collection.get(0) != null){        // If the profile pic is selected and cropped upload the image
                    image = image1;
                    imageName = Ref.child(mAuth.getCurrentUser().getUid()+"/pPic");
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
                if(i == 1 && img_collection.get(1) != null){        // If the front card image is selected and cropped upload the image
                    image = image2;
                    imageName = Ref.child(mAuth.getCurrentUser().getUid()+"/FrontView");
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
                if(i == 2 && img_collection.get(2) != null){ // If the back card image is selected and cropped upload the image
                    image = image3;
                    imageName = Ref.child(mAuth.getCurrentUser().getUid()+"/BackView");
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
    }

    //UPLOAD IMAGE TO DATABASE
    private void StoreLink(String url,Map<Object,Object> user,int i) {      //upload the images that user selected from gallery to firebase
        urlList.add(url);
        if(urlList.size() == 3) {
            user.put("pPic",urlList.get(0));
            user.put("front",urlList.get(1));
            user.put("back",urlList.get(2));
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

    // When user selects an image from the gallery for the result set the images to image view
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Intent intent = getActivity().getIntent();

        if(resultCode == RESULT_OK && requestCode ==CODE_IMAGE_GALLERY) {           //when the user selects the profile picture
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri pic = result.getUri();
            if (ImageList.isEmpty()) {                                                          // if the profile pic is chosen first
                ImageList.put("pPic", pic);
                Picasso.get().load(pic).into(profilePic);
            } else {
                ImageList.put("pPic", pic);
                if (ImageList.get("front") == null && ImageList.get("back") != null) {          // if back card view is chosen before profile pic
                    Picasso.get().load(ImageList.get("pPic")).into(profilePic);
                    Picasso.get().load(ImageList.get("back")).into(cardBack);
                } else if (ImageList.get("front") != null && ImageList.get("back") == null) {   // if front card view is chosen before profile pic
                    Picasso.get().load(ImageList.get("pPic")).into(profilePic);
                    Picasso.get().load(ImageList.get("front")).into(cardFront);
                } else {                                                                        // if front card view and back back view are chosen before front card view
                    Picasso.get().load(ImageList.get("pPic")).into(profilePic);
                    Picasso.get().load(ImageList.get("front")).into(cardBack);
                    Picasso.get().load(ImageList.get("back")).into(cardBack);
                }
            }
        }
        if(resultCode == RESULT_OK && requestCode == FRONT_IMG_GALLERY) {               //when the user selects the front card view
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri frontCard = result.getUri();
            if (ImageList.isEmpty()) {                            //if front card view is chosen first
                ImageList.put("front", frontCard);
                Picasso.get().load(ImageList.get("front")).into(cardFront);
            } else if (ImageList.get("pPic") == null && ImageList.get("back") != null) {        // if back card view is chosen before front card view
                Log.e("TAG1111", "PASS");
                ImageList.put("front", frontCard);
                Picasso.get().load(ImageList.get("front")).into(cardFront);
                Picasso.get().load(ImageList.get("back")).into(cardBack);
            } else if (ImageList.get("pPic") != null && ImageList.get("back") == null) {        // if profile pic is chosen before front card view
                ImageList.put("front", frontCard);
                Picasso.get().load(ImageList.get("front")).into(cardFront);
                Picasso.get().load(ImageList.get("pPic")).into(profilePic);
            } else {
                ImageList.put("front", frontCard);                                              // if back card view and profile pic are chosen before front card view
                Picasso.get().load(ImageList.get("pPic")).into(profilePic);
                Picasso.get().load(ImageList.get("front")).into(cardFront);
                Picasso.get().load(ImageList.get("back")).into(cardBack);
            }
        }

            if(resultCode == RESULT_OK && requestCode == BACK_IMG_GALLERY){             //when the user selects the back card view
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri backCard = result.getUri();
                if (ImageList.isEmpty()) {                                                      //if back card view is chosen first
                    ImageList.put("back", backCard);
                    Picasso.get().load(ImageList.get("back")).into(cardBack);
                }else if (ImageList.get("pPic") == null && ImageList.get("front") != null) {    // if front card view is chosen before back card view
                    Log.e("TAG1122", "PASS");
                    ImageList.put("back", backCard);
                    Log.e("TAG", ImageList.toString());
                    Picasso.get().load(ImageList.get("front")).into(cardFront);
                    Picasso.get().load(ImageList.get("back")).into(cardBack);
                }else if(ImageList.get("pPic") != null && ImageList.get("front") == null){      // if profile pic is chosen before back card view
                    ImageList.put("back", backCard);
                    Picasso.get().load(ImageList.get("back")).into(cardBack);
                    Picasso.get().load(ImageList.get("pPic")).into(profilePic);
                }else{
                    ImageList.put("back", backCard);                                            //if back card view and profile pic are chosen before front card view
                    Picasso.get().load(ImageList.get("pPic")).into(profilePic);
                    Picasso.get().load(ImageList.get("front")).into(cardFront);
                    Picasso.get().load(ImageList.get("back")).into(cardBack);
                }
            }
        }
    }
