package com.example.assignment03;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private OnMovieActionListener movieActionListener;

    // Constructor
    public MovieAdapter(List<Movie> movieList, OnMovieActionListener movieActionListener) {
        this.movieList = movieList;
        this.movieActionListener = movieActionListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        // Set movie title
        holder.titleTextView.setText(movie.getTitle() != null ? movie.getTitle() : "Untitled");

        // Load movie thumbnail using Glide
        Glide.with(holder.thumbnailImageView.getContext())
                .load(movie.getThumbnailUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(android.R.drawable.ic_menu_gallery) // Default placeholder
                .error(android.R.drawable.ic_menu_close_clear_cancel) // Error image
                .into(holder.thumbnailImageView);

        // Set click listener to trigger the action listener
        holder.itemView.setOnClickListener(v -> movieActionListener.onMovieSelected(movie));
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void updateMovieList(List<Movie> newMovies) {
        this.movieList.clear();
        this.movieList.addAll(newMovies);
        notifyDataSetChanged();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView thumbnailImageView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.movieTitle);
            thumbnailImageView = itemView.findViewById(R.id.movieThumbnail);
        }
    }

    // Interface for movie actions
    public interface OnMovieActionListener {
        void onMovieSelected(Movie movie);
    }
}
