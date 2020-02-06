package com.ilm.visitingcard_v11.Fragements;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ilm.visitingcard_v11.ItemsModel;
import com.ilm.visitingcard_v11.ItemsPreviewActivity;
import com.ilm.visitingcard_v11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    HomeFragment.ListViewAdapter listViewAdapter;
    ListView itemListView;
    EditText searchList;
    View mView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_main,container,false);
        searchList = mView.findViewById(R.id.search_list);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        String COLLECTION_KEY = "user";
        db.collection(COLLECTION_KEY).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<ItemsModel> itemList = new ArrayList<>();
                if(task.isSuccessful()){
                    //Log.e("Error",task.getResult().getDocuments().toString());
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        ItemsModel model = document.toObject(ItemsModel.class);
                        itemList.add(model);
                    }
                    itemListView = mView.findViewById(R.id.item_list);
                    listViewAdapter = new HomeFragment.ListViewAdapter(itemList,HomeFragment.this.getActivity());
                    itemListView.setAdapter(listViewAdapter);

                    searchList.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            HomeFragment.this.listViewAdapter.getFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                } else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }
            }
        });
     return mView;
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
////        TextView mHeaderView = getView().findViewById(R.id.title_header);
////        ListView listItems = getView().findViewById(R.id.item_list);
//        //get Database
//        //Firebase
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .build();
//        db.setFirestoreSettings(settings);
//        //set the Adapter
//        //Adapter
////        CustomAdapter listItemAdapter = new CustomAdapter(this, itemsListModel);
////        listItems.setAdapter(listItemAdapter);
//
//
//        String COLLECTION_KEY = "user";
//        db.collection(COLLECTION_KEY).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                List<ItemsModel> itemList = new ArrayList<>();
//                if(task.isSuccessful()){
//                    //Log.e("Error",task.getResult().getDocuments().toString());
//                    for(QueryDocumentSnapshot document : task.getResult()) {
//                        ItemsModel model = document.toObject(ItemsModel.class);
//                        itemList.add(model);
//                    }
//                    itemListView = getView().findViewById(R.id.item_list);
//                    listViewAdapter = new HomeFragment.ListViewAdapter(itemList,HomeFragment.this.getActivity());
//                    itemListView.setAdapter(listViewAdapter);
//                } else {
//                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
//                }
//            }
//        });
//    }

    public class ListViewAdapter extends BaseAdapter implements Filterable {

        private List<ItemsModel> itemModel;
        private List<ItemsModel> itemsModelListFiltered;
        private Context context;

        public ListViewAdapter(List<ItemsModel> itemModel, Context context) {
            this.itemModel = itemModel;
            this.itemsModelListFiltered = itemModel;
            this.context = context;
        }

        @Override
        public int getCount() {
                return itemsModelListFiltered.size();
        }

        @Override
        public Object getItem(int position) {
            return itemsModelListFiltered.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.row_item, null);


            TextView names = view.findViewById(R.id.user_name);
            TextView emails = view.findViewById(R.id.user_mail);
            ImageView imageView = view.findViewById(R.id.user_image);

            names.setText((itemsModelListFiltered.get(position).getfName())+" "+(itemsModelListFiltered.get(position).getlName()));
            emails.setText(itemsModelListFiltered.get(position).geteMail());
            Picasso.get().load(itemsModelListFiltered.get(position).getProfilePic()).into(imageView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("main activity", "item clicked");
                    startActivity(new Intent(HomeFragment.this.getActivity(), ItemsPreviewActivity.class).putExtra("items", itemsModelListFiltered.get(position)));

                }
            });

            return view;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();
                    if(constraint == null || constraint.length() == 0){
                        filterResults.count = itemModel.size();
                        filterResults.values = itemModel;
                    }else{
                        List<ItemsModel> resultsModel = new ArrayList<>();
                        String searchStr = constraint.toString().toLowerCase();

                        for(ItemsModel itemsModel: itemModel){
                            if(itemsModel.getfName().toLowerCase().contains(searchStr.toLowerCase()) || itemsModel.geteMail().contains(searchStr.toLowerCase())){
                                resultsModel.add(itemsModel);
                                filterResults.count = resultsModel.size();
                                filterResults.values = resultsModel;
                            }
                        }
                    }
                    return filterResults;
                }
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    itemsModelListFiltered = (List<ItemsModel>) results.values;
                    notifyDataSetChanged();
                }
            };
            return filter;
        }
    }

}
