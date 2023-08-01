package com.example.smartbin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartbin.R;
import com.example.smartbin.adapter.RecyclerAdapter;
import com.example.smartbin.adapter.RecyclerAdapter1;
import com.example.smartbin.adapter.RecyclerAdapter2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Rewards extends AppCompatActivity {
    String[] data1 = new String[100];
    String[] data2 = new String[100];
    String[] data3 = new String[100];

    String[] Image = new String[100];
    String[] data4 = new String[100];
    String[] data5 = new String[100];
    String[] data6 = new String[100];

    String[] Image2 = new String[100];


    int count = 0;
    int count1 = 0;
    ImageView imageView;
    TextView t1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView recyclerView1 = findViewById(R.id.recyclerView1);
        imageView = findViewById(R.id.image);
        t1= findViewById(R.id.text1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Rewards.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Vposts");
        FirebaseUser fuser;
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String b= String.valueOf(snapshot.child("points").getValue());
                t1.setText("Total Points: "+b);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String temp = snapshot.child("count").getValue().toString();
                int count = Integer.parseInt(temp);
                for (int i = 0; i < count; i++) {
                    String temp2 = String.valueOf(i);
                    data1[i] = String.valueOf(snapshot.child(temp2).child("data1").getValue());
                    data2[i] = String.valueOf(snapshot.child(temp2).child("data2").getValue());
                    data3[i] = String.valueOf(snapshot.child(temp2).child("data3").getValue());
                    Image[i] = String.valueOf(snapshot.child(temp2).child("img").getValue());
                }
                RecyclerAdapter2 adapter = new RecyclerAdapter2(Rewards.this, data1, count, data2, data3, Image);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference myRef1 = firebaseDatabase.getReference("Hposts");
        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String temp = snapshot.child("count").getValue().toString();
                int count1 = Integer.parseInt(temp);
                for (int i = 0; i < count1; i++) {
                    String temp2 = String.valueOf(i);
                    data4[i] = String.valueOf(snapshot.child(temp2).child("data1").getValue());
                    data5[i] = String.valueOf(snapshot.child(temp2).child("data2").getValue());
                    data6[i] = String.valueOf(snapshot.child(temp2).child("data3").getValue());
                    Image2[i] = String.valueOf(snapshot.child(temp2).child("img").getValue());
                }

                LinearLayoutManager layoutManager1 = new LinearLayoutManager(Rewards.this);
                layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView1.setLayoutManager(layoutManager1);
                recyclerView1.setNestedScrollingEnabled(false);
                RecyclerAdapter1 adapter1 = new RecyclerAdapter1(Rewards.this, data4, count1, data5, data6, Image2);
                recyclerView1.setAdapter(adapter1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Rewards.this,HomePage.class);
                startActivity(i);
                finish();
            }
        });
    }
}