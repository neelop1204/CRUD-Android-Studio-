package com.example.assignment03;

import java.io.Serializable;

public class Movie implements Serializable {
    private String id; // Firestore document ID
    private String thumbnailUrl;
    private String title;
    private String studio;
    private float criticsRating;
    private String description;

    // No-argument constructor for Firestore
    public Movie() {
    }

    // Constructor
    public Movie(String thumbnailUrl, String title, String studio, float criticsRating, String description) {
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.studio = studio;
        this.criticsRating = criticsRating;
        this.description = description;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudio() {
        return studio;
    }

    public void setStudio(String studio) {
        this.studio = studio;
    }

    public float getCriticsRating() {
        return criticsRating;
    }

    public void setCriticsRating(float criticsRating) {
        this.criticsRating = criticsRating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ToString for debugging
    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", title='" + title + '\'' +
                ", studio='" + studio + '\'' +
                ", criticsRating=" + criticsRating +
                ", description='" + description + '\'' +
                '}';
    }
}
