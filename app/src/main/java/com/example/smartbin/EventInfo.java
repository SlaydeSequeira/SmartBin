package com.example.smartbin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Objects;

public class EventInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Intent intent = getIntent();
        String A = intent.getStringExtra("a");
        String B = intent.getStringExtra("b");
        String C = intent.getStringExtra("c");
        String D = intent.getStringExtra("d");
        ImageView i= findViewById(R.id.image);
        Glide.with(EventInfo.this)
                .load(D)
                .placeholder(R.drawable.coolimg)
                .error(R.drawable.coolimg)
                .into(i);
        TextView t1,t2,t3;
        t1=findViewById(R.id.text1);
        t2=findViewById(R.id.text2);
        t3=findViewById(R.id.text3);
        t1.setText(A);
        t2.setText(B);
        t3.setText(C);
    }
}