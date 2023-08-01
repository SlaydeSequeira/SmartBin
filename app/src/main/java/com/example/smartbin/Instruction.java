package com.example.smartbin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.smartbin.adapter.ImageSliderAdapter;

import java.util.Objects;

public class Instruction extends AppCompatActivity {
    private int[] images = {R.drawable.i1, R.drawable.i2, R.drawable.i3, R.drawable.i4, R.drawable.i5};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        Objects.requireNonNull(getSupportActionBar()).hide();
        ImageView i1= findViewById(R.id.back);
        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        ImageSliderAdapter adapter = new ImageSliderAdapter(Instruction.this, images);
        viewPager.setAdapter(adapter);
    }
}