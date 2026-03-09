package com.example.refrimancia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// 345: This entire activity is for testing purposes and can be removed.
public class ExampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            // Updated to lead back to LoginActivity
            Intent intent = new Intent(ExampleActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
