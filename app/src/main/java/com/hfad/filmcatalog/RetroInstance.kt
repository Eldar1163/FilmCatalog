package com.hfad.filmcatalog

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetroInstance {
    companion object {
        val baseURL: String = "https://api.themoviedb.org/3/"
    }

    fun getRetroInstance(): Retrofit {
        return  Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}