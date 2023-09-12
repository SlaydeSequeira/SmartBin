package com.example.smartbin.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.example.smartbin.R;
import com.example.smartbin.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class pic extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pic, container, false);
        ImageView imageView = view.findViewById(R.id.profile_image2);
        FirebaseUser fuser;
        RelativeLayout r = view.findViewById(R.id.rel);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if the fragment is attached to an activity before using requireContext()
        if (isAdded()) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MyUsers")
                    .child(fuser.getUid());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null && isAdded()) { // Check if the fragment is still attached
                        if ("default".equals(user.getImageURL())) {
                            imageView.setImageResource(R.mipmap.ic_launcher);
                        } else {
                            Glide.with(requireContext()).load(user.getImageURL()).into(imageView);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle onCancelled
                }
            });
        }

        return view;
    }
}
