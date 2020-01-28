package com.ilm.visitingcard_v11;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    List<ItemsModel> itemsModelList = new ArrayList<>();
//    ListView listView;
//
//    RecyclerView recyclerView;
//    FirebaseDatabase firebaseDatabase;
//    DatabaseReference mRef;

//    com.ilm.visitingcard_v11.CustomAdapter customAdapter;
//    private DrawerLayout drawer;

//    private ListView listView;

    private final String COLLECTION_KEY = "user";

    //Views
    private TextView mHeaderView;
    private ListView mMissionListView;

    //Firebase
    private FirebaseFirestore db;

    //Adapter
    private CustomAdapter mMissionAdapter;
    private ArrayList<ItemsModel> mMissionsList;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        /*
         * My code here deals with authentication
         *
         */
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mHeaderView = (TextView) findViewById(R.id.missionHeader);
        mMissionListView = (ListView) findViewById(R.id.missionList);
        //get Database
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        //Set up the ArrayList
        mMissionsList = new ArrayList<ItemsModel>();
        //set the Adapter
        mMissionAdapter = new CustomAdapter(this, mMissionsList);

        mMissionListView.setAdapter(mMissionAdapter);

        db.collection(COLLECTION_KEY).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<ItemsModel> mMissionsList = new ArrayList<>();
                if(task.isSuccessful()){
                    //Log.e("Error",task.getResult().getDocuments().toString());
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Log.e("Values",document.getData().toString());
                        ItemsModel model = document.toObject(ItemsModel.class);
                        mMissionsList.add(model);
                    }
                    ListView mMissionsListView = (ListView) findViewById(R.id.missionList);
                    CustomAdapter mMissionAdapter = new CustomAdapter(MainActivity.this, mMissionsList);
                    mMissionsListView.setAdapter(mMissionAdapter);
                } else {
                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
                }
            }
        });


//


    }
//        Log.d("Start","Initialized");
//
//        final FirebaseFirestore db = FirebaseFirestore.getInstance();
//        listView = findViewById(R.id.list_view);
//
//        Map<String, Object> user = new HashMap<>();
//        user.put("first", "Ada");
//        user.put("last", "Lovelace");
//        user.put("born", 1815);

// Add a new document with a generated ID
//        db.collection("users")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.e("e", "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("e", "Error adding document", e);
//                    }
//                });

//        db.collection("user").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("d", document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.d("d", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//        db.collection("user").document("1").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                Log.e("Errors",task.getException().toString());
//                List<ItemsModel> mProductsList = new ArrayList<>();
//                if(task.isSuccessful()){
//                    Log.e("error", String.valueOf(task.getResult().getData()));
////                    for(QueryDocumentSnapshot document : task.getResult()) {
////                        ItemsModel miss = document.toObject(ItemsModel.class);
////                        mProductsList.add(miss);
////                    }
//                    Log.e("Errors",task.getException().toString());
//
//                    CustomAdapter mProductAdapter = new CustomAdapter(MainActivity.this, mProductsList);
//
//                    mProductAdapter.notifyDataSetChanged();
//                    listView.setAdapter(mProductAdapter);
//                }
//            }
//        });

//        db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                List<ItemsModel> mMissionsList = new ArrayList<>();
//                if(task.isSuccessful()){
//                    for(QueryDocumentSnapshot document : task.getResult()) {
//                        ItemsModel miss = document.toObject(ItemsModel.class);
//                        mMissionsList.add(miss);
//                    }
//                    ListView mMissionsListView = (ListView) findViewById(R.id.list_view);
//                    CustomAdapter mMissionAdapter = new CustomAdapter(MainActivity.this, mMissionsList);
//                    mMissionsListView.setAdapter(mMissionAdapter);
//                } else {
//                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
//                }
//            }
//        });



//        CollectionReference colRef = db.collection("users");
//        colRef.document().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.e("DocumentSnapshot data: " , document.getData().toString());
//                    } else {
//                        Log.e("No such document","empty");
//                    }
//                } else {
//                    Log.e( "get failed with ", task.getException().toString());
//                }
//            }
//        });

//        db.collection("users")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("TAG",document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.w("TAG","Error getting documents", task.getException());
//                        }
//                    }
//                });



    @Override
    public void onBackPressed() {
        if(FirebaseAuth.getInstance().getCurrentUser()!= null){
//            drawer.closeDrawer(GravityCompat.START);
//            FirebaseAuth.getInstance().signOut();
        }else{
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.menu,menu);
//
//        MenuItem menuItem = menu.findItem(R.id.searchView);
//
//        SearchView searchView = (SearchView) menuItem.getActionView();
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                Log.e("Main"," data search"+newText);
//
//                customAdapter.getFilter().filter(newText);
//
//                return true;
//            }
//        });
//
//
//        return true;
//
//    }


//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        int id = item.getItemId();
//
//
//        if(id == R.id.searchView){
//
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        switch (menuItem.getItemId()){
//            case R.id.nav_home:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new HomeFragment()).commit();
//                break;
//            case R.id.nav_profile:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ProfileFragment()).commit();
//                break;
//            case R.id.nav_addCard:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new AddFragment()).commit();
//                break;
//            case R.id.nav_share:
//                Toast.makeText(this,"Share",Toast.LENGTH_SHORT).show();
//                break;
//
//            case R.id.nav_send:
//                Toast.makeText(this,"Send",Toast.LENGTH_SHORT).show();
//                break;
//        }
//
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }

//    public class com.ilm.visitingcard_v11.CustomAdapter extends BaseAdapter implements Filterable {
//
//        private List<ItemsModel> itemsModelsl;
//        private List<ItemsModel> itemsModelListFiltered;
//        private Context context;
//
//        public com.ilm.visitingcard_v11.CustomAdapter(List<ItemsModel> itemsModelsl, Context context) {
//            this.itemsModelsl = itemsModelsl;
//            this.itemsModelListFiltered = itemsModelsl;
//            this.context = context;
//        }
//
//        @Override
//        public int getCount() {
//            return itemsModelListFiltered.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return itemsModelListFiltered.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            View view = getLayoutInflater().inflate(R.layout.row_item,null);
//
//
//            TextView names = view.findViewById(R.id.name);
//            TextView emails = view.findViewById(R.id.email);
//            ImageView imageView = view.findViewById(R.id.images);
//
//            names.setText(itemsModelListFiltered.get(position).getName());
//            emails.setText(itemsModelListFiltered.get(position).getEmail());
//            imageView.setImageResource(itemsModelListFiltered.get(position).getImages());
//
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e("main activity","item clicked");
//                    startActivity(new Intent(MainActivity.this,ItemsPreviewActivity.class).putExtra("items",itemsModelListFiltered.get(position)));
//
//                }
//            });
//
//            return view;
//        }
//
//
//
//        @Override
//        public Filter getFilter() {
//            Filter filter = new Filter() {
//                @Override
//                protected FilterResults performFiltering(CharSequence constraint) {
//
//                    FilterResults filterResults = new FilterResults();
//                    if(constraint == null || constraint.length() == 0){
//                        filterResults.count = itemsModelsl.size();
//                        filterResults.values = itemsModelsl;
//
//                    }else{
//                        List<ItemsModel> resultsModel = new ArrayList<>();
//                        String searchStr = constraint.toString().toLowerCase();
//
//                        for(ItemsModel itemsModel:itemsModelsl){
//                            if(itemsModel.getName().toLowerCase().contains(searchStr.toLowerCase()) || itemsModel.getEmail().toLowerCase().contains(searchStr.toLowerCase())){
//                                resultsModel.add(itemsModel);
//
//                            }
//                            filterResults.count = resultsModel.size();
//                            filterResults.values = resultsModel;
//                        }
//
//
//                    }
//
//                    return filterResults;
//                }
//
//                @Override
//                protected void publishResults(CharSequence constraint, FilterResults results) {
//
//                    itemsModelListFiltered = (List<ItemsModel>) results.values;
//                    notifyDataSetChanged();
//
//                }
//            };
//            return filter;
//        }
//    }

}
