package com.ilm.visitingcard_v11;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ilm.visitingcard_v11.Fragments.AddFragment;
import com.ilm.visitingcard_v11.Fragments.HomeFragment;
import com.ilm.visitingcard_v11.Fragments.ProfileFragment;
import com.ilm.visitingcard_v11.Fragments.SettingFragment;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TextView userName,userMail;
    ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        View mView = getLayoutInflater().inflate(R.layout.nav_header,null);
        userName = mView.findViewById(R.id.nav_user);
        userMail = mView.findViewById(R.id.nav_mail);
        userImage = mView.findViewById(R.id.nav_image);

        final FirebaseAuth mAuth =FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        mAuth.signOut();
        db.collection("user").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.e("Fail", mAuth.getCurrentUser().getUid());
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    ItemsModel itemsModel = Objects.requireNonNull(doc).toObject(ItemsModel.class);
//                    Log.e("list", Objects.requireNonNull(itemsModel).getfName().toString());

                    userName.setText(Objects.requireNonNull(itemsModel).getfName());
                    userMail.setText(itemsModel.geteMail());
                    Picasso.get().load(itemsModel.getProfilePic()).into(userImage);

                    Log.e("TAG", "Success");
                }else{
                    Log.e("TAG", "Failed");
                }
            }
        });

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.addHeaderView(mView);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment,new HomeFragment());
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        drawerLayout.closeDrawer(GravityCompat.START);
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if(menuItem.getItemId() == R.id.nav_home){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment,new HomeFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        if(menuItem.getItemId() == R.id.nav_profile){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment,new ProfileFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        if(menuItem.getItemId() == R.id.nav_addCard){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment,new AddFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        if(menuItem.getItemId() == R.id.nav_logout){
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(NavigationActivity.this,LoginActivity.class));
        }

        if(menuItem.getItemId() == R.id.nav_settings){
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment,new SettingFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
                return true;
    }

    //AFTER THE SCAN IS COMPLETED AND THE SCANNED USER-ID IS PASSED TO 'ItemsPreviewActivity.java'
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG","Calls Nav"+requestCode);
//        for (Fragment fragment : fragmentManager.getFragments()) {
//            Log.e("TAG","Calls");
//
//            fragment.onActivityResult(requestCode, resultCode, data);
//        }
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning!", Toast.LENGTH_LONG).show();
            } else {
                startActivity(new Intent(this,ItemsPreviewActivity.class).putExtra("id",result.getContents()));
            }
        }
    }

}

