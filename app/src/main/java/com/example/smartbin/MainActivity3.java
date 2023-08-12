package com.example.smartbin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.smartbin.R;
import com.example.smartbin.adapter.BinAdapter;
import com.example.smartbin.adapter.RecyclerAdapter;

public class MainActivity3 extends AppCompatActivity {
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        recyclerView = findViewById(R.id.recyclerView1);
        String idk[]= new String[100];
        idk[0]=idk[1]=idk[2]="1";
        BinAdapter binAdapter = new BinAdapter(this,idk,3,idk,idk,idk);
        recyclerView.setAdapter(binAdapter);
    }
}