package com.example.smartbin;

import static java.lang.Math.sqrt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.smartbin.adapter.BinAdapter;
import com.example.smartbin.adapter.RecyclerAdapter1;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity4 extends AppCompatActivity {
RecyclerView recyclerView;
EditText editText;
ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        recyclerView = findViewById(R.id.recyclerView1);
        String idk[] = new String[100];
        String location[] = new String[100];
        String address[] = new String[100];
        String xcod[] = new String[100];
        String ycod[] = new String[100];
        String dis[] = new String[100];
        int x1 = 0, y1 = 0;
        int[] x2 = new int[100];
        int[] y2 = new int[100];
        editText = findViewById(R.id.edittext);
        imageView = findViewById(R.id.image);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("bins");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String count = String.valueOf(snapshot.child("count").getValue());
                int c = Integer.parseInt(count);
                for (int i = 0; i < c; i++) {
                    String temp = String.valueOf(i);
                    location[i] = String.valueOf(snapshot.child("loc").child(temp).getValue());
                    address[i] = String.valueOf(snapshot.child("add").child(temp).getValue());
                    xcod[i] = String.valueOf(snapshot.child("x").child(temp).getValue());
                    ycod[i] = String.valueOf(snapshot.child("y").child(temp).getValue());
                    x2[i] = Integer.parseInt(xcod[i]);
                    y2[i] = Integer.parseInt(ycod[i]);
                    dis[i] = String.format("%.2f", Math.sqrt((x2[i] - x1) * (x2[i] - x1) - (y2[i] - y1) * (y2[i] - y1)) * 111.12) + "kms";
                }
                LinearLayoutManager layoutManager1 = new LinearLayoutManager(MainActivity4.this);
                layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager1);
                BinAdapter binAdapter = new BinAdapter(MainActivity4.this, address, c, idk, location, dis);
                recyclerView.setAdapter(binAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("bins");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String searchQuery = editText.getText().toString().toLowerCase();

                        ArrayList<String> filteredAddresses = new ArrayList<>();
                        ArrayList<String> filteredLocations = new ArrayList<>();
                        ArrayList<String> filteredDistances = new ArrayList<>();
                        String count = String.valueOf(snapshot.child("count").getValue());
                        int c = Integer.parseInt(count);
                        for (int i = 0; i < c; i++) {
                            if (location[i].toLowerCase().contains(searchQuery) || address[i].toLowerCase().contains(searchQuery)) {
                                filteredAddresses.add(address[i]);
                                filteredLocations.add(location[i]);
                                filteredDistances.add(dis[i]);
                            }
                        }

                        LinearLayoutManager layoutManager1 = new LinearLayoutManager(MainActivity4.this);
                        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager1);

                        BinAdapter binAdapter = new BinAdapter(MainActivity4.this, filteredAddresses.toArray(new String[0]),
                                filteredAddresses.size(), idk, filteredLocations.toArray(new String[0]),
                                filteredDistances.toArray(new String[0]));

                        recyclerView.setAdapter(binAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return false;
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("bins");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String searchQuery = editText.getText().toString().toLowerCase();

                        ArrayList<String> filteredAddresses = new ArrayList<>();
                        ArrayList<String> filteredLocations = new ArrayList<>();
                        ArrayList<String> filteredDistances = new ArrayList<>();
                        String count = String.valueOf(snapshot.child("count").getValue());
                        int c = Integer.parseInt(count);
                        for (int i = 0; i < c; i++) {
                            if (location[i].toLowerCase().contains(searchQuery) || address[i].toLowerCase().contains(searchQuery)) {
                                filteredAddresses.add(address[i]);
                                filteredLocations.add(location[i]);
                                filteredDistances.add(dis[i]);
                            }
                        }

                        LinearLayoutManager layoutManager1 = new LinearLayoutManager(MainActivity4.this);
                        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager1);

                        BinAdapter binAdapter = new BinAdapter(MainActivity4.this, filteredAddresses.toArray(new String[0]),
                                filteredAddresses.size(), idk, filteredLocations.toArray(new String[0]),
                                filteredDistances.toArray(new String[0]));

                        recyclerView.setAdapter(binAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }
}