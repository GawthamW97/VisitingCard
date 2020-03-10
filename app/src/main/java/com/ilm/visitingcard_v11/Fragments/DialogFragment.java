package com.ilm.visitingcard_v11.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ilm.visitingcard_v11.R;

public class DialogFragment extends AppCompatDialogFragment {

    EditText pWord;
    DialogList listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View mView = inflater.inflate(R.layout.confirm_password,null);

        builder.setView(mView)
                .setTitle("Confirm Password")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().dismiss();
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = pWord.getText().toString();

                        if(!password.equals("")){
                            listener.applyText(password);
                        }
                        listener.applyText(password);
            }
        });

        pWord = mView.findViewById(R.id.confirm_password);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            listener = (DialogList) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement Dialog");
        }
    }

    public interface DialogList{
        void applyText(String password);
    }
}
