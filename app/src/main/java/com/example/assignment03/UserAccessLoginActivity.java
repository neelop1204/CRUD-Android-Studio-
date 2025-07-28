package com.example.assignment03;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.TextView;
import android.content.Intent;

public class UserAccessLoginActivity extends AppCompatActivity {

    private EditText loginEmailInputField, loginPasswordInputField;
    private Button initiateLoginProcessButton;
    private TextView navigateToRegistrationScreenLink;

    private FirebaseAuth firebaseAuthController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        firebaseAuthController = FirebaseAuth.getInstance();

        // Check if the user is already logged in
        if (firebaseAuthController.getCurrentUser() != null) {
            // User is logged in, navigate to MainActivity
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_user_access_login);

        // Bind Views
        loginEmailInputField = findViewById(R.id.loginEmailInputField);
        loginPasswordInputField = findViewById(R.id.loginPasswordInputField);
        initiateLoginProcessButton = findViewById(R.id.initiateLoginProcessButton);
        navigateToRegistrationScreenLink = findViewById(R.id.navigateToRegistrationScreenLink);

        // Handle Login
        initiateLoginProcessButton.setOnClickListener(v -> {
            String email = loginEmailInputField.getText().toString().trim();
            String password = loginPasswordInputField.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                firebaseAuthController.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigate to Registration
        navigateToRegistrationScreenLink.setOnClickListener(v -> {
            startActivity(new Intent(this, NewUserRegistrationActivity.class));
        });
    }
}
