package com.ilm.visitingcard_v11;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemsPreviewActivity extends AppCompatActivity {

    ImageView profilePic;
    TextView userName,userPosition,userMail,userAddress,userPhone,userCompany,userWno,userCompWeb;
    Button delete_btn;
    ItemsModel itemsModel;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID;
    ItemsModel model;

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator currentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int shortAnimationDuration;
    private boolean value = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_preview);

        // Hook up clicks on the thumbnail views.
        final View cardFront = findViewById(R.id.front);
//        cardFront.getBackground().setAlpha(200);
        final View cardBack = findViewById(R.id.back);
//        cardBack.getBackground().setAlpha(200);
        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        profilePic = findViewById(R.id.user_image);
        userName = findViewById(R.id.conn_name);
        userMail = findViewById(R.id.conn_mail);
        userPhone = findViewById(R.id.conn_phone);
        userPosition = findViewById(R.id.conn_position);
        userAddress = findViewById(R.id.conn_location);
        userCompany = findViewById(R.id.conn_company);
        userWno = findViewById(R.id.conn_work_phone);
        userCompWeb = findViewById(R.id.conn_web);

        //TODO: View user profile from the list
        final Intent intent = getIntent();
        if(intent.getSerializableExtra("items") != null){
            itemsModel = (ItemsModel) intent.getSerializableExtra("items");
            Picasso.get().load(itemsModel.getProfilePic()).into(profilePic);
            userName.setText(Objects.requireNonNull(itemsModel).getfName()+" "+Objects.requireNonNull(itemsModel).getlName());
            userMail.setText(Objects.requireNonNull(itemsModel).geteMail());
            userPosition.setText(Objects.requireNonNull(itemsModel).getPosition());
            userCompany.setText(Objects.requireNonNull(itemsModel).getCompany());
            userPhone.setText(String.valueOf(Objects.requireNonNull(itemsModel).getpNo()));
            if(itemsModel.getWebsite() == null){
                LinearLayout linearLayout = findViewById(R.id.website_layout);
                linearLayout.setVisibility(View.GONE);
            }else{
                userCompWeb.setText(itemsModel.getWebsite());
            }
            if(itemsModel.getwNo() == 0){
                LinearLayout linearLayout = findViewById(R.id.work_phone_layout);
                linearLayout.setVisibility(View.GONE);
            }else{
                userWno.setText(String.valueOf(Objects.requireNonNull(itemsModel).getwNo()));
            }
            delete_btn = findViewById(R.id.conn_delete);
            if(itemsModel.getAddress() == null){
                LinearLayout linearLayout = findViewById(R.id.address_layout);
                linearLayout.setVisibility(View.GONE);
            }else{
                userAddress.setText(itemsModel.getAddress());
            }

            cardFront.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    zoomImageFromThumb(cardFront, itemsModel.getFront());
                }
            });

            cardBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoomImageFromThumb(cardBack, itemsModel.getBack());
                }
            });

            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    db.collection("user").document(mAuth.getCurrentUser().getUid()).update("conn", FieldValue.arrayRemove(itemsModel.getUID()));
                                    startActivity(new Intent(ItemsPreviewActivity.this,NavigationActivity.class));
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    startActivity(new Intent(ItemsPreviewActivity.this,NavigationActivity.class));
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(ItemsPreviewActivity.this);
                    builder.setMessage("Are you sure that you want to Delete this contact?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });

        }
        //TODO: Get user profile after scanning
        Intent qrIntent = getIntent();
        if(qrIntent.getSerializableExtra("id")!= null){
            userID = (String) qrIntent.getSerializableExtra("id");
            Log.e("ID",userID);

            db.collection("user").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<String> idList = new ArrayList<>();
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG", document.getId() + " => " + document.getData());
                                    idList.add(task.getResult().toString());
                                }
                                if(idList.contains(userID)){
                                    Log.e("TAGE","Scanned Valid ID");
                                    value = true;
                                }else{
                                    Toast.makeText(ItemsPreviewActivity.this,"Invalid Code Scanned",Toast.LENGTH_LONG).show();
                                    Log.e("TAGE","Scanned Invalid ID");
                                    value = false;
                                    finish();
                                    startActivity(new Intent(ItemsPreviewActivity.this,NavigationActivity.class));
                                }
                            } else {
                                Log.d("TAG", "Error getting documents: ", task.getException());
                            }
                        }
                    });
            if(value){
                db.collection("user").document(userID)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            model = doc.toObject(ItemsModel.class);
                            Picasso.get().load(model.getProfilePic()).into(profilePic);
                            userName.setText(Objects.requireNonNull(model).getfName());
                            userMail.setText(Objects.requireNonNull(model).geteMail());
                            userPosition.setText(Objects.requireNonNull(model).getPosition());
                            userCompany.setText(model.getCompany());
                            userPhone.setText(String.valueOf(Objects.requireNonNull(model).getpNo()));
                            userWno.setText(String.valueOf(Objects.requireNonNull(model).getwNo()));
                            userAddress.setText(model.getAddress());

                            cardFront.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    zoomImageFromThumb(cardFront, model.getFront());
                                }
                            });

                            cardBack.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    zoomImageFromThumb(cardBack, model.getBack());
                                }
                            });

                            //ASK USER FOR A CONFIRMATION TO ADD THE NEW CARD
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            db.collection("user").document(mAuth.getCurrentUser().getUid()).update("conn", FieldValue.arrayUnion(userID));
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            startActivity(new Intent(ItemsPreviewActivity.this, NavigationActivity.class));
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(ItemsPreviewActivity.this);
                            builder.setMessage("Do you want to add " + userName.getText().toString() + "?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                            Log.e("TAG", "Success");
                        } else {
                            Log.e("TAG", "Fail");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startActivity(new Intent(ItemsPreviewActivity.this, NavigationActivity.class));
                    }
                });
            }

        }

        //TODO:On Click Activity for Phone Numbers and Email Address

        userMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{itemsModel.geteMail()});
                startActivity(emailIntent);
            }
        });

        userPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+itemsModel.getpNo()));
                startActivity(intent);
            }
        });

        userWno.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent workPhone = new Intent(Intent.ACTION_DIAL);
                workPhone.setData(Uri.parse("tel:"+itemsModel.getwNo()));
                startActivity(workPhone);
            }
        });
    }

    //TODO:ENLARGE THE SELECTED CARD FOR THE USER TO VIEW

    private void zoomImageFromThumb(final View thumbView, String imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        Picasso.get().load(imageResId).into(expandedImageView);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.profile_container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        startActivity(new Intent(ItemsPreviewActivity.this, NavigationActivity.class));
    }
}
