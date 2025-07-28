package com.example.assignment03;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView moviePosterImageView;
    private TextView movieTitleTextView, movieDescriptionTextView;
    private Button backButton, addToFavoritesButton;

    private FirebaseFirestore firestore;
    private CollectionReference favoritesCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Bind UI elements
        moviePosterImageView = findViewById(R.id.fullMoviePoster);
        movieTitleTextView = findViewById(R.id.fullMovieTitle);
        movieDescriptionTextView = findViewById(R.id.fullMovieDescription);
        backButton = findViewById(R.id.movieDetailsBackButton);
        addToFavoritesButton = findViewById(R.id.movieDetailsAddToFavButton);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();
        favoritesCollection = firestore.collection("favorites");

        // Get data from intent
        Intent intent = getIntent();
        String posterUrl = intent.getStringExtra("posterUrl");
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");

        // Populate UI with movie details
        movieTitleTextView.setText(title != null && !title.isEmpty() ? title : "Title not available");
        movieDescriptionTextView.setText(description != null && !description.isEmpty() ? description : "Description not available");

        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this)
                    .load(posterUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_close_clear_cancel)
                    .into(moviePosterImageView);
        } else {
            moviePosterImageView.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Handle back button click
        backButton.setOnClickListener(v -> finish());

        // Handle add to favorites button click
        addToFavoritesButton.setOnClickListener(v -> {
            if (title == null || title.isEmpty()) {
                Toast.makeText(this, "Cannot add movie without a title!", Toast.LENGTH_SHORT).show();
                return;
            }
            addToFavorites(new Movie(posterUrl, title, "Unknown Studio", 0, description));
        });
    }

    private void addToFavorites(Movie movie) {
        // Check if movie title is already in the database (optional, for duplicate prevention)
        favoritesCollection.whereEqualTo("title", movie.getTitle())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, movie.getTitle() + " is already in favorites!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Save movie to Firestore
                        favoritesCollection.add(movie)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(this, movie.getTitle() + " added to favorites!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to add to favorites. Please try again.", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error checking favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
