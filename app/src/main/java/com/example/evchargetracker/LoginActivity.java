package com.example.evchargetracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    //elementos del view
    private EditText editEmail, editPassword;
    private Button btnLogin, btnRegister;
    private TextView txtForgotPassword;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in
        preferences = getSharedPreferences("EVChargeTracker", MODE_PRIVATE);

        if (isUserLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        initializeViews();
        setupClickListeners();
    }
}