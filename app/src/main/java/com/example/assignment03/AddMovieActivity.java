package com.example.assignment03;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddMovieActivity extends AppCompatActivity {

    private EditText thumbnailUrlInput, movieTitleInput, studioInput, criticsRatingInput;
    private Button saveMovieButton, cancelMovieButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        // Bind UI elements
        thumbnailUrlInput = findViewById(R.id.thumbnailUrlInput);
        movieTitleInput = findViewById(R.id.movieTitleInput);
        studioInput = findViewById(R.id.studioInput);
        criticsRatingInput = findViewById(R.id.criticsRatingInput);
        saveMovieButton = findViewById(R.id.saveMovieButton);
        cancelMovieButton = findViewById(R.id.cancelMovieButton);

        // Set listeners for buttons
        saveMovieButton.setOnClickListener(v -> saveMovie());
        cancelMovieButton.setOnClickListener(v -> cancelAddMovie());
    }

    private void saveMovie() {
        // Get user inputs
        String thumbnailUrl = thumbnailUrlInput.getText().toString().trim();
        String movieTitle = movieTitleInput.getText().toString().trim();
        String studio = studioInput.getText().toString().trim();
        String criticsRatingStr = criticsRatingInput.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(thumbnailUrl) || TextUtils.isEmpty(movieTitle) ||
                TextUtils.isEmpty(studio) || TextUtils.isEmpty(criticsRatingStr)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        float criticsRating;
        try {
            criticsRating = Float.parseFloat(criticsRatingStr);
            if (criticsRating < 0 || criticsRating > 10) {
                Toast.makeText(this, "Critics Rating must be between 0 and 10", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid Critics Rating", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pass movie details back to MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("thumbnailUrl", thumbnailUrl);
        resultIntent.putExtra("movieTitle", movieTitle);
        resultIntent.putExtra("studio", studio);
        resultIntent.putExtra("criticsRating", criticsRating);
        setResult(RESULT_OK, resultIntent);
        finish(); // Close AddMovieActivity
    }

    private void cancelAddMovie() {
        // Simply finish the activity
        Toast.makeText(this, "Cancelled adding movie", Toast.LENGTH_SHORT).show();
        finish();
    }
}
