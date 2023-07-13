package com.example.smartbin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;
import java.util.Random;

public class LoginPage extends AppCompatActivity {

    int uniqueid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random= new Random();
                int randomNumber = random.nextInt(1000000);
                Toast.makeText(LoginPage.this, uniqueid, Toast.LENGTH_SHORT).show();
            }
        });
    }
}