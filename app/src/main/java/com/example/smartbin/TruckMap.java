package com.example.smartbin;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
        import androidx.fragment.app.FragmentActivity;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TruckMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truck_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mMap != null) {
            LatLng markerLocation = new LatLng(19, 78);

            // Load the original bitmap icon
            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.imp);

            // Define the desired width and height for the scaled icon
            int width = 100;  // Change this to your desired width
            int height = 100; // Change this to your desired height

            // Scale down the bitmap to the desired dimensions
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false);

            // Create a BitmapDescriptor from the scaled bitmap
            BitmapDescriptor customMarkerIcon = BitmapDescriptorFactory.fromBitmap(scaledBitmap);

            // Add the custom marker to the map
            mMap.addMarker(new MarkerOptions()
                    .position(markerLocation)
                    .icon(customMarkerIcon)
                    .title("Marker Title"));

            // Move the camera to the marker's location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLocation, 12));
        } else {
            // Handle the case where mMap is null
        }
    }


}

