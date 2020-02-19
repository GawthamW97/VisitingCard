package com.ilm.visitingcard_v11.Fragments;

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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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

    private ListItemAdapter listAdapter;
    private RecyclerView itemListView;
    private EditText searchList;
    private View mView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<ItemsModel> itemList;
    private List<String> conn_list = null;

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

        //Get a list of connections that the current user has.

        db.collection("user").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        ItemsModel list = doc.toObject(ItemsModel.class);
                        if(list.getConn()== null){
                            conn_list = null;
                        }else
                        {
                            conn_list = Objects.requireNonNull(list).getConn();
                        }
                    }
                });

        //Get the list of users from the database and compare with the list of users that the current user has connected

        db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                itemList = new ArrayList<ItemsModel>();
                if(task.isSuccessful()){
                    //Log.e("Error",task.getResult().getDocuments().toString());
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        if((conn_list != null)&&conn_list.contains(document.getId())){
                            ItemsModel model = document.toObject(ItemsModel.class);
                            // SET THE UID FROM FIRESTORE OF THE ITEM TO THE OBJECT
                            model.setUID(document.getId());
                            itemList.add(model);
                            Log.e("ID",conn_list.toString());
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

        // Search the list from the characters the user inserts in the text field.

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

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Log.e("POS",String.valueOf(position));
//                Log.e("POS",String.valueOf(conn_list.get(position)));
//                Log.e("POS",String.valueOf(conn_list));
                db.collection("user").document(mAuth.getCurrentUser().getUid())
                        .update("conn", FieldValue.arrayRemove(conn_list.get(position)).toString());
                conn_list.remove(position);
                listAdapter.notifyItemRemoved(position);
            }
        });

        helper.attachToRecyclerView(itemListView);
     return mView;
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<ItemsModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (ItemsModel model : itemList) {
            //if the existing elements contains the search input
            if ((model.getfName().toLowerCase().trim().contains(text.toLowerCase())) || (model.getCompany().toLowerCase().contains(text.toLowerCase()))){
                //adding the element to filtered list
                filterdNames.add(model);
            }
        }
        //calling a method of the adapter class and passing the filtered list
        listAdapter.filterList(filterdNames);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FragmentManager fragmentManager = this.
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.home_container, new HomeFragment())
//                .commit();
    }
}




