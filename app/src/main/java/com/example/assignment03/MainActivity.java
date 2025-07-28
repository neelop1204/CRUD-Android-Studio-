package com.example.assignment03;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuthController;
    private List<Movie> movieList = new ArrayList<>();
    private MovieAdapter movieAdapter;
    private RequestQueue requestQueue;
    private static final String OMDB_API_URL = "https://www.omdbapi.com/";
    private static final String OMDB_API_KEY = "159d7044"; // Your API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        firebaseAuthController = FirebaseAuth.getInstance();

        // Check if the user is authenticated
        FirebaseUser currentUser = firebaseAuthController.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to continue", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, UserAccessLoginActivity.class));
            finish();
            return;
        }

        // Setup RequestQueue for API calls
        requestQueue = Volley.newRequestQueue(this);

        // Setup RecyclerView
        RecyclerView moviesRecyclerView = findViewById(R.id.moviesRecyclerView);
        moviesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(movieList, this::openMovieDetails);
        moviesRecyclerView.setAdapter(movieAdapter);

        // Setup Search Button
        Button searchButton = findViewById(R.id.searchButton);
        EditText searchBar = findViewById(R.id.searchBar);

        searchButton.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim();
            if (!TextUtils.isEmpty(query)) {
                searchMovies(query);
            } else {
                Toast.makeText(this, "Enter a search query", Toast.LENGTH_SHORT).show();
            }
        });

        // Favorites Button
        Button favoritesButton = findViewById(R.id.favoritesButton);
        favoritesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        // Logout Button
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            firebaseAuthController.signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, UserAccessLoginActivity.class));
            finish();
        });
    }

    private void searchMovies(String query) {
        String url = OMDB_API_URL + "?s=" + query + "&apikey=" + OMDB_API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.optString("Response", "False").equals("True")) {
                            JSONArray searchResults = response.optJSONArray("Search");
                            if (searchResults != null) {
                                movieList.clear();
                                for (int i = 0; i < searchResults.length(); i++) {
                                    JSONObject movieObject = searchResults.getJSONObject(i);
                                    String movieId = movieObject.optString("imdbID", "");
                                    fetchMovieDetails(movieId); // Fetch detailed info for each movie
                                }
                            }
                        } else {
                            Toast.makeText(this, "No movies found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing search results", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to fetch search results", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    private void fetchMovieDetails(String movieId) {
        if (TextUtils.isEmpty(movieId)) return;

        String url = OMDB_API_URL + "?i=" + movieId + "&apikey=" + OMDB_API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.optString("Response", "False").equals("True")) {
                            String title = response.optString("Title", "N/A");
                            String thumbnailUrl = response.optString("Poster", "");
                            String studio = response.optString("Production", "Unknown");
                            float criticsRating = response.has("imdbRating") && !response.getString("imdbRating").equals("N/A")
                                    ? Float.parseFloat(response.getString("imdbRating"))
                                    : 0;
                            String description = response.optString("Plot", "No description available");

                            // Add movie to the list
                            Movie movie = new Movie(thumbnailUrl, title, studio, criticsRating, description);
                            movieList.add(movie);
                            movieAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error fetching movie details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to fetch movie details", Toast.LENGTH_SHORT).show()
        );

        requestQueue.add(request);
    }

    private void openMovieDetails(Movie movie) {
        // Open movie details activity
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("posterUrl", movie.getThumbnailUrl());
        intent.putExtra("title", movie.getTitle());
        intent.putExtra("description", movie.getDescription());
        startActivity(intent);
    }
}
