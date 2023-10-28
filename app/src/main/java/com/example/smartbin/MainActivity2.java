package com.example.smartbin;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationListener {
    private GoogleMap googleMap;
    private LocationManager locationManager;
    Location location1;
    private FloatingActionButton centerButton;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize locationCallback to receive location updates
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    location1 = locationResult.getLastLocation();
                    if (location1 != null) {
                        // Display coordinates in a Toast message
                        String message = "Latitude: " + location1.getLatitude() + "\nLongitude: " + location1.getLongitude();
                        //  Toast.makeText(MainActivity2.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        // Check and request location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Start receiving location updates
            startLocationUpdates();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        centerMapToUserLocation();
        centerButton = findViewById(R.id.centerButton);
        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerMapToUserLocation();
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Request location updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this); // Update every 10 seconds or 10 meters change
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        List<Place> places = new ArrayList<>();
        places.add(new Place("Place 1", 19.118940, 72.880755));
        places.add(new Place("Place 2", 19.263434, 72.968624));
        places.add(new Place("Place 3", 19.194976, 72.835818));
        places.add(new Place("Place 4", 18.943044, 72.828842));
        places.add(new Place("Place 5", 19.044329, 72.820380));

        for (Place place : places) {
            LatLng placeLatLng = new LatLng(place.getLatitude(), place.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(placeLatLng)
                    .title(place.getName()));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            return true;
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSEnabled) {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
            return true;
        }

        try {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                LatLng destinationLatLng = marker.getPosition();
                LatLng originLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                String directionsUrl = getDirectionsUrl(originLatLng, destinationLatLng);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(directionsUrl));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e) {
            // Handle the exception, for example, display a message to the user.
            Toast.makeText(this, "Unable to retrieve current location due to GPS permission issue", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    private String getDirectionsUrl(LatLng origin, LatLng destination) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String parameters = strOrigin + "&" + strDestination + "&" + sensor;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    private class Place {
        private String name;
        private double latitude;
        private double longitude;

        public Place(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (googleMap != null) {
            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

            // Save the last known location
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("lastLatitude", (float) location.getLatitude());
            editor.putFloat("lastLongitude", (float) location.getLongitude());
            editor.apply();

            // Remove location updates after receiving a valid location
            locationManager.removeUpdates(this);
        }
    }

    private void centerMapToUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (location1 == null) {

                fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                        } else {
                            LatLng defaultLatLng = new LatLng(location1.getLatitude(), location1.getLongitude());
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 15));
                        }
                    }
                });
            } else {
                LatLng defaultLatLng = new LatLng(location1.getLatitude(), location1.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 15));
            }
        } else {
            LatLng defaultLatLng = new LatLng(18.943044, 72.828842);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 15));
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000) // Update interval in milliseconds
                .setFastestInterval(5000); // Fastest update interval in milliseconds

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start receiving location updates
                startLocationUpdates();
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
                googleMap.setMyLocationEnabled(true);
                googleMap.setOnMarkerClickListener(this);
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop location updates when the activity is destroyed
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}