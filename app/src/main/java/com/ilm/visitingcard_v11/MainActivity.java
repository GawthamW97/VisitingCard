package com.ilm.visitingcard_v11;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

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

    ArrayList<ItemsModel> itemsListModel;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        /*
         * My code here deals with authentication
         *
         */
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
//
//        //Views
//        TextView mHeaderView = (TextView) findViewById(R.id.title_header);
//        ListView listItems = (ListView) findViewById(R.id.item_list);
//        //get Database
//        //Firebase
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(true)
//                .build();
//        db.setFirestoreSettings(settings);
//        //Set up the ArrayList
//        itemsListModel = new ArrayList<ItemsModel>();
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
//                List<ItemsModel> mMissionsList = new ArrayList<>();
//                if(task.isSuccessful()){
//                    //Log.e("Error",task.getResult().getDocuments().toString());
//                    for(QueryDocumentSnapshot document : task.getResult()) {
//                        ItemsModel model = document.toObject(ItemsModel.class);
//                        mMissionsList.add(model);
//                    }
//                    ListView itemListView = (ListView) findViewById(R.id.item_list);
////                    CustomAdapter mMissionAdapter = new CustomAdapter(MainActivity.this, mMissionsList);
////                    itemListView.setAdapter(mMissionAdapter);
//
//                    ListViewAdapter listViewAdapter = new ListViewAdapter(mMissionsList,MainActivity.this);
//                    itemListView.setAdapter(listViewAdapter);
//                } else {
//                    Log.d("MissionActivity", "Error getting documents: ", task.getException());
//                }
//            }
//        });

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



//    @Override
//    public void onBackPressed() {
//        if(FirebaseAuth.getInstance().getCurrentUser()!= null){
////            drawer.closeDrawer(GravityCompat.START);
//            FirebaseAuth.getInstance().signOut();
//        }else{
//            super.onBackPressed();
//        }
//    }
//
//
//    public class ListViewAdapter extends BaseAdapter implements Filterable {
//
//        private List<ItemsModel> itemModel;
//        private List<ItemsModel> itemsModelListFiltered;
//        private Context context;
//
//        public ListViewAdapter(List<ItemsModel> itemModel, Context context) {
//            this.itemModel = itemModel;
//            this.itemsModelListFiltered = itemModel;
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
//            View view = getLayoutInflater().inflate(R.layout.row_item, null);
//
//
//            TextView names = view.findViewById(R.id.user_name);
//            TextView emails = view.findViewById(R.id.user_mail);
//            ImageView imageView = view.findViewById(R.id.user_image);
//
//            names.setText((itemsModelListFiltered.get(position).getfName())+" "+(itemsModelListFiltered.get(position).getlName()));
//            emails.setText(itemsModelListFiltered.get(position).geteMail());
//            Picasso.get().load(itemsModelListFiltered.get(position).getProfilePic()).into(imageView);
//
//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e("main activity", "item clicked");
//                    startActivity(new Intent(MainActivity.this, ItemsPreviewActivity.class).putExtra("items", itemsModelListFiltered.get(position)));
//
//                }
//            });
//
//            return view;
//        }
//
//        @Override
//        public Filter getFilter() {
//            Filter filter = new Filter() {
//                @Override
//                protected FilterResults performFiltering(CharSequence constraint) {
//
//                    FilterResults filterResults = new FilterResults();
//                    if(constraint == null || constraint.length() == 0){
//                        filterResults.count = itemModel.size();
//                        filterResults.values = itemModel;
//
//                    }else{
//                        List<ItemsModel> resultsModel = new ArrayList<>();
//                        String searchStr = constraint.toString().toLowerCase();
//
//                        for(ItemsModel itemsModel: itemModel){
//                            if(itemsModel.getfName().contains(searchStr) || itemsModel.geteMail().contains(searchStr)){
//                                resultsModel.add(itemsModel);
//                                filterResults.count = resultsModel.size();
//                                filterResults.values = resultsModel;
//                            }
//                        }
//                    }
//                    return filterResults;
//                }
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
