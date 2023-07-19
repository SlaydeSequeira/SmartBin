package com.example.smartbin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.smartbin.adapter.RecyclerAdapter;

public class AccountHistory extends AppCompatActivity {

    String[] Titles = new String[100];
    String[] Description = new String[100];
    String[] Image = new String[100];
    String[] Author = new String[100];
    int count;
    RecyclerView recyclerView;
    ImageView i1;;
    RecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_history);
        recyclerView = findViewById(R.id.recyclerView);
        i1 = findViewById(R.id.image);
        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AccountHistory.this,HomePage.class);
                startActivity(i);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(AccountHistory.this));
        count=12;
        for(int i=0;i<100;i++)
        {
            Titles[i]="Points Earned by Disposing Waste";
            Description[i]="+20 Pts";
            Image[i]="Trophy";
            Author[i]="Location:At Frcrce Dustbin";
        }
        adapter = new RecyclerAdapter(AccountHistory.this, Titles, count, Image, Description,Author);
        recyclerView.setAdapter(adapter);
    }
}