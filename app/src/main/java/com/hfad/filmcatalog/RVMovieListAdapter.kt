package com.hfad.filmcatalog

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import com.bumptech.glide.Glide
import io.realm.Realm
import io.realm.RealmConfiguration

class RVMovieListAdapter(val ctxt: Context, val movies: List<Movie>) : RecyclerView.Adapter<RVMovieListAdapter.RVMovieListHolder>() {
    class RVMovieListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val movie_poster: ImageView = itemView.findViewById(R.id.iv_movie_poster)
        val movie_title: TextView = itemView.findViewById(R.id.tv_movie_title)
        val movie_description: TextView = itemView.findViewById(R.id.tv_movie_description)
        val movie_release_date: TextView = itemView.findViewById(R.id.tv_movie_release_date)
        val movie_favourite_btn : ToggleButton = itemView.findViewById(R.id.ib_movie_favourite)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RVMovieListHolder {
        val view = LayoutInflater.from(ctxt).inflate(R.layout.rv_item_layout, p0, false)
        return RVMovieListHolder(view)
    }

    override fun onBindViewHolder(p0: RVMovieListHolder, p1: Int) {
        p0.itemView.setOnClickListener {
            Toast.makeText(ctxt, movies[p1].title, Toast.LENGTH_SHORT).show()
        }
        p0.movie_title.text = movies[p1].title
        p0.movie_description.text = movies[p1].overview
        p0.movie_release_date.text = movies[p1].release_date

        val poster_path = "https://image.tmdb.org/t/p/w500${movies[p1].poster_path}"
        Glide.with(ctxt)
            .load(poster_path)
            .into(p0.movie_poster)


        Realm.init(ctxt)
        val config = RealmConfiguration.Builder()
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .build()
        var realm = Realm.getInstance(config)
        realm.beginTransaction()

        var movie = realm.where(FavouriteMovie::class.java)
            .equalTo("id", movies[p1].id).findFirst()

        var favourite_state: Boolean?
        favourite_state = movie?.isFavourite

        realm.commitTransaction()

        p0.movie_favourite_btn.isChecked = if (favourite_state != null) favourite_state else false

        p0.movie_favourite_btn.setOnClickListener {
            realm.beginTransaction()
            if (movie == null) {
                movie = realm.createObject(FavouriteMovie::class.java)
                (movie as FavouriteMovie).id = movies[p1].id
                (movie as FavouriteMovie).isFavourite = true
            } else {
                (movie as FavouriteMovie).isFavourite = p0.movie_favourite_btn.isChecked
            }
            realm.commitTransaction()
        }
    }

    override fun getItemCount(): Int {
        return movies.size
    }
}