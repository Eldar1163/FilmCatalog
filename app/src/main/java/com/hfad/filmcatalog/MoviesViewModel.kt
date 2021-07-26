package com.hfad.filmcatalog

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.v7.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MoviesViewModel : ViewModel() {
    var movieList: MutableLiveData<List<Movie>>? = null
    var filteredMovieList: MutableLiveData<List<Movie>>? = null

    fun getMovies(): LiveData<List<Movie>> {
        if (movieList == null) {
            movieList = MutableLiveData<List<Movie>>()

            loadMovies()
        }

        return movieList as MutableLiveData<List<Movie>>
    }

    fun getMoviesByQuery(query: String): LiveData<List<Movie>> {
        if (filteredMovieList == null) {
            filteredMovieList = MutableLiveData<List<Movie>>()

            loadFilteredMovies(query)
        }

        return filteredMovieList as MutableLiveData<List<Movie>>
    }

    fun loadMovies() {
        val retrofit = RetroInstance().getRetroInstance()
        val api = retrofit.create(MoviesApiEndpoints::class.java)
        val call: Call<MoviesData> = api.getMoviesFromApi(MainActivity.API_KEY)

        call.enqueue(object : Callback<MoviesData> {
            override fun onResponse(call: Call<MoviesData>, response: Response<MoviesData>) {
                movieList?.value = (response.body() as MoviesData).results
            }

            override fun onFailure(call: Call<MoviesData>, t: Throwable) {
                movieList?.value = null
            }

        })
    }

    fun loadFilteredMovies(query: String) {
        val retrofit = RetroInstance().getRetroInstance()
        val api = retrofit.create(MoviesApiEndpoints::class.java)
        val call: Call<MoviesData> = api.getFilteredMoviesFromApi(MainActivity.API_KEY, query)

        call.enqueue(object : Callback<MoviesData> {
            override fun onResponse(call: Call<MoviesData>, response: Response<MoviesData>) {
                filteredMovieList?.value = (response.body() as MoviesData).results
            }

            override fun onFailure(call: Call<MoviesData>, t: Throwable) {
                filteredMovieList?.value = null
            }

        })
    }
}