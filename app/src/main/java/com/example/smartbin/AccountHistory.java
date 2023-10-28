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

import com.example.smartbin.adapter.RecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AccountHistory extends AppCompatActivity {

    String[] Titles = new String[100];
    String[] Description = new String[100];
    String[] Image = new String[100];
    String[] Author = new String[100];
    int count;
    RecyclerView recyclerView;
    ImageView i1;
    RecyclerAdapter adapter;
    TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_history);
        recyclerView = findViewById(R.id.recyclerView);
        Objects.requireNonNull(getSupportActionBar()).hide();

        t1 = findViewById(R.id.text1);
        i1 = findViewById(R.id.image);
        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AccountHistory.this, HomePage.class);
                startActivity(i);
                finish();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(AccountHistory.this));
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef = firebaseDatabase.getReference("MyUsers").child(fuser.getUid()).child("points").child("received");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method will be called when the data is available.
                if (dataSnapshot.exists()) {
                    // Get the value from the DataSnapshot

                    // Now you can use the receivedPoints variable
                    // for whatever you need to do with the value.
                } else {
                    // The data doesn't exist at this reference.
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur.
            }
        });
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long redeemedPoints = snapshot.child("points").child("redeemed").getValue(Long.class);
                Long receivedPoints = snapshot.child("points").child("received").getValue(Long.class);
                int totalPoints = receivedPoints.intValue() - redeemedPoints.intValue();
                t1.setText("Total Points: " + totalPoints);
                Long pts = snapshot.child("points").child("received").getValue(Long.class);
                int c = pts.intValue();
                c = c / 20;
                for (int i = 0; i < 100; i++) {
                    Titles[i] = "Points Earned by Disposing Waste";
                    Description[i] = "+20 Pts";
                    Image[i] = "Trophy";
                    Author[i] = "Location: At Frcrce Dustbin";
                }
                adapter = new RecyclerAdapter(AccountHistory.this, Titles, c, Image, Description, Author);
                recyclerView.setAdapter(adapter);
                adapter.updateData(AccountHistory.this, Titles, c, Image, Description, Author);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
