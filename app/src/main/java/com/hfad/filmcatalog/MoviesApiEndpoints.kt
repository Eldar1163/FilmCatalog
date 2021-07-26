package com.hfad.filmcatalog

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApiEndpoints {

    @GET("discover/movie")
    fun getMoviesFromApi(@Query("api_key") api_key: String,
                         @Query("language") lang: String = "ru-RU"): Call<MoviesData>

    @GET("search/movie")
    fun getFilteredMoviesFromApi(@Query("api_key") api_key: String,
                                 @Query("query") query: String,
                                 @Query("language") lang: String = "ru-RU"): Call<MoviesData>
}