package com.ilm.visitingcard_v11;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<ItemsModel>{

    Context context;
    ArrayList<ItemsModel> object;

    public CustomAdapter(MainActivity context, List<ItemsModel> object){
        super(context,0, object);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.row_item,parent,false);
        }

        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        TextView userMail = (TextView) convertView.findViewById(R.id.user_mail);
        ImageView userImage = (ImageView) convertView.findViewById(R.id.user_image);

        ItemsModel mission= getItem(position);

        userName.setText(mission.getfName()+ mission.getlName());
        userMail.setText(mission.geteMail());
        Picasso.get().load(mission.getProfilePic()).into(userImage);
        return convertView;
    }

}
