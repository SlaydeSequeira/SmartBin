package com.example.smartbin;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TruckMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    private MapView mapView;
    private Polyline line;
    LatLng P,next;
    double xcod[]= new double[100];
    double ycod[]= new double[100];
    double x=19.263434;
    double y=72.968624;
    LatLng all[]=new LatLng[100];
    String a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_map);
        Intent intent = getIntent();
        int count = intent.getIntExtra("swiped",0);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        a= String.valueOf(count);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.imp);

            // Define the desired width and height for the scaled icon
            int width = 100;  // Change this to your desired width
            int height = 100; // Change this to your desired height

            // Scale down the bitmap to the desired dimensions
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);

            // Create a BitmapDescriptor from the scaled bitmap
            BitmapDescriptor customMarkerIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("trucks").child(a);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(int i=0;i<4;i++)
                    {
                        String temp=String.valueOf(i);
                        xcod[i]= Double.parseDouble(String.valueOf(snapshot.child("x").child(temp).getValue()));
                        ycod[i]= Double.parseDouble(String.valueOf(snapshot.child("y").child(temp).getValue()));
                        P= new LatLng(xcod[i], ycod[i]); // Replace with your desired coordinates
                        all[i]=P;
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(P, 15));
                    LatLng P = new LatLng(xcod[3], ycod[3]);
                    mMap.addMarker(new MarkerOptions().position(P).icon(customMarkerIcon).title("Initial"));
                    for(int i=0;i<3;i++)
                    {
                        mMap.addMarker(new MarkerOptions().position(all[i]).title("Stop "+(3-i)));
                        PolylineOptions lineOptions = new PolylineOptions()
                                .add(all[i], all[i+1])
                                .width(10) // Line width in pixels
                                .color(Color.GREEN); // Line color
                        line = mMap.addPolyline(lineOptions);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } else {
            // Handle the case where mMap is null
        }

    }


}
