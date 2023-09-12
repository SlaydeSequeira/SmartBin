package com.example.smartbin.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartbin.R;
import com.example.smartbin.adapter.DumpTruckAdapter;
import com.example.smartbin.adapter.EventAdapter;


public class EventFragment extends Fragment {
    RecyclerView recyclerView;
    String[] text1= new String[100];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view= inflater.inflate(R.layout.fragment_event, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        // Array of random numbers
        String[] randomNumbers = {"2","3","6","10"};

// Array of environment cleaning activities
        String[] cleaningActivities = {
                "Beach Cleanup",
                "Community Recycling",
                "Tree Planting",
                "River Cleanup"
        };

// Array of descriptions for each activity
        String[] activityDescriptions = {
                "Join us for a beach cleanup event and help protect marine life by removing trash from our shores.",
                "Participate in our community recycling program to reduce waste and promote recycling in your neighborhood.",
                "Join our tree planting initiative and contribute to a greener environment by planting trees in local parks.",
                "Volunteer for a river cleanup to preserve the health of our rivers and protect wildlife habitats."
        };

// Array of image URLs corresponding to each activity
        String[] imageUrls = {
                "https://www.sandals.com/blog/content/images/2019/06/saunders-beach.jpg",
                "https://th.bing.com/th/id/OIP.1UDPJzS-EEQnVj3b8SwHGwHaE8?w=290&h=193&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "https://th.bing.com/th/id/OIP.F64-83D9hp-98NWKeNcWDgHaFj?w=232&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7",
                "https://th.bing.com/th/id/OIP.5KzL_KlUO1MFd22ZwHcgaQHaE8?w=286&h=191&c=7&r=0&o=5&dpr=1.3&pid=1.7"
        };

        EventAdapter eventAdapter = new EventAdapter(getActivity(), cleaningActivities, 4, imageUrls, activityDescriptions, randomNumbers);
        recyclerView.setAdapter(eventAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        return  view;
    }
}