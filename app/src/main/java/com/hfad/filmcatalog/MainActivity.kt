package com.hfad.filmcatalog

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    companion object {
        val API_KEY: String = "6ccd72a2a8fc239b13f209408fc31c33"
        val SEARCH_QUERY: String = "search_query"
        val WAS_LAUNCH: String = "was_launch"
    }

    private lateinit var movieViewModel: MoviesViewModel
    private lateinit var rvMovieList: RecyclerView
    private lateinit var srl_reload: SwipeRefreshLayout
    private lateinit var progressBarOnReload: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var iv_error: ImageView
    private lateinit var tv_error: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.sv_search_movies)
        searchView.setOnQueryTextListener(this)
        val query: String? = savedInstanceState?.getString(SEARCH_QUERY)
        searchView.setQuery(query,false)

        iv_error = findViewById(R.id.iv_error)
        tv_error = findViewById(R.id.tv_error)

        srl_reload = findViewById(R.id.srl_reload)
        srl_reload.setOnRefreshListener(this)

        progressBarOnReload = findViewById(R.id.progress_bar_on_reloading)

        val progressBar : ProgressBar = findViewById(R.id.progress_on_loading)

         if (savedInstanceState?.getBoolean(WAS_LAUNCH) == null)
             progressBar.visibility = View.VISIBLE

        rvMovieList = findViewById(R.id.rv_movies_list)
        rvMovieList.layoutManager = LinearLayoutManager(this)

        movieViewModel = ViewModelProviders.of(this).get(MoviesViewModel::class.java)
        if (searchView.query.toString() == "") {
            movieViewModel.getMovies().observe(this, Observer<List<Movie>>() {
                progressBar.visibility = View.GONE

                if (it != null) {
                    rvMovieList.adapter = RVMovieListAdapter(this, it)
                } else {
                    rvMovieList.adapter = RVMovieListAdapter(this, listOf<Movie>())
                    Snackbar.make(
                        rvMovieList,
                        getString(R.string.internet_connection_error_string),
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                }
            })
        } else {
            movieViewModel.getMoviesByQuery(searchView.query.toString()).observe(this, Observer<List<Movie>>() {
                if (it != null && it.size > 0)  {
                    rvMovieList.adapter = RVMovieListAdapter(this, it)
                }
                else if (it != null && it.size == 0) {
                    iv_error.setImageDrawable(getDrawable(R.drawable.big_search))
                    tv_error.text = "По запросу \"${searchView.query.toString()}\" ничего не найдено"
                    iv_error.visibility = View.VISIBLE
                    tv_error.visibility = View.VISIBLE
                    rvMovieList.adapter = RVMovieListAdapter(this, listOf<Movie>())
                }
                else if (it == null){
                    rvMovieList.adapter = RVMovieListAdapter(this, listOf<Movie>())
                    Snackbar.make(rvMovieList, getString(R.string.internet_connection_error_string), Snackbar.LENGTH_LONG)
                        .show()
                }
            })
        }
    }


    override fun onRefresh() {
        iv_error.visibility = View.GONE
        tv_error.visibility = View.GONE
        srl_reload.isRefreshing = false
        progressBarOnReload.visibility = View.VISIBLE

        if (searchView.query.toString() == "")
            reloadFilmList()
        else
            onQueryTextSubmit(searchView.query.toString())
    }

    fun reloadFilmList() {
        movieViewModel.movieList = null
        movieViewModel.getMovies().observe(this, Observer<List<Movie>>() {
            if (it != null) {
                rvMovieList.adapter = RVMovieListAdapter(this, it)
            }
            else {
                rvMovieList.adapter = RVMovieListAdapter(this, listOf<Movie>())
                Snackbar.make(rvMovieList, getString(R.string.internet_connection_error_string), Snackbar.LENGTH_LONG)
                    .show()
            }

            progressBarOnReload.visibility = View.GONE
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null)
        {
            iv_error.visibility = View.GONE
            tv_error.visibility = View.GONE
            progressBarOnReload.visibility = View.VISIBLE
            movieViewModel.filteredMovieList = null
            movieViewModel.getMoviesByQuery(query).observe(this, Observer<List<Movie>>() {
                if (it != null && it.size > 0)  {
                    rvMovieList.adapter = RVMovieListAdapter(this, it)
                }
                else if (it != null && it.size == 0) {
                    iv_error.setImageDrawable(getDrawable(R.drawable.big_search))
                    tv_error.text = "По запросу \"${searchView.query.toString()}\" ничего не найдено"
                    iv_error.visibility = View.VISIBLE
                    tv_error.visibility = View.VISIBLE
                    rvMovieList.adapter = RVMovieListAdapter(this, listOf<Movie>())
                }
                else if (it == null){
                    Snackbar.make(rvMovieList, getString(R.string.internet_connection_error_string), Snackbar.LENGTH_LONG)
                        .show()
                }
                progressBarOnReload.visibility = View.GONE
            })
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY, searchView.query.toString())
        outState.putBoolean(WAS_LAUNCH, true)
    }
}