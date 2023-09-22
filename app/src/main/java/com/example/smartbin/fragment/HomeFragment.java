package com.example.smartbin.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.smartbin.AccountHistory;
import com.example.smartbin.MainActivity2;
import com.example.smartbin.R;
import com.example.smartbin.Rewards;
import com.example.smartbin.adapter.ImageSliderAdapter;
import com.example.smartbin.model.Users;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Marker marker;
    private int[] images = {R.drawable.pic1, R.drawable.pic2};
    private CardView c1, c2;
    ImageView i1;
    TextView t1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView textView = rootView.findViewById(R.id.text);
        ImageView imageView = rootView.findViewById(R.id.image);
        ViewPager2 viewPager = rootView.findViewById(R.id.viewPager);
        ImageSliderAdapter adapter = new ImageSliderAdapter(getActivity(), images);
        c1 = rootView.findViewById(R.id.card1);
        c2 = rootView.findViewById(R.id.card2);
        t1 = rootView.findViewById(R.id.mytext);
        i1 = rootView.findViewById(R.id.myimg);
        FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
        FirebaseUser fuser;
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MyUsers")
                .child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                if (user != null) {
                    t1.setText("Hello, "+user.getUsername());

                    if ("default".equals(user.getImageURL())) {
                        imageView.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(requireContext()).load(user.getImageURL()).into(i1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AccountHistory.class);
                startActivity(i);
            }
        });
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Rewards.class);
                startActivity(i);
            }
        });
        viewPager.setAdapter(adapter);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MainActivity2.class);
                startActivity(i);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MainActivity2.class);
                startActivity(i);
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateCurrentLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Customize the map as needed
        // Create a list of places with their coordinates
        List<Place> places = new ArrayList<>();
        places.add(new Place("Place 1", 19.118940, 72.880755));
        places.add(new Place("Place 2", 19.263434, 72.968624));
        places.add(new Place("Place 3", 19.194976, 72.835818));
        places.add(new Place("Place 4", 18.943044, 72.828842));
        places.add(new Place("Place 5", 19.044329, 72.820380));
        // Add markers for each place
        for (Place place : places) {
            LatLng placeLatLng = new LatLng(place.getLatitude(), place.getLongitude());
            googleMap.addMarker(new MarkerOptions()
                    .position(placeLatLng)
                    .title(place.getName()));
        }
        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMarkerClickListener(this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                LatLng currentLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15)); // Adjust the zoom level as needed
                updateCurrentLocation(lastKnownLocation);
            }

            // Automatically recenter and zoom in on the user's location
            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (lastKnownLocation != null) {
                        LatLng currentLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15)); // Adjust the zoom level as needed
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, handle the case where the permission is not available
            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
            return true;
        }

        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSEnabled) {
            // Prompt the user to enable GPS
            Toast.makeText(requireContext(), "Please enable GPS", Toast.LENGTH_SHORT).show();
            return true; // Return true to consume the marker click event
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            LatLng destinationLatLng = marker.getPosition();
            LatLng originLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            String directionsUrl = getDirectionsUrl(originLatLng, destinationLatLng);
            // Start an intent to open Google Maps with the directions URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(directionsUrl));
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "Unable to retrieve current location", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // Initialize location-related functionality here, such as enabling location on the map
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    googleMap.setOnMarkerClickListener(this);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastKnownLocation != null) {
                        updateCurrentLocation(lastKnownLocation);
                    }
                }
            } else {
                // Permission denied
                // Handle the case where the user denied the permission
                Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng destination) {
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String strDestination = "destination=" + destination.latitude + "," + destination.longitude;
        String sensor = "sensor=false";
        String parameters = strOrigin + "&" + strDestination + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
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

    private void updateCurrentLocation(Location location) {
        // Handle location updates here
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String message = "Latitude: " + latitude + "\nLongitude: " + longitude;
      //  Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }
}
