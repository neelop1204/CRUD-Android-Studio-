package com.example.assignment03;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OMDBApi {
    @GET("/")
    Call<MovieResponse> searchMovies(
            @Query("s") String query,
            @Query("apikey") String apiKey
    );
}
