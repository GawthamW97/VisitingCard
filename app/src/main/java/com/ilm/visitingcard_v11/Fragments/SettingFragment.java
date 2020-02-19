package com.ilm.visitingcard_v11.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ilm.visitingcard_v11.ChangeMailActivity;
import com.ilm.visitingcard_v11.R;

public class SettingFragment extends Fragment {

    Button mail,pWord;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.setting_menu, container, false);
        mail = mView.findViewById(R.id.set_mail);
        pWord = mView.findViewById(R.id.set_password);

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingFragment.this.getActivity(), ChangeMailActivity.class));
            }
        });

        return mView;
    }
}
