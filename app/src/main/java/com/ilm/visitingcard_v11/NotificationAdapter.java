package com.ilm.visitingcard_v11;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private ArrayList<Notification> mList;
    private Context context;
    private int i;

    class NotificationHolder extends RecyclerView.ViewHolder {
        TextView mTextView1;
        TextView mTextView2;
        Button close;

        NotificationHolder(@NonNull final View itemView) {
            super(itemView);

            mTextView1 = itemView.findViewById(R.id.message);
            mTextView2 = itemView.findViewById(R.id.date);
            close = itemView.findViewById(R.id.close);
            context = itemView.getContext();

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemView.setVisibility(View.GONE);
//                    context.startActivity(new Intent(context,ItemsPreviewActivity.class).putExtra("items", String.valueOf(mList.get(getAdapterPosition()))));
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {   //Open Selected Item from Connection List
                    Log.e("POSITION",String.valueOf(getAdapterPosition()));
//                    context.startActivity(new Intent(context,ItemsPreviewActivity.class).putExtra("items", String.valueOf(mList.get(getAdapterPosition()))));
                }
            });
        }
    }

    public NotificationAdapter(ArrayList<Notification> itemList) {
        mList = itemList;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_items,
                parent, false);
        return new NotificationAdapter.NotificationHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationHolder holder, int position) {
        Notification currentItem = mList.get(position);
        i = position;
        holder.mTextView1.setText(currentItem.getName());
        holder.mTextView2.setText((CharSequence) currentItem.getDate());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void filterList(ArrayList<Notification> filteredList) {
        mList = filteredList;
        notifyDataSetChanged();
    }

}
