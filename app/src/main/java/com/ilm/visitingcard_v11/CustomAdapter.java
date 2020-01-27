package com.ilm.visitingcard_v11;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomAdapter extends ArrayAdapter<ItemsModel> {

    Context context;
    ArrayList<ItemsModel> object;

    public CustomAdapter(MainActivity context, List<ItemsModel> object){
        super(context,0, object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView =  ((Activity)getContext()).getLayoutInflater().inflate(R.layout.row_item,parent,false);
        }

        TextView titleTextView = (TextView) convertView.findViewById(R.id.mission_title);
        TextView expTextView = (TextView) convertView.findViewById(R.id.mission_exp);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.mission_date);
        TextView descriptionTextView = (TextView) convertView.findViewById(R.id.mission_description);

        ItemsModel mission = getItem(position);

        titleTextView.setText(Objects.requireNonNull(mission).getFirst());
        expTextView.setText(mission.getLast());
        dateTextView.setText(mission.getFirst());
        descriptionTextView.setText(mission.geteMail());

        return convertView;
    }

}
