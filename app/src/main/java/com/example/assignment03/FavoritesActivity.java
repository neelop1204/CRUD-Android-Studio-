package com.example.assignment03;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private MovieAdapter movieAdapter;
    private List<Movie> favoriteMovies;
    private FirebaseFirestore db;
    private CollectionReference favoritesCollection;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Initialize RecyclerView
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoriteMovies = new ArrayList<>();
        movieAdapter = new MovieAdapter(favoriteMovies, this::showMovieOptionsDialog);
        favoritesRecyclerView.setAdapter(movieAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        favoritesCollection = db.collection("favorites");

        // Back Button
        backButton = findViewById(R.id.favoritesBackButton);
        backButton.setOnClickListener(v -> finish());

        // Fetch favorite movies
        fetchFavoriteMovies();
    }

    private void fetchFavoriteMovies() {
        favoritesCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                favoriteMovies.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Movie movie = document.toObject(Movie.class);
                    movie.setId(document.getId()); // Save document ID for updates/deletion
                    favoriteMovies.add(movie);
                }
                movieAdapter.notifyDataSetChanged();
                if (favoriteMovies.isEmpty()) {
                    Toast.makeText(this, "No favorite movies found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to load favorites.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMovieOptionsDialog(Movie movie) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_movie_options, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Bind UI elements in the dialog
        ImageView posterImageView = dialogView.findViewById(R.id.dialogPosterImageView);
        TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        EditText descriptionEditText = dialogView.findViewById(R.id.dialogDescriptionEditText);
        Button updateButton = dialogView.findViewById(R.id.dialogUpdateButton);
        Button deleteButton = dialogView.findViewById(R.id.dialogDeleteButton);

        // Populate dialog with movie details
        Glide.with(this)
                .load(movie.getThumbnailUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(posterImageView);

        titleTextView.setText(movie.getTitle());
        descriptionEditText.setText(movie.getDescription());

        AlertDialog dialog = builder.create();

        // Handle update button click
        updateButton.setOnClickListener(v -> {
            String updatedDescription = descriptionEditText.getText().toString().trim();
            if (!updatedDescription.isEmpty()) {
                updateMovieDescription(movie, updatedDescription, dialog);
            } else {
                Toast.makeText(this, "Description cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle delete button click
        deleteButton.setOnClickListener(v -> deleteMovie(movie, dialog));

        dialog.show();
    }

    private void updateMovieDescription(Movie movie, String updatedDescription, AlertDialog dialog) {
        favoritesCollection.document(movie.getId())
                .update("description", updatedDescription)
                .addOnSuccessListener(aVoid -> {
                    movie.setDescription(updatedDescription);
                    movieAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Description updated.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update description.", Toast.LENGTH_SHORT).show());
    }

    private void deleteMovie(Movie movie, AlertDialog dialog) {
        favoritesCollection.document(movie.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    favoriteMovies.remove(movie);
                    movieAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Movie deleted from favorites.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete movie.", Toast.LENGTH_SHORT).show());
    }
}
