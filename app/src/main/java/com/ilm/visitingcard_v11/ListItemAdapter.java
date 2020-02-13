package com.ilm.visitingcard_v11;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ListViewHolder> {
        private ArrayList<ItemsModel> mList;
        private Context context;
        private int i;

        public class ListViewHolder extends RecyclerView.ViewHolder {
            public ImageView mImageView;
            public TextView mTextView1;
            public TextView mTextView2;

            public ListViewHolder(final View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.user_image);
                mTextView1 = itemView.findViewById(R.id.user_name);
                mTextView2 = itemView.findViewById(R.id.user_mail);

                context = itemView.getContext();

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("POSITION",String.valueOf(getAdapterPosition()));
                        context.startActivity(new Intent(context,ItemsPreviewActivity.class).putExtra("items",mList.get(getAdapterPosition())));
                    }
                });
            }
        }

        public ListItemAdapter(ArrayList<ItemsModel> exampleList) {
            mList = exampleList;
        }

        @Override
        public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,
                    parent, false);
            ListViewHolder evh = new ListViewHolder(v);
            return evh;
        }

        @Override
        public void onBindViewHolder(ListViewHolder holder, int position) {
            ItemsModel currentItem = mList.get(position);
            i = position;
//            holder.mImageView.setImageURI();
            holder.mTextView1.setText(currentItem.getfName());
            holder.mTextView2.setText(currentItem.geteMail());

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
