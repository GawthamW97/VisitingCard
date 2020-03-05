package com.ilm.visitingcard_v11.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.ilm.visitingcard_v11.NavigationActivity;
import com.ilm.visitingcard_v11.R;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class AddFragment extends Fragment {

    private ImageView gen_img;
    private String text2Qr;
    private FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.add_people, container, false);
        Button bt_scan = mView.findViewById(R.id.btn_scan);
        ImageView backBtn = mView.findViewById(R.id.back_btn);
        gen_img = mView.findViewById(R.id.qr_image);
        mAuth = FirebaseAuth.getInstance();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                   //On press start HomeActivity
                getActivity().finish();
                startActivity(new Intent(getContext(), NavigationActivity.class));
            }
        });


        bt_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                        //ADD THE SCANNED USER PROFILE TO THE CURRENT USER'S CONNECTION LIST
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan!!");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();                      //Initiate scanning process by opening camera
            }
        });

        text2Qr = mAuth.getUid();                       // Get Current user's ID to generate QR Code
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE,900,900);     //Set the size of the QR Code Generated
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            gen_img.setImageBitmap(bitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }
        return mView;
    }
}
