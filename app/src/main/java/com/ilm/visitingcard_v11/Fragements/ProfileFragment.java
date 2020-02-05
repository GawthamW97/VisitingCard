package com.ilm.visitingcard_v11.Fragements;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ilm.visitingcard_v11.ItemsModel;
import com.ilm.visitingcard_v11.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ItemsModel itemsModel;
    private TextView userName,userPosition,userMail,userAddress,userPhone;
    private ImageView profilePic,cardFront,cardBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View mView = inflater.inflate(R.layout.activity_profile, container, false);
        profilePic = mView.findViewById(R.id.user_profile_image);
        userName = mView.findViewById(R.id.user_profile_name);
        userMail = mView.findViewById(R.id.user_profile_mail);
        userPhone = mView.findViewById(R.id.user_profile_phone);
        userPosition = mView.findViewById(R.id.user_profile_position);
        userAddress = mView.findViewById(R.id.user_profile_location);
        cardFront = mView.findViewById(R.id.visiting_front);
        cardBack = mView.findViewById(R.id.visiting_back);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAth = FirebaseAuth.getInstance();

        db.collection("user").document(mAth.getCurrentUser().getUid().toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    ItemsModel itemsModel = doc.toObject(ItemsModel.class);

                    Log.e("URL", itemsModel.getProfilePic().toString());
                    Picasso.get().load(itemsModel.getProfilePic()).into(profilePic);
                    Picasso.get().load(itemsModel.getFront()).into(cardFront);
                    Picasso.get().load(itemsModel.getBack()).into(cardBack);
                    userName.setText(Objects.requireNonNull(itemsModel).getfName() + " " + Objects.requireNonNull(itemsModel).getlName());
                    userMail.setText(Objects.requireNonNull(itemsModel).geteMail());
                    userPosition.setText(Objects.requireNonNull(itemsModel).getAddress());
                    Log.e("Success", "success");
                }
            }
        });

        return mView;
    }
}
