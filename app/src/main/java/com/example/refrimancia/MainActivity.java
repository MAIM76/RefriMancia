package com.example.refrimancia;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvForgotPassword, tvCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);

        tvForgotPassword.setPaintFlags(tvForgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvCreateAccount.setPaintFlags(tvCreateAccount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // 345: Temporary code for testing. Replace with actual implementation.
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExampleActivity.class);
            startActivity(intent);
        });

        // 345: Temporary code for testing. Replace with actual implementation.
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExampleActivity.class);
            startActivity(intent);
        });

        // 345: Temporary code for testing. Replace with actual implementation.
        tvCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExampleActivity.class);
            startActivity(intent);
        });
    }
}
