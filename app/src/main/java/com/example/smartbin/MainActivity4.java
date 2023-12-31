package com.example.smartbin;

import static java.lang.Math.sqrt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smartbin.adapter.BinAdapter;
import com.example.smartbin.adapter.TempAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity4 extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText editText;
    ImageView imageView;
    BinAdapter binAdapter;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        Objects.requireNonNull(getSupportActionBar()).hide();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.parseColor("#F6F6F6"));}
        recyclerView = findViewById(R.id.recyclerView1);
        RecyclerView recyclerView2 = findViewById(R.id.recyclerView2);
        String idk[] = new String[100];
        String location[] = new String[100];
        String address[] = new String[100];
        String xcod[] = new String[100];
        String ycod[] = new String[100];
        String dis[] = new String[100];
        double x1 = 19.044488, y1 = 72.820551;
        double[] x2 = new double[100];
        double[] y2 = new double[100];
        editText = findViewById(R.id.edittext);
        imageView = findViewById(R.id.image);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeController(binAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            double userLatitude = location.getLatitude();
                            double userLongitude = location.getLongitude();
                            double a=userLatitude;
                            double b=userLongitude;

                            String toastMessage = "User Coordinates: Latitude = " + userLatitude +
                                    ", Longitude = " + userLongitude;
                            //Toast.makeText(MainActivity4.this, toastMessage, Toast.LENGTH_LONG).show();
                            // Logic to handle location object
                        }
                    }
                });

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
                    if (xcod[i] != null && ycod[i] != null) {
                        x2[i] = Double.parseDouble(xcod[i]);
                        y2[i] = Double.parseDouble(ycod[i]);
                        dis[i] = String.format("%.2f", Math.sqrt((x2[i] - x1) * (x2[i] - x1) + (y2[i] - y1) * (y2[i] - y1)) * 111.12) + "kms";
                    } else {
                        dis[i] = "N/A"; // Or any suitable placeholder for missing coordinates
                    }
                }
                LinearLayoutManager layoutManager1 = new LinearLayoutManager(MainActivity4.this);
                layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager1);
                BinAdapter binAdapter = new BinAdapter(MainActivity4.this, address, c, idk, location, dis);
                recyclerView.setAdapter(binAdapter);


                // Create a new LinearLayoutManager instance for recyclerView2
                LinearLayoutManager layoutManager2 = new LinearLayoutManager(MainActivity4.this);
                layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView2.setLayoutManager(layoutManager2);
                TempAdapter tempAdapter = new TempAdapter(MainActivity4.this, address, c, idk, location, dis);
                recyclerView2.setAdapter(tempAdapter);
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

                        // Create a new LinearLayoutManager instance for recyclerView2
                        LinearLayoutManager layoutManager2 = new LinearLayoutManager(MainActivity4.this);
                        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView2.setLayoutManager(layoutManager2);
                        TempAdapter tempAdapter = new TempAdapter(MainActivity4.this, address, filteredAddresses.size(), idk, location, dis);
                        recyclerView2.setAdapter(tempAdapter);
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

                        // Create a new LinearLayoutManager instance for recyclerView2
                        LinearLayoutManager layoutManager2 = new LinearLayoutManager(MainActivity4.this);
                        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView2.setLayoutManager(layoutManager2);
                        TempAdapter tempAdapter = new TempAdapter(MainActivity4.this, address, filteredAddresses.size(), idk, location, dis);
                        recyclerView2.setAdapter(tempAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    public class SwipeController extends ItemTouchHelper.Callback {
        private final BinAdapter adapter;

        public SwipeController(BinAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            // Define the swipe directions (left and right)
            int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(0, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            // Do nothing since we don't support moving items
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT) {

                Intent i = new Intent(MainActivity4.this,MainActivity2.class);
                startActivity(i);
                finish();
            } else if (direction == ItemTouchHelper.RIGHT) {
                Intent i = new Intent(MainActivity4.this,MainActivity2.class);
                startActivity(i);
                finish();
            }
        }
    }
}
