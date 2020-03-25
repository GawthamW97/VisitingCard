package com.ilm.visitingcard_v11;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ListViewHolder> {
        private ArrayList<ItemsModel> mList;
        private Context context;
        private int i;

        class ListViewHolder extends RecyclerView.ViewHolder {
            ImageView mImageView;
            TextView mTextView1;
            TextView mTextView2;

            ListViewHolder(final View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.user_image);
                mTextView1 = itemView.findViewById(R.id.user_name);
                mTextView2 = itemView.findViewById(R.id.user_mail);
                context = itemView.getContext();

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {   //Open Selected Item from Connection List
                        Log.e("POSITION",String.valueOf(getAdapterPosition()));
                        context.startActivity(new Intent(context,ItemsPreviewActivity.class).putExtra("items",mList.get(getAdapterPosition())));
                    }
                });
            }
        }

        public ListItemAdapter(ArrayList<ItemsModel> itemList) {
            mList = itemList;
        }

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {  // fill recycler tab with row values and view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,
                    parent, false);
            return new ListViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ListViewHolder holder, int position) {     //set values for the fields in the row_items layout
            ItemsModel currentItem = mList.get(position);
            i = position;
            Picasso.get().load(currentItem.getpPic()).into(holder.mImageView);
            holder.mTextView1.setText(currentItem.getfN() + " "+currentItem.getlN());
            holder.mTextView2.setText(currentItem.getCmp());
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void filterList(ArrayList<ItemsModel> filteredList) {
            mList = filteredList;
            notifyDataSetChanged();
        }
    }
