package com.example.smartbin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.smartbin.adapter.MyPagerAdapter;
import com.example.smartbin.fragment.HomeFragment;
import com.example.smartbin.fragment.ProfileFragment;
import com.example.smartbin.model.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class HomePage extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference myRef;

    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    FirebaseUser fuser;

    DatabaseReference reference;
    LinearLayout r1,r2,r3,r5,r6,r7,r8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser.getUid());

        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView openDrawerButton = findViewById(R.id.open);
        openDrawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TabItem home = findViewById(R.id.Home);
        TabItem search = findViewById(R.id.search);
        TabItem marketplace = findViewById(R.id.marketplace);
        TabItem chat = findViewById(R.id.chat);
        ViewPager viewPager = findViewById(R.id.viewpager);

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pagerAdapter.addFragment(new HomeFragment(), "Home");
        pagerAdapter.addFragment(new ProfileFragment(),"Profile");
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_home_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_person_24);

        navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        ImageView imageView = headerView.findViewById(R.id.image);
        TextView Pts = headerView.findViewById(R.id.text);
        TextView username = headerView.findViewById(R.id.username);
        TextView Id = headerView.findViewById(R.id.ID);
        r1 = headerView.findViewById(R.id.rel1);
        r2 = headerView.findViewById(R.id.rel2);
        r3 = headerView.findViewById(R.id.rel3);
        r5 = headerView.findViewById(R.id.rel5);
        r7 = headerView.findViewById(R.id.rel7);
        r8 = headerView.findViewById(R.id.rel8);
        r8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, MainActivity4.class);
                startActivity(i);
            }
        });
        r7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this,Instruction.class);
                startActivity(i);
            }
        });
        LinearLayout r6 = headerView.findViewById(R.id.rel6);

        r6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this,Help.class);
                startActivity(i);
            }
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("MyUsers")
                .child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String a= String.valueOf(snapshot.child("token").getValue());
                String b= String.valueOf(snapshot.child("points").getValue());
                Id.setText("ID: "+a);
                Pts.setText(b+" Points");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users user = dataSnapshot.getValue(Users.class);
                username.setText(user.getUsername());

                if (user.getImageURL().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(HomePage.this).load(user.getImageURL()).into(imageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(HomePage.this, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;

                    case R.id.maps:
                        Intent i = new Intent(HomePage.this, MainActivity2.class);
                        startActivity(i);
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, MainActivity2.class);
                startActivity(i);
            }
        });
        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this,AccountHistory.class);
                startActivity(i);
            }
        });
        r5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomePage.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void replace(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.test, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void CheckStatus(String status) {
        myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        myRef.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        CheckStatus("Offline");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }
}
