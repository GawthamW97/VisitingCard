package com.ilm.visitingcard_v11.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ilm.visitingcard_v11.Notification;
import com.ilm.visitingcard_v11.NotificationAdapter;
import com.ilm.visitingcard_v11.R;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    private View mView;
    private NotificationAdapter notificationAdapter;
    private RecyclerView itemListView;
    private EditText searchList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<Notification> itemList;
    private ProgressBar progressBar;
    private LinearLayout layout;;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_main,container,false);
        progressBar = mView.findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.VISIBLE);
        layout = mView.findViewById(R.id.home_container);
        final SwipeRefreshLayout pullToRefresh = mView.findViewById(R.id.pullToRefresh);
        progressBar.setVisibility(View.INVISIBLE);
        searchList = mView.findViewById(R.id.search_list);
        itemListView = mView.findViewById(R.id.item_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(NotificationFragment.this.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        itemListView.setHasFixedSize(true);
        itemListView.addItemDecoration(new DividerItemDecoration(itemListView.getContext(), layoutManager.getOrientation()));
        itemListView.setLayoutManager(layoutManager);
        itemListView.setVisibility(View.INVISIBLE);
        // Refresh the view on pull down
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.VISIBLE);
                layout.setVisibility(View.INVISIBLE);
                showData(mView);
                pullToRefresh.setRefreshing(false);
                progressBar.setVisibility(View.INVISIBLE);
                layout.setVisibility(View.VISIBLE);
            }
        });
        showData(mView);
        return mView;
    }

    //Show data
    private void showData(View mView) {
        //Get the list of Notification from the database and compare with the list of users that the current user has connected
        db.collection("user").document(mAuth.getCurrentUser().getUid())
                .collection("notify").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                itemList = new ArrayList<Notification>();
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Notification model = document.toObject(Notification.class);
                        model.setUID(document.getId());                           // SET THE UID FROM FIRESTORE OF THE ITEM TO THE OBJECT
                        itemList.add(model);
                    }
                    notificationAdapter = new NotificationAdapter(itemList);
                    itemListView.setAdapter(notificationAdapter);
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });

        itemListView.setVisibility(View.VISIBLE);

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
    }

    private void filter(String text) {                           //Filter search values for each characters
        //new array list that will hold the filtered data
        ArrayList<Notification> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (Notification model : itemList) {
            if ((model.getName().toLowerCase().trim().contains(text.toLowerCase()))
                    || (model.getNotificationMessage().toLowerCase().contains(text.toLowerCase()))){        //if the existing elements contains the search input

                //adding the element to filtered list
                filterdNames.add(model);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        notificationAdapter.filterList(filterdNames);
    }
}
