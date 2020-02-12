package com.ilm.visitingcard_v11.Fragements;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ilm.visitingcard_v11.ItemsModel;
import com.ilm.visitingcard_v11.ListItemAdapter;
import com.ilm.visitingcard_v11.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

//    ListViewAdapter listViewAdapter;
    private ListItemAdapter listAdapter;
    private RecyclerView itemListView;
    private EditText searchList;
    View mView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<ItemsModel> itemList;
    List<String> conn_list;
//    //declaring variables
//    private ListView listviewforresults;
//    //Adapter for listview
//    ArrayAdapter<String> ReAdapter;
//    //Edittext for search
//    EditText searchdata;
//    //ArrayList for listview
//    ArrayList<String>  data=new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.activity_main,container,false);

        searchList = mView.findViewById(R.id.search_list);
        itemListView = mView.findViewById(R.id.item_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeFragment.this.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        itemListView.setHasFixedSize(true);
        itemListView.addItemDecoration(new DividerItemDecoration(itemListView.getContext(), layoutManager.getOrientation()));
        itemListView.setLayoutManager(layoutManager);
        db.collection("user").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        ItemsModel list = doc.toObject(ItemsModel.class);
                        conn_list = Objects.requireNonNull(list).getConn();
                    }
                });
        db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                itemList = new ArrayList<ItemsModel>();
                if(task.isSuccessful()){
                    //Log.e("Error",task.getResult().getDocuments().toString());
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Log.e("ID",document.getId());
                        if(conn_list.contains(document.getId())){
                            ItemsModel model = document.toObject(ItemsModel.class);
                            itemList.add(model);
                        }else{
                            continue;
                        }
                    }
                    listAdapter = new ListItemAdapter(itemList);
                    itemListView.setAdapter(listAdapter);
                } else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }
            }
        });

        searchList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("char",s.toString());
                filter(s.toString());
            }
        });
     return mView;
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<ItemsModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (ItemsModel model : itemList) {
            //if the existing elements contains the search input
            if ((model.getfName().toLowerCase().trim().contains(text.toLowerCase())) || (model.geteMail().toLowerCase().contains(text.toLowerCase()))){
                //adding the element to filtered list
                filterdNames.add(model);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        listAdapter.filterList(filterdNames);
    }
}




