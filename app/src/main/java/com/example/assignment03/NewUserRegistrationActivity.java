package com.example.assignment03;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;


public class NewUserRegistrationActivity extends AppCompatActivity {

    private EditText registrationEmailInputField, registrationPasswordInputField;
    private Button completeUserRegistrationButton, cancelUserRegistrationButton;

    private FirebaseAuth firebaseAuthController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_registration);

        // Initialize Firebase Auth
        firebaseAuthController = FirebaseAuth.getInstance();

        // Bind Views
        registrationEmailInputField = findViewById(R.id.registrationEmailInputField);
        registrationPasswordInputField = findViewById(R.id.registrationPasswordInputField);
        completeUserRegistrationButton = findViewById(R.id.completeUserRegistrationButton);
        cancelUserRegistrationButton = findViewById(R.id.cancelUserRegistrationButton);

        // Handle Registration
        completeUserRegistrationButton.setOnClickListener(v -> {
            String email = registrationEmailInputField.getText().toString().trim();
            String password = registrationPasswordInputField.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                firebaseAuthController.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                finish(); // Navigate back to LoginActivity
                            } else {
                                Toast.makeText(this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancel Registration
        cancelUserRegistrationButton.setOnClickListener(v -> {
            finish(); // Navigate back to LoginActivity
        });
    }
}

