package com.example.smartbin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        int tint = Color.parseColor("#081DA8");
        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(tint, PorterDuff.Mode.SRC_IN);
        button.getBackground().setColorFilter(colorFilter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginPage.class);
                startActivity(i);
            }
        });
    }
}