package com.example.smartbin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class EventInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        Intent intent = getIntent();
        int count = intent.getIntExtra("swiped",0);
        String c=String.valueOf(count);
        Toast.makeText(EventInfo.this,c,Toast.LENGTH_SHORT).show();
    }
}