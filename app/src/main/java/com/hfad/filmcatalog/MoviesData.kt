package com.hfad.filmcatalog

data class MoviesData(val page: Int,
                 val results: List<Movie>,
                 val total_results: Int,
                 val total_pages: Int)
