package com.ilm.visitingcard_v11.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ilm.visitingcard_v11.ChangeMailActivity;
import com.ilm.visitingcard_v11.ChangePassActivity;
import com.ilm.visitingcard_v11.DeleteAccountActivity;
import com.ilm.visitingcard_v11.NavigationActivity;
import com.ilm.visitingcard_v11.R;

public class SettingFragment extends Fragment {

    Button mail, pWord, deleteAcc;
    ImageView backBtn;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.setting_menu, container, false);
        mail = mView.findViewById(R.id.set_mail);
        pWord = mView.findViewById(R.id.set_password);
        deleteAcc = mView.findViewById(R.id.delete_account);
        backBtn = mView.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                startActivity(new Intent(getContext(), NavigationActivity.class));
            }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           //To change the mail address
                startActivity(new Intent(SettingFragment.this.getActivity(), ChangeMailActivity.class));
            }
        });

        pWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {           //To change the current password
                startActivity(new Intent(SettingFragment.this.getActivity(), ChangePassActivity.class));
            }
        });

        deleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //To delete current user
                startActivity(new Intent(SettingFragment.this.getActivity(), DeleteAccountActivity.class));
            }
        });

        return mView;
    }
}
